package club.ctrl.server.database

import at.favre.lib.crypto.bcrypt.BCrypt
import club.ctrl.server.entity.ResponseWrapper
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.eq
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document
import java.security.SecureRandom
import java.util.Base64

const val CREDENTIALS = "credentials"
const val SESSIONS = "sessions"

@Serializable
data class CredentialObject(val email: String, val passwordHash: String, val permissionLevel: Int)

@Serializable
data class SessionObject(val email: String, val token: String, val createdAt: Long)



/*
   - High-level login / logout functions
*/

fun login(email: String, password: String, db: MongoDatabase): ResponseWrapper<SessionObject> {
    // find their email in the database
    val document: Document = db.getCollection(CREDENTIALS)
        .find(eq("email", email))
        .first() ?: return ResponseWrapper(false, "Unknown email")

    // retrieve their credential obj including password hash
    val credentialObject: CredentialObject = document.let {
        it.remove("_id")
        val json = it.toJson()
        Json.decodeFromString<CredentialObject>(json)
    }

    // test if their password hashed matches our stored password hash
    val result = BCrypt.verifyer().verify(password.toCharArray(), credentialObject.passwordHash);

    // if the password was correct
    if(result.verified) {
        // delete all old sessions by email
        clearAllSessionsByEmail(email, db)

        // add session, get token
        val session = addSession(email, db)

        // in this specific case, errorReason is used to store the new token
        return ResponseWrapper(true, data = session)
    }

    // the password was not correct here
    return ResponseWrapper(false, "Invalid access code")
}

// logs a user out with the given token, attempts to remove all sessions by email as well
fun logout(token: String, db: MongoDatabase) {
    // find their email and clear any sessions matching that email
    val potentialEmail = tokenToEmail(token, db)
    potentialEmail?.let { clearAllSessionsByEmail(it, db) }

    // clear all tokens by the argument provided (should only be 1)
    clearAllSessionsByToken(token, db)
}





/*
   - Session functions
*/

// get the email (userid) from their token
fun tokenToEmail(sessionToken: String, db: MongoDatabase): String? {
    println("CHECKING!")
    println(sessionToken)
    val result = db.getCollection(SESSIONS)
        .find(eq("token", sessionToken))
        .first()
    ?: return null

    return result["email"] as String?
}

// clear all sessions given an email
fun clearAllSessionsByEmail(email: String, db: MongoDatabase) {
    db.getCollection(SESSIONS).deleteMany(eq("email", email))
}

// clear all sessions given a session token
fun clearAllSessionsByToken(token: String, db: MongoDatabase) {
    db.getCollection(SESSIONS).deleteMany(eq("token", token))
}

// generate token, create SessionObject + json encode for type safety, insert to db
fun addSession(email: String, db: MongoDatabase): SessionObject {
    val token = genSessionToken()
    val session = SessionObject(email, token, System.currentTimeMillis().floorDiv(1000))

    // prepare it for the database
    val json = Json.encodeToString(session)
    val doc = Document.parse(json)

    db.getCollection(SESSIONS).insertOne(doc)

    return session
}

// get if user is priviledged or not
fun isPrivileged(userId: String, db: MongoDatabase): Boolean {
    val result = db.getCollection(CREDENTIALS)
        .find(eq("email", userId))
        .first()
        ?: return false

    return result["permissionLevel"] == 0
}




/*
   - Misc functions
*/

private fun genSessionToken(): String {
    val random = SecureRandom()
    val bytes = ByteArray(64)
    random.nextBytes(bytes)
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)!!
}