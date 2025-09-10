package club.ctrl.server.entity

import kotlinx.serialization.Serializable
import io.ktor.server.response.respond
import io.ktor.server.application.ApplicationCall

// so this class is basically like Result<T> in rust, but even cooler cuz its directly serializes to web requests
// either you have a { success: false, error: "Error msg here" }
// or you have a { success: true, data: { obj } }
// and this can be consistently handled on the frontend. wowee, magical!

@Serializable
data class ResponseWrapper<T>(
    val success: Boolean,
    val errorReason: String? = null,
    val data: T? = null
)

// public fun <T> respondSuccess(data: T) = ResponseWrapper(success = true, data = data)
// public fun <T> respondError(reason: String) = ResponseWrapper<T>(success = false, errorReason = reason)

// extension functions for ApplicationCall
suspend inline fun <reified T> ApplicationCall.respondSuccess(data: T) {
    respond(ResponseWrapper(success = true, data = data))
}

suspend fun ApplicationCall.respondError(reason: String) {
    respond(ResponseWrapper<Nothing>(success = false, errorReason = reason))
}
