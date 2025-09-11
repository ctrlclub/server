package club.ctrl.server.database

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document


const val VIEWS = "views"

@Serializable
data class View(val userId: String, val challengeId: Int, val subchallengeId: Int)


// return true if the item was added, else false
fun addViewIfNotExists(userId: String, challengeId: Int, subchallengeId: Int, db: MongoDatabase): Boolean {
    val filter = Filters.and(Filters.eq("userId", userId), Filters.eq("challengeId", challengeId), Filters.eq("subchallengeId", subchallengeId))
    val doesExist = !(db.getCollection(VIEWS).find(filter).toList().isEmpty())

    if(doesExist) {
        return false
    }

    val newView = View(userId, challengeId, subchallengeId)

    // prepare it for the database
    val json = Json.encodeToString(newView)
    val doc = Document.parse(json)

    db.getCollection(VIEWS).insertOne(doc)

    return true
}

// returns true if the user has already viewed the subchallenge
fun hasViewed(userId: String, challengeId: Int, subchallengeId: Int, db: MongoDatabase): Boolean {
    val viewed = getViewedSubchallenges(userId, challengeId, db)
    return !viewed.none { it.subchallengeId == subchallengeId }
}

// returns null if they've never open the challenge, or the last subchallenge id opened
fun getLastViewed(userId: String, challengeId: Int, db: MongoDatabase): Int? {
    val views = getViewedSubchallenges(userId, challengeId, db)
    if(views.isEmpty()) {
        return null;
    }

    return views.last().subchallengeId;
}

// returns a list of views performed by the user, sorted by subchallenge id
private fun getViewedSubchallenges(userId: String, challengeId: Int, db: MongoDatabase): List<View> {
    val filter = Filters.and(Filters.eq("userId", userId), Filters.eq("challengeId", challengeId))
    val results = db.getCollection(VIEWS).find(filter)

    val views = mutableListOf<View>()

    for(doc in results.filterNotNull()) {
        doc.remove("_id")
        val json = doc.toJson()
        views.add(Json.decodeFromString<View>(json))
    }

    views.sortBy { it.subchallengeId }
    return views
}
