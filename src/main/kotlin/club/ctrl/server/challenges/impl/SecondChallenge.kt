package club.ctrl.server.challenges.impl

import club.ctrl.server.challenges.Challenge
import club.ctrl.server.challenges.ChallengeCollection
import club.ctrl.server.challenges.Subchallenge
import club.ctrl.server.server.routes.SubmissionFeedback
import com.mongodb.client.model.Filters
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document


@Serializable
data class UniqueDataset(val userId: String, val dataset: String, val sum: Int, val mean: Int, val maxDelta: Int)


object SecondChallenge : Challenge {
    override val name: String = "Easy: The Warehouse"
    override val id: Int = 1
    override val subchallenges: List<Subchallenge> = listOf(SecondChallengeP0, SecondChallengeP1, SecondChallengeP2)
    override val isTeamChallenge: Boolean = false;
}

object SecondChallengeP0 : Subchallenge {
    override val parent: Challenge = SecondChallenge
    override val subchallengeIdx: Int = 0

    override fun onFirstOpen(userId: String, db: ChallengeCollection) {
        db.deleteMany(Filters.eq("userId", userId)) // should be impossible

        val numbers = List(365) { (1..1000).random() * 10 }
        val dataset = "numbers = [" + (numbers).joinToString(", ") + "]"

        val sum = numbers.sum()
        val mean = numbers.sum() / numbers.size
        val maxDelta = numbers.max() - numbers.min()

        val uniqueDataset = UniqueDataset(userId, dataset, sum, mean, maxDelta)

        val json = Json.encodeToString(uniqueDataset)
        val doc = Document.parse(json)

        db.insertOne(doc)
    }

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        submission.toIntOrNull() ?: return SubmissionFeedback(false, "The answer must be an integer!")

        val requiredAnswer = db.find(Filters.eq("userId", userId))
        requiredAnswer.first() ?: return SubmissionFeedback(false, "No dataset found")

        val datasetObject: UniqueDataset = requiredAnswer.first().let {
            it.remove("_id")
            val json = it.toJson()
            Json.decodeFromString<UniqueDataset>(json)
        }

        if(datasetObject.sum == submission.toInt()) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "The answer should be ${if (datasetObject.sum > submission.toInt()) "higher" else "lower"}.")
    }

    override fun loadMarkdown(userId: String, db: ChallengeCollection): String {
        val dataset = db.find(Filters.eq("userId", userId))
        dataset.first() ?: return "No dataset found"

        val datasetObject: UniqueDataset = dataset.first().let {
            it.remove("_id")
            val json = it.toJson()
            Json.decodeFromString<UniqueDataset>(json)
        }

        var content = loadLocalContent()
        content = content!!.replace("%list%", datasetObject.dataset)

        return content
    }
}


object SecondChallengeP1 : Subchallenge {
    override val parent: Challenge = SecondChallenge
    override val subchallengeIdx: Int = 1

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit // subchallenge 0 inits all data

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        submission.toIntOrNull() ?: return SubmissionFeedback(false, "The answer must be an integer!")

        val requiredAnswer = db.find(Filters.eq("userId", userId))
        requiredAnswer.first() ?: return SubmissionFeedback(false, "No dataset found")

        val datasetObject: UniqueDataset = requiredAnswer.first().let {
            it.remove("_id")
            val json = it.toJson()
            Json.decodeFromString<UniqueDataset>(json)
        }

        if(datasetObject.mean == submission.toInt()) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "The answer should be ${if (datasetObject.mean > submission.toInt()) "higher" else "lower"}.")
    }
}

object SecondChallengeP2 : Subchallenge {
    override val parent: Challenge = SecondChallenge
    override val subchallengeIdx: Int = 2

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit // subchallenge 0 inits all data

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        submission.toIntOrNull() ?: return SubmissionFeedback(false, "The answer must be an integer!")

        val requiredAnswer = db.find(Filters.eq("userId", userId))
        requiredAnswer.first() ?: return SubmissionFeedback(false, "No dataset found")

        val datasetObject: UniqueDataset = requiredAnswer.first().let {
            it.remove("_id")
            val json = it.toJson()
            Json.decodeFromString<UniqueDataset>(json)
        }

        if(datasetObject.maxDelta == submission.toInt()) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "The answer should be ${if (datasetObject.maxDelta > submission.toInt()) "higher" else "lower"}.")
    }
}
