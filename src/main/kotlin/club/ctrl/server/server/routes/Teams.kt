package club.ctrl.server.server.routes

import club.ctrl.server.database.Team
import club.ctrl.server.database.getUserTeam
import club.ctrl.server.database.registerUserInTeam
import club.ctrl.server.database.removeUserInTeam
import club.ctrl.server.extensions.respondError
import club.ctrl.server.extensions.respondSuccess
import club.ctrl.server.server.UserIdKey
import com.mongodb.client.MongoDatabase
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable

@Serializable
data class MyTeam(val userIds: List<String>, val teamId: Int, val owner: String, val isOwner: Boolean)

@Serializable
data class RegisterTeam(val teamCode: Int)

fun Route.teamsRoutes(db: MongoDatabase) {
    get("/myteam") {
        val userId = call.attributes[UserIdKey];
        val team = getUserTeam(userId, db)

        if(team == null) {
            call.respondError("Not in team")
        } else {
            val myTeam = MyTeam(team.userIds, team.teamId, team.owner, userId == team.owner)
            call.respondSuccess(myTeam)
        }
    }

    post("/register") {
        val userId = call.attributes[UserIdKey];
        val payload: RegisterTeam;

        try {
            payload = call.receive<RegisterTeam>()
        } catch(ex: ContentTransformationException) {
            call.respondError("Invalid submission format: $ex")
            return@post
        }

        val team: Team?;
        try {
            team = registerUserInTeam(userId, payload.teamCode, db)
        } catch (ex: Exception) {
            println(ex)
            call.respondError("hi")
            return@post
        }
        if(team == null) {
            call.respondError("No team found with that code")
        } else {
            call.respondSuccess(Unit)
        }
    }

    get("/leave") {
        val userId = call.attributes[UserIdKey];

        removeUserInTeam(userId, db)

        call.respondSuccess(Unit)
    }
}
