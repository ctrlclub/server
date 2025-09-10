package club.ctrl.server.server.routes

import club.ctrl.server.challenges.ChallengeManager
import club.ctrl.server.database.getChallengeMeta
import club.ctrl.server.entity.respondSuccess
import club.ctrl.server.server.UserIdKey
import com.mongodb.client.MongoDatabase
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable

@Serializable
data class ChallengeListing(val challengeId: Int, val numSubchallenges: Int, val completedSubchallenges: Int, val unlocked: Boolean, val name: String)


fun Route.challengesRoute(db: MongoDatabase) {
    get("/list") {
        val userId = call.attributes.get(UserIdKey)!! // injected by authenticating middleware

        val challengeListingBuilder: MutableList<ChallengeListing> = mutableListOf()

        val challengeMeta = getChallengeMeta(db)
        for(challenge in ChallengeManager.challenges) {
            if(!challengeMeta.containsKey(challenge.id)) continue // ignore all challenges not referenced in database

            val visible_unlocked = challengeMeta[challenge.id]!!
            if(!visible_unlocked.first) continue // if challenge is not visible, don't send to frontend

            challengeListingBuilder.add(ChallengeListing(challenge.id, challenge.subchallenges.size, 1, visible_unlocked.second, challenge.name))
        }

        call.respondSuccess(challengeListingBuilder)
    }
}
