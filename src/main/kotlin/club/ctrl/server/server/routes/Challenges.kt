package club.ctrl.server.server.routes

import club.ctrl.server.challenges.ChallengeManager
import club.ctrl.server.database.addViewIfNotExists
import club.ctrl.server.database.getChallengeMeta
import club.ctrl.server.database.getChallengeSubmissions
import club.ctrl.server.database.getChallengeUnlocked
import club.ctrl.server.database.getUserTeam
import club.ctrl.server.database.getWorkingAt
import club.ctrl.server.database.hasViewed
import club.ctrl.server.database.registerSubmission
import club.ctrl.server.extensions.respondError
import club.ctrl.server.extensions.respondSuccess
import club.ctrl.server.server.UserIdKey
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.bson.Document

@Serializable
data class ChallengeListing(val challengeId: Int, val numSubchallenges: Int, val completedSubchallenges: Int, val unlocked: Boolean, val name: String, val isTeamChallenge: Boolean)

@Serializable
data class SubchallengeContent(val subchallengeId: Int, val content: String, val completed: Boolean, val answer: String?)

@Serializable
data class ContentLoad(val challengeName: String, val totalSubchallenges: Int, val subchallenges: List<SubchallengeContent>, val isTeamChallenge: Boolean)

@Serializable
data class UserSubmission(val challengeId: Int, val subchallengeId: Int, val answer: String)

@Serializable
data class SubmissionFeedback(val correct: Boolean, val userFeedback: String)


fun Route.challengesRoute(db: MongoDatabase) {
    get("/list") {
        val userId = call.attributes[UserIdKey] // injected by authenticating middleware
        val team = getUserTeam(userId, db)

        val challengeListingBuilder: MutableList<ChallengeListing> = mutableListOf()

        val challengeMeta = getChallengeMeta(db)
        for(challenge in ChallengeManager.challenges) {
            if(!challengeMeta.containsKey(challenge.id)) continue // ignore all challenges not referenced in database

            val visibleUnlocked = challengeMeta[challenge.id]!!
            if(!visibleUnlocked.first) continue // if challenge is not visible, don't send to frontend

            // get the amount of subchallenges completed to show the correct stars
            val submissions = getChallengeSubmissions(if(challenge.isTeamChallenge && team != null) { "internal_team${team.teamId}@ctrl.club" } else { userId }, challenge.id, db)

            challengeListingBuilder.add(ChallengeListing(challenge.id, challenge.subchallenges.size, submissions.size, visibleUnlocked.second, challenge.name, challenge.isTeamChallenge))
        }

        call.respondSuccess(challengeListingBuilder)
    }

    get("{id}") {
        var userId = call.attributes[UserIdKey]

        val idParameter = call.parameters["id"]
        if(idParameter == null) {
            call.respondError("No challenge ID provided")
            return@get
        }

        val id = idParameter.toIntOrNull(10)
        if(id == null) {
            call.respondError("The challenge ID provided was not a number")
            return@get
        }

        val challengeObj = ChallengeManager.challenges.getOrNull(id)
        if(challengeObj == null) {
            call.respondError("No challenge was found with the provided ID")
            return@get
        }

        // internal userid -> team transformer
        if(challengeObj.isTeamChallenge) {
            val team = getUserTeam(userId, db);
            team ?: call.respondError("You must be in a team to enter this challenge.")
            userId = "internal_team${team!!.teamId}@ctrl.club"
        }

        val unlocked = getChallengeUnlocked(id, db);
        if(!unlocked) {
            call.respondError("No challenge was found with the provided ID")
            return@get
        }

        val subchallengeContents = mutableListOf<SubchallengeContent>()

        val workingAt = getWorkingAt(userId, id, db) // working at subchallenge id
        if(workingAt != null) { // if they still have subchallenges to complete
            val viewed = hasViewed(userId, id, workingAt, db)
            if(!viewed) {
                // first we get the subchallenge to init anything it needs to
                try {
                    ChallengeManager.challenges[id].subchallenges[workingAt].onFirstOpen(
                        userId,
                        scopeChallengeCollection(challengeObj.id, db)
                    )
                } catch(ex: Exception) {
                    error("Error generating user dataset: $ex")
                }
            }

            // add the contents for the working at subchallenge
            subchallengeContents.add(SubchallengeContent(
                workingAt, // subchallenge id
                ChallengeManager.challenges[id].subchallenges[workingAt].loadMarkdown(userId, scopeChallengeCollection(challengeObj.id, db)), // md content
                false, // completed? is false cuz they're "workingAt" it. ha. ha. ok not funny
                null // no completed answer ofc
            ))

            addViewIfNotExists(userId, id, workingAt, db) // add their view to the view collection
        }

        // then here we collate everything into the obj to send to the frontend
        val submissions = getChallengeSubmissions(userId, id, db)
        for(sub in submissions.reversed()) {
            subchallengeContents.add(SubchallengeContent(
                sub.subchallengeId,
                ChallengeManager.challenges[id].subchallenges[sub.subchallengeId].loadMarkdown(userId, scopeChallengeCollection(challengeObj.id, db)),
                true,
                sub.answer
            ))
        }

        val content = ContentLoad(subchallenges = subchallengeContents.reversed(), challengeName = challengeObj.name, totalSubchallenges = challengeObj.subchallenges.size, isTeamChallenge = challengeObj.isTeamChallenge)
        call.respondSuccess(content)
    }

    post("/submit") {
        var userId = call.attributes[UserIdKey]
        val submission: UserSubmission;

        try {
            submission = call.receive<UserSubmission>()
        } catch(ex: ContentTransformationException) {
            call.respondError("Invalid submission format: $ex")
            return@post
        }

        if(ChallengeManager.challenges.size <= submission.challengeId || ChallengeManager.challenges.getOrNull(submission.challengeId) == null) {
            call.respondError("Unknown challenge (unknown id)")
            return@post
        }

        val challengeObj = ChallengeManager.challenges[submission.challengeId];


        if(challengeObj.subchallenges.size <= submission.subchallengeId) {
            call.respondError("Unknown subchallenge (unknown id)")
            return@post
        }

        val subchallengeObj = challengeObj.subchallenges[submission.subchallengeId]

        // internal userid -> team transformer
        if(challengeObj.isTeamChallenge) {
            val team = getUserTeam(userId, db);
            team ?: call.respondError("You must be in a team to enter this challenge.")
            if(team!!.owner != userId) call.respondError("You must be the team leader to answer this question.")
            userId = "internal_team${team.teamId}@ctrl.club"
        }

        if(getChallengeSubmissions(userId, submission.challengeId, db).any { it.subchallengeId == submission.subchallengeId }) {
            call.respondError("You have already completed this challenge")
            return@post
        }

        println("$userId is currently working at ${getWorkingAt(userId, submission.challengeId, db)}")
        if(getWorkingAt(userId, submission.challengeId, db) != submission.subchallengeId) {
            call.respondError("You haven't unlocked this subchallenge yet")
            return@post
        }

        // asserted the following: challenge exists, subchallenge exists, user hasn't done subchallenge yet, user is working at subchallenge
        // so now we can check if the actual answer is correct

        val feedback = subchallengeObj.onSubmit(userId, submission.answer, scopeChallengeCollection(challengeObj.id, db))

        if(feedback.correct) {
            registerSubmission(submission.challengeId, submission.subchallengeId, userId, submission.answer, db)
        }

        call.respondSuccess(feedback)
    }
}

fun scopeChallengeCollection(challengeId: Int, db: MongoDatabase): MongoCollection<Document> = db.getCollection("challengecollection_${challengeId}")