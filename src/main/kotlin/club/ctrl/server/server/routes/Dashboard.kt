package club.ctrl.server.server.routes

import club.ctrl.server.database.CHALLENGES
import club.ctrl.server.database.ChallengeEntry
import club.ctrl.server.database.SUBMISSIONS
import club.ctrl.server.database.VIEWS
import club.ctrl.server.database.getChallenges
import club.ctrl.server.database.updateChallenges
import club.ctrl.server.entity.respondError
import club.ctrl.server.entity.respondSuccess
import club.ctrl.server.server.UserIdKey
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
data class ClearSubchallengeProgress(val userId: String, val challengeId: Int, val subchallengeId: Int)

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
