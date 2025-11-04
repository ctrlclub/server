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
import kotlin.random.Random


@Serializable
data class EighthChallengeDataset(val userId: String, val fileTree: String, val topLevelTotal: Int, val totalFiles: Int, val dominantExtensionCount: Int)

object EighthChallengeRecursion : Challenge {
    override val name: String = "Medium: Recursion"
    override val id: Int = 7
    override val subchallenges: List<Subchallenge> = listOf(EighthChallengeP0, EighthChallengeP1, EighthChallengeP2,
        EighthChallengeP3, EighthChallengeP4)
    override val isTeamChallenge: Boolean = false;
}

object EighthChallengeP0 : Subchallenge {
    override val parent: Challenge = EighthChallengeRecursion
    override val subchallengeIdx: Int = 0

    override fun onFirstOpen(userId: String, db: ChallengeCollection) {
        val fileCount = Random.nextInt(150, 200)
        val (fileTree, count, totalTopLevel) = generateFinalTree(fileCount)
        val dominantExtCount = count.values.max()
        val dataset = EighthChallengeDataset(userId, fileTree, totalTopLevel, fileCount, dominantExtCount)

        upsertSerializable(db, dataset) {
            Filters.eq("userId", userId)
        }
    }

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        if(!submission.equals("yes", ignoreCase = true)) {
            return SubmissionFeedback(false, "no??? Type yes")
        }

        return SubmissionFeedback(true, "Great. Let's put your understanding to use...")
    }
}

object EighthChallengeP1 : Subchallenge {
    override val parent: Challenge = EighthChallengeRecursion
    override val subchallengeIdx: Int = 1

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        val answer: Long = 2432902008176640000

        submission.toLongOrNull() ?: return SubmissionFeedback(false, "The answer must be an integer")
        val int = submission.toLong()

        if(answer == int) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "Incorrect answer. Your answer was too ${if(int > answer) { "high" } else { "low" }}")
    }
}

object EighthChallengeP2 : Subchallenge {
    override val parent: Challenge = EighthChallengeRecursion
    override val subchallengeIdx: Int = 2

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit

    override fun loadMarkdown(userId: String, db: ChallengeCollection): String {
        val dataset = findSerializable<EighthChallengeDataset>(db) {
            Filters.eq("userId", userId)
        } ?: return "No dataset was generated for this challenge"

        var content = loadLocalContent() ?: return "No subchallenge content found"

        content = content.fillPlaceholders(
            "tree" to dataset.fileTree
        )

        return content
    }

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        submission.toIntOrNull() ?: return SubmissionFeedback(false, "The answer must be an integer")
        val int = submission.toIntOrNull()

        val dataset = findSerializable<EighthChallengeDataset>(db) {
            Filters.eq("userId", userId)
        } ?: return SubmissionFeedback(false, "No dataset generated")

        if(int == dataset.topLevelTotal) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "That wasn't the right answer...")
    }
}

object EighthChallengeP3 : Subchallenge {
    override val parent: Challenge = EighthChallengeRecursion
    override val subchallengeIdx: Int = 3

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        submission.toIntOrNull() ?: return SubmissionFeedback(false, "The answer must be an integer")
        val int = submission.toIntOrNull()

        val dataset = findSerializable<EighthChallengeDataset>(db) {
            Filters.eq("userId", userId)
        } ?: return SubmissionFeedback(false, "No dataset generated")

        if(int == dataset.totalFiles) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "That wasn't the right answer...")
    }
}

object EighthChallengeP4 : Subchallenge {
    override val parent: Challenge = EighthChallengeRecursion
    override val subchallengeIdx: Int = 4

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        submission.toIntOrNull() ?: return SubmissionFeedback(false, "The answer must be an integer")
        val int = submission.toIntOrNull()

        val dataset = findSerializable<EighthChallengeDataset>(db) {
            Filters.eq("userId", userId)
        } ?: return SubmissionFeedback(false, "No dataset generated")

        if(int == dataset.dominantExtensionCount) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "That wasn't the right answer...")
    }
}

fun generateFinalTree(nFiles: Int): Triple<String, Map<String, Int>, Int> {
    val extensions = listOf("docx", "doc", "xlsx", "xls", "pptx", "ppt", "pdf", "txt", "rtf", "odt", "ods", "odp", "csv", "jpg", "jpeg", "png", "gif", "zip")

    val allFiles = generateFileList(nFiles, extensions)
    return distributeFiles(
        files = allFiles.toMutableList(),
        maxDepth = 4,
        depth = 0
    )
}


fun generateFileList(totalFiles: Int, extensions: List<String>): List<String> {
    val files = mutableListOf<String>()
    repeat(totalFiles) {
        val name = randomName()
        val ext = extensions.random()
        files += "$name.$ext"
    }
    return files
}

fun distributeFiles(
    files: MutableList<String>,
    maxDepth: Int,
    depth: Int = 0
): Triple<String, Map<String, Int>, Int> { // tree, counts, top-level file count
    val builder = StringBuilder()
    builder.append("{")

    val counts = mutableMapOf<String, Int>()
    var topLevelCount = 0

    if (files.isEmpty()) {
        builder.append("}")
        return Triple(builder.toString(), counts, topLevelCount)
    }

    // Step 1: decide how many files stay at this level
    val maxTopFiles = if (depth == 0) (files.size / 2).coerceAtLeast(1) else 0
    val topFilesCount = if (depth == 0) Random.nextInt(1, maxTopFiles + 1) else 0

    val topFiles = files.take(topFilesCount)
    val remainingFiles = files.drop(topFilesCount)

    // Step 2: add top-level files
    topFiles.forEach {
        val ext = it.substringAfterLast('.', "")
        counts[ext] = counts.getOrDefault(ext, 0) + 1
        if (depth == 0) topLevelCount++
        builder.append("\"$it\": None,")
    }

    if (remainingFiles.isNotEmpty()) {
        if (depth >= maxDepth || remainingFiles.size <= 3) {
            // dump all remaining files here
            remainingFiles.forEach {
                val ext = it.substringAfterLast('.', "")
                counts[ext] = counts.getOrDefault(ext, 0) + 1
                builder.append("\"$it\": None,")
            }
        } else {
            // split into folders
            val folderCount = Random.nextInt(2, 5)
            val groups = List(folderCount) { mutableListOf<String>() }

            remainingFiles.forEach { file ->
                groups.random().add(file)
            }

            groups.filter { it.isNotEmpty() }.forEach { subGroup ->
                val folderName = randomName()
                val (subTree, subCounts, _) = distributeFiles(
                    files = subGroup,
                    maxDepth = maxDepth,
                    depth = depth + 1
                )
                builder.append("\"$folderName\": $subTree,")
                subCounts.forEach { (ext, n) ->
                    counts[ext] = counts.getOrDefault(ext, 0) + n
                }
            }
        }
    }

    builder.append("}")
    return Triple(builder.toString(), counts, topLevelCount)
}


fun randomName(): String {
    val adjectives = listOf("Secret", "Final", "Untitled", "Corrupt", "My", "A", "The", "Hidden", "Broken", "Draft")
    val nouns = listOf(
        "Notes", "Plan", "File", "Homework", "Data", "Project", "Recipe", "Dream",
        "Work", "LifeNotes", "Text", "Show", "Screenshot", "Agenda", "Draft",
        "Prototype", "Analysis", "Virus", "Diagram", "Invoice", "Proof", "Manifest"
    )
    return adjectives.random() + nouns.random()
}