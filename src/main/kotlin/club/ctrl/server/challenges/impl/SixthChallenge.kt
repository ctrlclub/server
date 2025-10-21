package club.ctrl.server.challenges.impl

import club.ctrl.server.challenges.Challenge
import club.ctrl.server.challenges.ChallengeCollection
import club.ctrl.server.challenges.Subchallenge
import club.ctrl.server.extensions.fillPlaceholders
import club.ctrl.server.extensions.findSerializable
import club.ctrl.server.extensions.upsertSerializable
import club.ctrl.server.server.routes.SubmissionFeedback
import com.mongodb.client.model.Filters
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlin.random.Random


@Serializable
data class SixthChallengeDataset(val userId: String, val players: String, val playersWithArmour: String, val totalRankTwo: Int, val leaderboardHighestOne: String, val leaderboardHighestTwo: String)

@Serializable
data class FakePlayer(val name: String, val strength: Int, val defence: Int, val stamina: Int, val armour: List<String>)
val defenseBonus = mapOf(
    "chestplate" to 15,
    "helmet" to 10,
    "gauntlets" to 5,
    "shield" to 15
)

object SixthChallenge : Challenge {
    override val name: String = "Medium: Quest of Legends"
    override val id: Int = 5
    override val subchallenges: List<Subchallenge> = listOf(SixthChallengeP0, SixthChallengeP1, SixthChallengeP2)
    override val isTeamChallenge: Boolean = false;
}

object SixthChallengeP0 : Subchallenge {
    override val parent: Challenge = SixthChallenge
    override val subchallengeIdx: Int = 0

    override fun onFirstOpen(userId: String, db: ChallengeCollection) {
        val names = GamerNameGenerator.generateNames(40)
        val players = mutableListOf<FakePlayer>()


        names.forEach {
            players.add(FakePlayer(
                it,
                Random.nextInt(1,80),
                Random.nextInt(1, 70),
                Random.nextInt(1, 50),
                listOf("chestplate", "helmet", "gauntlets", "shield").shuffled().take(Random.nextInt(0, 5))
            ))
        }


        val numRank2 = players.filter { it.strength >= 30 && it.defence >= 10 && it.stamina >= 30 }.size

        val preDefenceScore = players.map { p ->
            val score = 0.5 * p.strength + 0.3 * p.defence + 0.2 * p.stamina
            p to score
        }
        val highestPreDefence = preDefenceScore.maxByOrNull { it.second }!!.first.name

        val postDefenceScore = players.map { p ->
            val defence = p.armour.sumOf { defenseBonus[it] ?: 0 }
            val score = 0.5 * p.strength + 0.3 * (p.defence + defence) + 0.2 * p.stamina
            p to score
        }
        val highestPostDefence = postDefenceScore.maxByOrNull { it.second }!!.first.name

        val formattedNoArmour = "[\n    " +
                (players.joinToString(separator = ",\n    ") { player ->
                    Json.encodeToString(NoArmourSerializer, player)
                }) + "\n]"
        val formatted = "[\n    " +
                (players.joinToString(separator = ",\n    ") { player ->
                    Json.encodeToString(player)
                }) + "\n]"

        val dataset = SixthChallengeDataset(userId, formattedNoArmour, formatted, numRank2, highestPreDefence, highestPostDefence)

        upsertSerializable(db, dataset) {
            Filters.eq("userId", userId)
        }
    }

    override fun loadMarkdown(userId: String, db: ChallengeCollection): String {
        val dataset = findSerializable<SixthChallengeDataset>(db) {
            Filters.eq("userId", userId)
        } ?: return "No dataset was generated for this challenge"

        var content = loadLocalContent() ?: return "No subchallenge content found"

        content = content.fillPlaceholders(
            "players" to dataset.players
        )

        return content
    }

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        submission.toIntOrNull() ?: return SubmissionFeedback(false, "The answer must be an integer")
        val int = submission.toInt()

        val dataset = findSerializable<SixthChallengeDataset>(db) {
            Filters.eq("userId", userId)
        } ?: return SubmissionFeedback(false, "No dataset generated")

        if(dataset.totalRankTwo == int) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "Incorrect answer. Your answer was too ${if(int > dataset.totalRankTwo) { "high" } else { "low" }}")
    }
}

object SixthChallengeP1 : Subchallenge {
    override val parent: Challenge = SixthChallenge
    override val subchallengeIdx: Int = 1

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit // all data init by sc 0

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        val dataset = findSerializable<SixthChallengeDataset>(db) {
            Filters.eq("userId", userId)
        } ?: return SubmissionFeedback(false, "No dataset generated")

