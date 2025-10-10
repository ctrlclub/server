package club.ctrl.server.challenges.impl

import club.ctrl.server.challenges.Challenge
import club.ctrl.server.challenges.ChallengeCollection
import club.ctrl.server.challenges.Subchallenge
import club.ctrl.server.server.routes.SubmissionFeedback
import com.mongodb.client.model.Filters
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document
import javax.xml.crypto.Data

@Serializable
data class Explorer(val name: String, val coord: Coord, val resources: Int);

@Serializable
data class Coord(val x: Int, val y: Int)

@Serializable
data class DatabaseEntry(
    val userId: String,
    val firstExplorers: List<Explorer>,
    val secondExplorers: List<Explorer>,
    val grid: List<List<Int>>,
    val lowestExplorerName: String,
    val sumCoordinates: Int,
    val totalSurvived: Int
)


object ThirdChallenge : Challenge {
    override val name: String = "Hard: Rescue Protocol"
    override val id: Int = 2
    override val subchallenges: List<Subchallenge> = listOf(ThirdChallengeP0, ThirdChallengeP1, ThirdChallengeP2)
    override val isTeamChallenge: Boolean = false;
}

object ThirdChallengeP0 : Subchallenge {
    override val parent: Challenge = ThirdChallenge
    override val subchallengeIdx: Int = 0

    override fun onFirstOpen(userId: String, db: ChallengeCollection) {
        val mapSizeX = 30;
        val mapSizeY = 30;
        val numExplorers = 5;

        val explorers: MutableList<Explorer> = mutableListOf() // needs storing
        for(i in 0 until numExplorers) {
            explorers.add(createExplorer(mapSizeX - 1, mapSizeY - 1, explorers) ?: return)
        }

        val rand = java.util.Random()
        val grid = Array(mapSizeY) { Array(mapSizeX) { rand.nextInt(1, 8) } } // needs storing
        val numbers = (1..8).shuffled().take(explorers.size)

        explorers.forEachIndexed { index, explorer ->
            val (x, y) = explorer.coord
            grid[y][x] = numbers[index]
        }

        val minNumbersIndex = numbers.withIndex().minByOrNull { it.value }?.index ?: return
        val lowestExplorerName = explorers[minNumbersIndex].name // needs storing

        val occupied = explorers.map { it.coord }.toSet()
        fun randomEmptyCoord(): Coord {
            var coord: Coord
            do {
                coord = Coord(rand.nextInt(grid[0].size), rand.nextInt(grid.size))
            } while (coord in occupied)
            return coord
        }
        val zeroCoord = randomEmptyCoord() // needs storing?
        grid[zeroCoord.y][zeroCoord.x] = 0
        val nineCoord = randomEmptyCoord() // needs storing?
        grid[nineCoord.y][nineCoord.x] = 9
        val sumCoordinates = zeroCoord.x + zeroCoord.y + nineCoord.x + nineCoord.y // this needs storing

        explorers.add(createExplorer(mapSizeX - 1, mapSizeY - 1, explorers) ?: return)
        explorers.add(createExplorer(mapSizeX - 1, mapSizeY - 1, explorers) ?: return)

        val totalSurvived = explorers
            .mapNotNull {
                val sum = it.coord.x + it.coord.y
                if (sum <= it.resources) sum else null
            }
            .size

        val jsonString = Json.encodeToString(DatabaseEntry(userId, explorers.subList(0, 5), explorers.subList(5, 7), grid.map { it.toList() }, lowestExplorerName, sumCoordinates, totalSurvived))
        val doc = Document.parse(jsonString)
        db.insertOne(doc)
    }

    override fun loadMarkdown(userId: String, db: ChallengeCollection): String {
        val dataset = db.find(Filters.eq("userId", userId))
        dataset.first() ?: return "No dataset found"

        val datasetObject: DatabaseEntry = dataset.first().let {
            it.remove("_id")
            val json = it.toJson()
            Json.decodeFromString<DatabaseEntry>(json)
        }

        var content = loadLocalContent()
        val formattedNames = datasetObject.firstExplorers.map {
            "    (\"${it.name}\", (${it.coord.x}, ${it.coord.y}), ${it.resources})"
        }
        content = content!!.replace("%explorer_data%", "[\n${formattedNames.joinToString(",\n")}\n]")

        val formattedGrid = datasetObject.grid.map { row ->
            "    [${row.joinToString(", ")}]"
        }
        content = content.replace("%island_map%", "[\n${formattedGrid.joinToString(",\n")}\n]")

        return content
    }

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        val dataset = db.find(Filters.eq("userId", userId))

