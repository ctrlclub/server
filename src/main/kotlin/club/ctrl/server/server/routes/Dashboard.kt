package club.ctrl.server.server.routes

import club.ctrl.server.challenges.ChallengeManager
import club.ctrl.server.database.ChallengeEntry
import club.ctrl.server.database.SUBMISSIONS
import club.ctrl.server.database.VIEWS
import club.ctrl.server.database.clearTeams
import club.ctrl.server.database.getActiveTeamIds
import club.ctrl.server.database.getChallengeSubmissions
import club.ctrl.server.database.getChallenges
import club.ctrl.server.database.populateTeams
import club.ctrl.server.database.setPassword
import club.ctrl.server.database.updateChallenges
import club.ctrl.server.extensions.respondError
import club.ctrl.server.extensions.respondSuccess
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable


@Serializable
data class ClearChallengeProgress(val userId: String, val challengeId: Int)
@Serializable
data class ClearSubchallengeProgress(val userId: String, val challengeId: Int, val subchallengeId: Int)
@Serializable
data class ChangePassword(val userId: String, val newPassword: String)

@Serializable
data class GenTeamCodes(val number: Int)

@Serializable
data class LeaderboardTeamProgress(val teamName: String, val complete: Int, val total: Int)

fun Route.dashboardRoutes(db: MongoDatabase) {
    get("auth") {
        call.respondSuccess(Unit)
    }

    get("challenges") {
        call.respondSuccess(getChallenges(db).sortedBy { it.challengeId })
    }

    post("challenges") {
        val updates: List<ChallengeEntry>;

        try {
            updates = call.receive<List<ChallengeEntry>>()
        } catch(ex: ContentTransformationException) {
            call.respondError("Invalid submission format: $ex")
            return@post
        }

        updateChallenges(updates.sortedBy { it.challengeId }, db)

        call.respondSuccess(Unit)
    }

    post("reset-challenge") {
        val clear: ClearChallengeProgress;

        try {
            clear = call.receive<ClearChallengeProgress>()
        } catch(ex: ContentTransformationException) {
            call.respondError("Invalid submission format: $ex")
            return@post
        }

        resetChallenge(clear.userId, clear.challengeId, db)
        call.respondSuccess(Unit)
    }

    // not going to expose this on the frontend right now
    // problem is, the data potentially generated per-user for a challenge may not be deleted, and theres no handling for this
    post("clear-subchallenge") {
        val clear: ClearSubchallengeProgress;

        try {
            clear = call.receive<ClearSubchallengeProgress>()
        } catch(ex: ContentTransformationException) {
            call.respondError("Invalid submission format: $ex")
            return@post
        }

        resetSubchallenge(clear.userId, clear.challengeId, clear.subchallengeId, db)
        call.respondSuccess(Unit)
    }

    post("set-password") {
        val changePassword: ChangePassword;

        try {
            changePassword = call.receive<ChangePassword>()
        } catch(ex: ContentTransformationException) {
            call.respondError("Invalid submission format: $ex")
            return@post
        }

        setPassword(changePassword.userId, changePassword.newPassword, db)

        call.respondSuccess(Unit)
    }

    get("clear-teams") {
        clearTeams(db)

        call.respondSuccess(Unit)
    }

    post("gen-team-codes") {
        val payload: GenTeamCodes;

        try {
            payload = call.receive<GenTeamCodes>()
        } catch(ex: ContentTransformationException) {
            call.respondError("Invalid submission format: $ex")
            return@post
        }

        val codes = populateTeams(payload.number, db)
        call.respondSuccess(codes)
    }



    get("leaderboard/{id}") {
        val id = call.parameters["id"]!!.toIntOrNull()
        if(id == null) {
            call.respondError("Invalid challenge")
            return@get
        }

        val challenge = ChallengeManager.challenges.getOrNull(id)
        if(challenge == null) {
            call.respondError("Invalid challenge")
            return@get
        }
        if(!challenge.isTeamChallenge) {
            call.respondError("Leaderboard only available on team challenges")
            return@get
        }


        val teamsEmails = getActiveTeamIds(db)
        val progresses = mutableListOf<LeaderboardTeamProgress>()

        teamsEmails.forEach {
            progresses.add(
                LeaderboardTeamProgress(
                    "Team $it",
                    getChallengeSubmissions("internal_team$it@ctrl.club", challenge.id, db).size,
                    challenge.subchallenges.size
                )
            )
        }

        call.respondSuccess(progresses)
    }
}

fun resetSubchallenge(userId: String, challengeId: Int, subchallengeId: Int, db: MongoDatabase) {
    // need to clear views, submissions
    db.getCollection(VIEWS).deleteOne(Filters.and(Filters.eq("userId", userId), Filters.eq("challengeId", challengeId), Filters.eq("subchallengeId", subchallengeId)))
    db.getCollection(SUBMISSIONS).deleteOne(Filters.and(Filters.eq("userId", userId), Filters.eq("challengeId", challengeId), Filters.eq("subchallengeId", subchallengeId)))
}

fun resetChallenge(userId: String, challengeId: Int, db: MongoDatabase) {
    db.getCollection(VIEWS).deleteMany(Filters.and(Filters.eq("userId", userId), Filters.eq("challengeId", challengeId)))
    db.getCollection(SUBMISSIONS).deleteMany(Filters.and(Filters.eq("userId", userId), Filters.eq("challengeId", challengeId)))
    scopeChallengeCollection(challengeId, db).deleteMany(Filters.eq("userId", userId))
}
