package club.ctrl.server.server

import club.ctrl.server.database.tokenToEmail
import club.ctrl.server.entity.respondError
import com.mongodb.client.MongoDatabase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.response.respond
import io.ktor.util.AttributeKey
import kotlin.text.isNullOrBlank

// attribute key to grab user ID in request handlers
public val UserIdKey = AttributeKey<String>("userId")

// exception to abort a request
class AbortPipeline : RuntimeException()

// config for the plugin
class Config { var dbHandle: MongoDatabase? = null }

// ktor plugin to ensure valid sid is present in a request
// if a valid sid is not present, the plugin will throw an AbortPipeline error
// otherwise, the requesters email (userId) will be appended to the request attributes
public val SIDValidator = createRouteScopedPlugin(
    name = "CookieRequired",
    createConfiguration = ::Config
) {
    onCall { call ->
        val cfg = pluginConfig
        val sid = call.request.cookies["sid"]

        if(sid.isNullOrBlank()) {
            call.respondError("Missing auth token")
            throw AbortPipeline()
        }

        val emailLookup = tokenToEmail(sid, cfg.dbHandle!!)
        if(emailLookup == null) {
            call.respondError("Invalid auth token")
            throw AbortPipeline()
        }

        // if the email is valid we attach it to the call
        call.attributes.put(UserIdKey, emailLookup);
    }
}