        val datasetObject: DatabaseEntry = dataset.first().let {
            it.remove("_id")
            val json = it.toJson()
            Json.decodeFromString<DatabaseEntry>(json)
        }

        return SubmissionFeedback(datasetObject.lowestExplorerName.equals(submission, ignoreCase = true), "")

    }
}

object ThirdChallengeP1 : Subchallenge {
    override val parent: Challenge = ThirdChallenge
    override val subchallengeIdx: Int = 1

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit // subchallenge 0 inits all data

    override fun loadMarkdown(userId: String, db: ChallengeCollection): String {
        val dataset = db.find(Filters.eq("userId", userId))
        dataset.first() ?: return "No dataset found"

        val datasetObject: DatabaseEntry = dataset.first().let {
            it.remove("_id")
            val json = it.toJson()
            Json.decodeFromString<DatabaseEntry>(json)
        }

        var content = loadLocalContent()
        content = content!!.replace("%lowest_explorer%", datasetObject.lowestExplorerName)

        return content
    }

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        submission.toIntOrNull() ?: return SubmissionFeedback(false, "The answer must be an integer!")

        val dataset = db.find(Filters.eq("userId", userId))

        val datasetObject: DatabaseEntry = dataset.first().let {
            it.remove("_id")
            val json = it.toJson()
            Json.decodeFromString<DatabaseEntry>(json)
        }

        if(datasetObject.sumCoordinates == submission.toInt()) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "The answer should be ${if (datasetObject.sumCoordinates > submission.toInt()) "higher" else "lower"}.")
    }
}


object ThirdChallengeP2 : Subchallenge {
    override val parent: Challenge = ThirdChallenge
    override val subchallengeIdx: Int = 2

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit // subchallenge 0 inits all data

    override fun loadMarkdown(userId: String, db: ChallengeCollection): String {
        val dataset = db.find(Filters.eq("userId", userId))
        dataset.first() ?: return "No dataset found"

        val datasetObject: DatabaseEntry = dataset.first().let {
            it.remove("_id")
            val json = it.toJson()
            Json.decodeFromString<DatabaseEntry>(json)
        }

        var content = loadLocalContent()
        val formattedNames = datasetObject.secondExplorers.map {
            "    (\"${it.name}\", (${it.coord.x}, ${it.coord.y}), ${it.resources})"
        }
        content = content!!.replace("%new_explorers%", "[\n${formattedNames.joinToString(",\n")}\n]")

        return content
    }

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        submission.toIntOrNull() ?: return SubmissionFeedback(false, "The answer must be an integer!")

        val dataset = db.find(Filters.eq("userId", userId))

        val datasetObject: DatabaseEntry = dataset.first().let {
            it.remove("_id")
            val json = it.toJson()
            Json.decodeFromString<DatabaseEntry>(json)
        }

        if(datasetObject.totalSurvived == submission.toInt()) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "The answer should be ${if (datasetObject.totalSurvived > submission.toInt()) "higher" else "lower"}.")
    }
}



fun createExplorer(maxX: Int, maxY: Int, currentExplorers: List<Explorer>): Explorer? {
    val name = getRandomArmyName(currentExplorers.map { it.name })
    val position = getUniqueRandomCoord(maxX, maxY, currentExplorers.map { it.coord }) ?: return null

    return Explorer(name, position, java.util.Random().nextInt(4) - 2 + position.x + position.y)
}

fun getRandomArmyName(exclude: List<String>): String {
    val names = listOf(
        "Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Golf",
        "Hotel", "India", "Juliet", "Kilo", "Lima", "Mike", "November",
        "Oscar", "Papa", "Quebec", "Romeo", "Sierra", "Tango", "Uniform",
        "Victor", "Whiskey", "Xray", "Yankee", "Zulu"
    )
    val available = names.filterNot { it in exclude }
    return available.randomOrNull() ?: "null"
}

fun getUniqueRandomCoord(maxX: Int, maxY: Int, exclude: List<Coord>): Coord? {
    val total = maxX * maxY
    if (exclude.size >= total) return null

    val rand = java.util.Random()
    var coord: Coord
    do {
        coord = Coord(rand.nextInt(maxX), rand.nextInt(maxY))
    } while (coord in exclude)
    return coord
}