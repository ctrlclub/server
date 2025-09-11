package club.ctrl.server.database

import club.ctrl.server.challenges.ChallengeManager
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters

const val CHALLENGES = "challenges"

// returns challenge id <-> (visible, unlocked)
fun getChallengeMeta(db: MongoDatabase): Map<Int, Pair<Boolean, Boolean>> {
    val challengeDocs = db.getCollection(CHALLENGES).find()
    val metaList: MutableMap<Int, Pair<Boolean, Boolean>> = mutableMapOf()

    for(doc in challengeDocs) {
        if(!(
            doc.containsKey("challengeId") &&
            doc.containsKey("visible") &&
            doc.containsKey("unlocked")
        )) {
            error("Could not parse metadata on ChallengeMeta document (missing key): ${doc.toJson()}")
            continue;
        }

        metaList[doc.getInteger("challengeId")!!] = Pair(doc.getBoolean("visible")!!, doc.getBoolean("unlocked")!!)
    }

    return metaList
}

// return true if the challenge exists and it is unlocked
fun getChallengeUnlocked(challengeId: Int, db: MongoDatabase): Boolean {
    if(ChallengeManager.challenges.size <= challengeId) return false

    val filter = Filters.eq("challengeId", challengeId)
    val result = db.getCollection(CHALLENGES).find(filter).first()

    result ?: return false

    return result.getBoolean("unlocked", false) // default to false if it doesn't exist
}