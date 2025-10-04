package club.ctrl.server.server.routes

import club.ctrl.server.database.CHALLENGES
import club.ctrl.server.database.ChallengeEntry
import club.ctrl.server.database.getChallenges
import club.ctrl.server.database.updateChallenges
import club.ctrl.server.entity.respondError
import club.ctrl.server.entity.respondSuccess
import club.ctrl.server.server.UserIdKey
import com.mongodb.client.MongoDatabase
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post


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
}