        if(dataset.leaderboardHighestOne.equals(submission, ignoreCase = true)) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "That wasn't the right player...")
    }
}

object SixthChallengeP2 : Subchallenge {
    override val parent: Challenge = SixthChallenge
    override val subchallengeIdx: Int = 2

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit // all data init by sc 0

    override fun loadMarkdown(userId: String, db: ChallengeCollection): String {
        val dataset = findSerializable<SixthChallengeDataset>(db) {
            Filters.eq("userId", userId)
        } ?: return "No dataset was generated for this challenge"

        var content = loadLocalContent() ?: return "No subchallenge content found"

        content = content.fillPlaceholders(
            "players" to dataset.playersWithArmour
        )

        return content
    }

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        val dataset = findSerializable<SixthChallengeDataset>(db) {
            Filters.eq("userId", userId)
        } ?: return SubmissionFeedback(false, "No dataset generated")

        if(dataset.leaderboardHighestTwo.equals(submission, ignoreCase = true)) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "That wasn't the right player...")
    }
}






// thanks gpt
object GamerNameGenerator {
    private val adjectives = listOf(
        "Savage", "Crimson", "Silent", "Stealthy", "Shadow", "Nuclear", "Cyber",
        "Rogue", "Toxic", "Feral", "Elite", "Viral", "Ghostly", "Atomic", "Frost",
        "Dark", "Venomous", "Solar", "Neon", "Abyssal", "Quantum", "Iron", "Infernal"
    )

    private val nouns = listOf(
        "Viper", "Hunter", "Sniper", "Reaper", "Assassin", "Rider", "Wolf",
        "Drake", "Phantom", "Slayer", "Warden", "Ninja", "Titan", "Knight",
        "Specter", "Beast", "Dragon", "Predator", "Havoc", "Crusader"
    )

    private val verbs = listOf(
        "Destroyer", "Breaker", "Killer", "Slasher", "Crusher", "Runner", "Wrecker"
    )

    private val prefixes = listOf(
        "xX", "0", "The", "Ultra", "Mega", "i", "Pro", "Dark", "Neo", "Captain", "Dr"
    )

    private val suffixes = listOf(
        "Xx", "_YT", "_TV", "420", "69", "_LOL", "1337", "_OG", "HD", "V2"
    )

    private val numbers = (1..9999).map { it.toString() }

    private val schemaOptions: List<() -> String> = listOf(
        { "${random(adjectives)}${random(nouns)}" },
        { "${random(adjectives)}${random(nouns)}${random(numbers)}" },
        { "${random(prefixes)}${random(adjectives)}${random(nouns)}${random(suffixes)}" },
        { "${random(nouns)}${random(numbers)}" },
        { "${random(nouns)}${random(verbs)}" },
        { "${random(adjectives)}_${random(nouns)}" },
        { "${random(prefixes)}${random(nouns)}${random(suffixes)}" },
        { "${random(adjectives)}${random(nouns)}_${random(numbers)}" },
        { "${random(nouns)}_${random(suffixes)}" },
        { "${random(prefixes)}${random(nouns)}${random(verbs)}" }
    )

    private fun <T> random(list: List<T>): T = list[Random.nextInt(list.size)]

    fun generateNames(count: Int = 20): List<String> {
        return (1..count).map {
            val schema = random(schemaOptions)
            val name = schema()
            maybeStylize(name)
        }.distinct()
    }

    private fun maybeStylize(name: String): String {
        return when (Random.nextInt(5)) {
            0 -> name.uppercase()
            1 -> name.lowercase()
            2 -> l33tify(name)
            3 -> name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            else -> name
        }
    }

    private fun l33tify(text: String): String {
        val map = mapOf(
            'a' to '4', 'e' to '3', 'i' to '1', 'o' to '0', 's' to '5', 't' to '7'
        )
        return text.map { map[it.lowercaseChar()] ?: it }.joinToString("")
    }
}

// Transforming serializer to remove keys dynamically
object NoArmourSerializer : JsonTransformingSerializer<FakePlayer>(FakePlayer.serializer()) {
    override fun transformSerialize(element: JsonElement): JsonElement {
        // element is a JsonObject representing the player
        return if (element is JsonObject) {
            JsonObject(element.filterKeys { it != "armour" }) // remove "armour"
        } else element
    }
}