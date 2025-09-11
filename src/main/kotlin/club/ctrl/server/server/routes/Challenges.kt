package club.ctrl.server.server.routes

import club.ctrl.server.challenges.ChallengeManager
import club.ctrl.server.database.getChallengeMeta
import club.ctrl.server.database.getChallengeSubmissions
import club.ctrl.server.database.getChallengeUnlocked
import club.ctrl.server.database.getLastViewed
import club.ctrl.server.database.getWorkingAt
import club.ctrl.server.database.hasViewed
import club.ctrl.server.entity.respondError
import club.ctrl.server.entity.respondSuccess
import club.ctrl.server.server.UserIdKey
import com.mongodb.client.MongoDatabase
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable

@Serializable
data class ChallengeListing(val challengeId: Int, val numSubchallenges: Int, val completedSubchallenges: Int, val unlocked: Boolean, val name: String)

@Serializable
data class SubchallengeContent(val subchallengeId: Int, val content: String, val completed: Boolean, val answer: String?)


fun Route.challengesRoute(db: MongoDatabase) {
    get("/list") {
        val userId = call.attributes.get(UserIdKey)!! // injected by authenticating middleware

        val challengeListingBuilder: MutableList<ChallengeListing> = mutableListOf()

        val challengeMeta = getChallengeMeta(db)
        for(challenge in ChallengeManager.challenges) {
            if(!challengeMeta.containsKey(challenge.id)) continue // ignore all challenges not referenced in database

            val visible_unlocked = challengeMeta[challenge.id]!!
            if(!visible_unlocked.first) continue // if challenge is not visible, don't send to frontend

            challengeListingBuilder.add(ChallengeListing(challenge.id, challenge.subchallenges.size, 1, visible_unlocked.second, challenge.name))
        }

        call.respondSuccess(challengeListingBuilder)
    }

    get("{id}") {
        val userId = call.attributes[UserIdKey]

        val idParameter = call.parameters["id"]
        if(idParameter == null) {
            call.respondError("No challenge ID provided")
            return@get
        }

        val urlId = idParameter.toIntOrNull(10)
        if(urlId == null) {
            call.respondError("The challenge ID provided was not a number")
            return@get
        }

        val id = urlId - 1
        val challengeObj = ChallengeManager.challenges.getOrNull(id)
        if(challengeObj == null) {
            call.respondError("No challenge was found with the provided ID")
            return@get
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
                ChallengeManager.challenges[id].subchallenges[workingAt].onFirstOpen(userId)
            }

            // add the contents for the working at subchallenge
            subchallengeContents.add(SubchallengeContent(
                workingAt, // subchallenge id
                ChallengeManager.challenges[id].subchallenges[workingAt].loadMarkdown(userId), // md content
                false, // completed? is false cuz they're "workingAt" it. ha. ha. ok not funny
                null // no completed answer ofc
            ))


        }

        // then here we collate everything into the obj to send to the frontend
        val submissions = getChallengeSubmissions(userId, id, db)
        for(sub in submissions.reversed()) {
            subchallengeContents.add(SubchallengeContent(
                sub.subchallengeId,
                ChallengeManager.challenges[id].subchallenges[sub.subchallengeId].loadMarkdown(userId),
                true,
                sub.answer
            ))
        }

        call.respondSuccess(subchallengeContents.reversed())
    }
}
