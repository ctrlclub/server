package club.ctrl.server.database

import com.mongodb.client.MongoDatabase

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
