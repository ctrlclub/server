package club.ctrl.server.database

import club.ctrl.server.challenges.ChallengeManager
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.Document


const val SUBMISSIONS = "submissions"

@Serializable
data class Submission(val challengeId: Int, val subchallengeId: Int, val userId: String, val answer: String, val completedAt: Long)

// returns null if all challenges are completed, or the subchallenge id they are working at
fun getWorkingAt(userId: String, challengeId: Int, db: MongoDatabase): Int? {
    if(ChallengeManager.challenges.getOrNull(challengeId) == null) return null;
    val submissions = getChallengeSubmissions(userId, challengeId, db)

    if(submissions.size == ChallengeManager.challenges[challengeId].subchallenges.size) {
        return null;
    }

    if(submissions.isEmpty()) return 0;
    return submissions.last().subchallengeId + 1;
}

// returns all submissions on a challenge, ordered by subchallenge id
fun getChallengeSubmissions(userId: String, challengeId: Int, db: MongoDatabase): List<Submission> {
    val filter = Filters.and(Filters.eq("userId", userId), Filters.eq("challengeId", challengeId))
    val databaseResult = db.getCollection(SUBMISSIONS).find(filter)

    val results = databaseResult.toList().filterNotNull()

    return results.map {
        it.remove("_id")
        val json = it.toJson()
        Json.decodeFromString<Submission>(json)
    }.toList().sortedBy { it.subchallengeId }
}


// adds a correct submission to the database, returns a string if there was an error
fun registerSubmission(challengeId: Int, subchallengeId: Int, userId: String, answer: String, db: MongoDatabase) {

    val unixSecondsNow = System.currentTimeMillis().floorDiv(1000);
    val submission = Submission(challengeId, subchallengeId, userId, answer, unixSecondsNow)

    // yeah its a bit backwards its for type safety
    val json = Json.encodeToString(submission)
    val document = Document.parse(json)

    db.getCollection(SUBMISSIONS).insertOne(document)
}