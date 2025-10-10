package club.ctrl.server.challenges.impl

import club.ctrl.server.challenges.Challenge
import club.ctrl.server.challenges.ChallengeCollection
import club.ctrl.server.challenges.Subchallenge
import club.ctrl.server.server.routes.SubmissionFeedback
import com.mongodb.client.model.Filters
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document


object FirstChallenge : Challenge {
    override val name: String = "Basics: For-Loops"
    override val id: Int = 0
    override val subchallenges: List<Subchallenge> = listOf(FirstChallengeP0, FirstChallengeP1, FirstChallengeP2)
    override val isTeamChallenge: Boolean = false;
}

object FirstChallengeP0 : Subchallenge {
    override val parent: Challenge = FirstChallenge
    override val subchallengeIdx: Int = 0

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        val answer = 7;

        submission.toIntOrNull() ?: return SubmissionFeedback(false, "The answer must be an integer!")
        if(answer == submission.toInt()) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "Close! The answer should be ${if (answer > submission.toInt()) "higher" else "lower"}.")
    }
}


object FirstChallengeP1 : Subchallenge {
    override val parent: Challenge = FirstChallenge
    override val subchallengeIdx: Int = 1

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit // subchallenge 0 inits all data

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        val answer = 4;

        submission.toIntOrNull() ?: return SubmissionFeedback(false, "The answer must be an integer!")
        if(answer == submission.toInt()) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "The answer should be ${if (answer > submission.toInt()) "higher" else "lower"}.")
    }
}

object FirstChallengeP2 : Subchallenge {
    override val parent: Challenge = FirstChallenge
    override val subchallengeIdx: Int = 2

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit // subchallenge 0 inits all data

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        val answer = 69420;

        submission.toIntOrNull() ?: return SubmissionFeedback(false, "The answer must be an integer!")
        if(answer == submission.toInt()) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "The answer should be ${if (answer > submission.toInt()) "higher" else "lower"}.")
    }
}
