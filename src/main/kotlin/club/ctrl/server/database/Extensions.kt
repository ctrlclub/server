package club.ctrl.server.database

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.conversions.Bson

inline fun <reified T : Any> upsertSerializable(
    collection: MongoCollection<Document>,
    value: T,
    filterBuilder: (T) -> Bson
) {
    // serialize Kotlin object -> BSON Document
    val json = Json.encodeToString(value)
    val doc = Document.parse(json)

    // build update from document fields
    val updates = doc.entries.map { Updates.setOnInsert(it.key, it.value) }
    val updateCombined = Updates.combine(updates)

    // build filter from callback
    val filter: Bson = filterBuilder(value)
    val options = UpdateOptions().upsert(true)
    collection.updateOne(filter, updateCombined, options)
}

inline fun <reified T : Any> findSerializable(
    collection: MongoCollection<Document>,
    filterBuilder: () -> Bson
): T? {
    val filter = filterBuilder()
    val doc = collection.find(filter).firstOrNull() ?: return null
    doc.remove("_id")
    return Json.decodeFromString(doc.toJson())
}