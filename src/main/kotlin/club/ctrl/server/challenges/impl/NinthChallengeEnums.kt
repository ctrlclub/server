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
import kotlin.math.ceil
import kotlin.random.Random

@Serializable
data class NinthChallengeDataset(val userId: String, val logs: List<String>, val num401s: Int)


object NinthChallengeEnums : Challenge {
    override val name: String = "Easy: Enums"
    override val id: Int = 8
    override val subchallenges: List<Subchallenge> = listOf(NinthChallengeP0, NinthChallengeP1, NinthChallengeP2)
    override val isTeamChallenge: Boolean = false;
}

object NinthChallengeP0 : Subchallenge {
    override val parent: Challenge = NinthChallengeEnums
    override val subchallengeIdx: Int = 0

    override fun onFirstOpen(userId: String, db: ChallengeCollection) {
        val (logs, codeMap, numUnique401s) = generateLogs(60)
        val dataset = NinthChallengeDataset(userId, logs, numUnique401s)

        upsertSerializable(db, dataset) {
            Filters.eq("userId", userId)
        }
    }

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        return SubmissionFeedback(true, "Great. Let's put your understanding to use...")
    }
}

object NinthChallengeP1 : Subchallenge {
    override val parent: Challenge = NinthChallengeEnums
    override val subchallengeIdx: Int = 1

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit

    override fun loadMarkdown(userId: String, db: ChallengeCollection): String {
        val dataset = findSerializable<NinthChallengeDataset>(db) {
            Filters.eq("userId", userId)
        } ?: return "No dataset was generated for this challenge"

        var content = loadLocalContent() ?: return "No subchallenge content found"

        content = content.fillPlaceholders(
            "logs" to "\"${dataset.logs.joinToString("\", \"")}\""
        )

        return content
    }

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        return SubmissionFeedback(true, "Great. Let's put your understanding to use...")
    }
}

object NinthChallengeP2 : Subchallenge {
    override val parent: Challenge = NinthChallengeEnums
    override val subchallengeIdx: Int = 2

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        submission.toIntOrNull() ?: return SubmissionFeedback(false, "The answer must be an integer")
        val int = submission.toIntOrNull()

        val dataset = findSerializable<NinthChallengeDataset>(db) {
            Filters.eq("userId", userId)
        } ?: return SubmissionFeedback(false, "No dataset generated")

        if(int == dataset.num401s) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "That wasn't the right answer...")
    }
}





fun generateLogs(lineCount: Int): Triple<List<String>, Map<Int, Int>, Int> {
    val statusCodes = listOf(200, 401, 403, 404, 405)
    val uniqueIpCount = maxOf(1, ceil((lineCount.toFloat() / 3)).toInt())
    val ipPool = List(uniqueIpCount) {
        List(4) { Random.nextInt(0, 256) }.joinToString(".")
    }

    val logs = mutableListOf<String>()
    val counts = mutableMapOf(200 to 0, 401 to 0, 403 to 0, 404 to 0, 405 to 0)
    val ips401 = mutableSetOf<String>()

    repeat(lineCount) {
        val code = statusCodes.random()
        val ip = ipPool.random()
        logs += "$code - $ip"
        counts[code] = counts.getValue(code) + 1

        if(code == 401) {
            ips401.add(ip)
        }
    }

    return Triple(logs, counts, ips401.size)
}