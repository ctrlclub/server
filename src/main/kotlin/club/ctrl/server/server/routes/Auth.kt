package club.ctrl.server.server.routes

import club.ctrl.server.extensions.respondError
import club.ctrl.server.extensions.respondSuccess
import club.ctrl.server.database.login
import club.ctrl.server.database.logout
import com.mongodb.client.MongoDatabase
import io.ktor.http.Cookie
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable


@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class LoggedInResponse(val isLoggedIn: Boolean)


fun Route.authenticationRoutes(db: MongoDatabase) {
    post("/login") {
        val data = call.receive<LoginRequest>();

        val loginAttempt = login(data.email.lowercase(), data.password, db)
        if(loginAttempt.success) {
            call.response.cookies.append(
                Cookie(
                    name = "sid",
                    value = loginAttempt.data!!.token, // in this specific case, its used to store the token
                    path = "/",
                    httpOnly = true,
                    secure = true,
                    extensions = mapOf("SameSite" to "None")
                )
            )

            call.respondSuccess(Unit);
            return@post
        }

        call.respondError("Failed to login: ${loginAttempt.errorReason}");
    }


    get("/logout") {
        // if they do have an sid cookie, attempt to log them out
        val sid = call.request.cookies["sid"]
        if(sid == null) {
            return@get
        }
        logout(sid, db)


        // give them a cookie that expires now, to cancel any cookie that may be there
        call.response.cookies.append(
            name = "sid",
            value = "",
            maxAge = 0,
            path = "/",
            httpOnly = true,
            secure = true,
            extensions = mapOf("SameSite" to "None")
        )

        call.respondSuccess(Unit);
    }


    get("/testsession") {
        val sid = call.request.cookies["sid"]
        val data = LoggedInResponse(isLoggedIn = sid != null)

        call.respondSuccess(data);
    }
}
