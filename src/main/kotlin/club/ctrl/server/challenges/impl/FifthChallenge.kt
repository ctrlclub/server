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
import java.util.Random


@Serializable
data class AnswerSet(val userId: String, val map: String, val total: Int, val remaining: Int, val median: Double, val numBasins: Int)

object FifthChallenge : Challenge {
    override val name: String = "Team: The Great Flood \uD83C\uDF0A"
    override val id: Int = 4
    override val subchallenges: List<Subchallenge> = listOf(FifthChallengeP0, FifthChallengeP1, FifthChallengeP2, FifthChallengeP3)
    override val isTeamChallenge: Boolean = true;
}

object FifthChallengeP0 : Subchallenge {
    override val parent: Challenge = FifthChallenge
    override val subchallengeIdx: Int = 0

    const val sizeX = 24;
    const val sizeY = 24;

    override fun loadMarkdown(userId: String, db: ChallengeCollection): String {
        val dataset = findSerializable<AnswerSet>(db) {
            Filters.eq("userId", userId)
        } ?: return "No dataset was generated for this challenge"

        var content = loadLocalContent() ?: return "No subchallenge content found"

        content = content.fillPlaceholders(
            "map" to dataset.map,
        )

        return content
    }

    override fun onFirstOpen(userId: String, db: ChallengeCollection) {
        val rand = Random()
        val grid = Array(sizeY) { Array(sizeX) { rand.nextInt(0, 100 + 1) } } // needs storing
        val total = grid.sumOf { it.sum() } // to store
        val remaining = sizeX * sizeY * 100 - total // to store
        val sortFlat = grid.flatten().sorted(); // to store
        val median: Double = if (sortFlat.size % 2 == 1) {
            sortFlat[sortFlat.size / 2].toDouble()
        } else {
            (sortFlat[sortFlat.size / 2 - 1] + sortFlat[sortFlat.size / 2]) / 2.0
        }

        val basins = findBasins(grid)
        val numBasins = basins.size // to store

        val dataset = AnswerSet(userId, formatMap(grid), total, remaining, median, numBasins)
        upsertSerializable(db, dataset) {
            Filters.eq("userId", userId)
        }
    }

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        submission.toIntOrNull() ?: return SubmissionFeedback(false, "The answer must be an integer")
        val int = submission.toInt()

        val dataset = findSerializable<AnswerSet>(db) {
            Filters.eq("userId", userId)
        } ?: return SubmissionFeedback(false, "No dataset generated")

        if(dataset.total == int) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "Incorrect answer. Your answer was too ${if(int > dataset.total) { "high" } else { "low" }}")
    }
}

object FifthChallengeP1 : Subchallenge {
    override val parent: Challenge = FifthChallenge
    override val subchallengeIdx: Int = 1

    override fun loadMarkdown(userId: String, db: ChallengeCollection): String {
        val dataset = findSerializable<AnswerSet>(db) {
            Filters.eq("userId", userId)
        } ?: return "No dataset was generated for this challenge"

        var content = loadLocalContent() ?: return "No subchallenge content found"

        content = content.fillPlaceholders(
            "lastanswer" to dataset.total.toString()
        )

        return content
    }

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit // subchallenge 0 inits all data

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        submission.toIntOrNull() ?: return SubmissionFeedback(false, "The answer must be an integer")
        val int = submission.toInt()

        val dataset = findSerializable<AnswerSet>(db) {
            Filters.eq("userId", userId)
        } ?: return SubmissionFeedback(false, "No dataset generated")

        if(dataset.remaining == int) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "Incorrect answer. Your answer was too ${if(int > dataset.remaining) { "high" } else { "low" }}")
    }
}

object FifthChallengeP2 : Subchallenge {
    override val parent: Challenge = FifthChallenge
    override val subchallengeIdx: Int = 2

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit // subchallenge 0 inits all data

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        submission.toDoubleOrNull() ?: return SubmissionFeedback(false, "The answer must be an decimal number")
        val answer = submission.toDouble()

        val dataset = findSerializable<AnswerSet>(db) {
            Filters.eq("userId", userId)
        } ?: return SubmissionFeedback(false, "No dataset generated")

        if(dataset.median == answer) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "Incorrect answer")
    }
}

object FifthChallengeP3 : Subchallenge {
    override val parent: Challenge = FifthChallenge
    override val subchallengeIdx: Int = 3

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit // subchallenge 0 inits all data

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        submission.toIntOrNull() ?: return SubmissionFeedback(false, "The answer must be an integer")
        val int = submission.toInt()

        val dataset = findSerializable<AnswerSet>(db) {
            Filters.eq("userId", userId)
        } ?: return SubmissionFeedback(false, "No dataset generated")

        if(dataset.numBasins == int) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "Incorrect answer. Your answer was too ${if(int > dataset.numBasins) { "high" } else { "low" }}")
    }
}


fun findBasins(grid: Array<Array<Int>>): List<Pair<Int, Int>> {
    val height = grid.size
    val width = grid[0].size
    val basins = mutableListOf<Pair<Int, Int>>()

    for (y in 0 until height) {
        for (x in 0 until width) {
            val value = grid[y][x]
            var isMin = true

            // check up
            if (y > 0 && grid[y - 1][x] <= value) isMin = false
            // check down
            if (y < height - 1 && grid[y + 1][x] <= value) isMin = false
            // check left
            if (x > 0 && grid[y][x - 1] <= value) isMin = false
            // check right
            if (x < width - 1 && grid[y][x + 1] <= value) isMin = false

            if (isMin) basins.add(Pair(x, y))
        }
    }

    return basins
}

fun formatMap(grid: Array<Array<Int>>): String {
    val formattedGrid = grid.map { row ->
        "    [${row.joinToString(", ")}]"
    }
    return "[\n${formattedGrid.joinToString(",\n")}\n]"
}