package club.ctrl.server.challenges.impl

import club.ctrl.server.challenges.Challenge
import club.ctrl.server.challenges.ChallengeCollection
import club.ctrl.server.challenges.Subchallenge
import club.ctrl.server.database.findSerializable
import club.ctrl.server.server.routes.SubmissionFeedback
import com.mongodb.client.model.Filters


object FourthChallenge : Challenge {
    override val name: String = "Team: Team challenge yea"
    override val id: Int = 3
    override val subchallenges: List<Subchallenge> = listOf(FourthChallengeP0, FourthChallengeP1)
    override val isTeamChallenge: Boolean = true;
}

object FourthChallengeP0 : Subchallenge {
    override val parent: Challenge = FourthChallenge
    override val subchallengeIdx: Int = 0

    override fun onFirstOpen(userId: String, db: ChallengeCollection) {
    }

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        return SubmissionFeedback(true, "")
    }
}

object FourthChallengeP1 : Subchallenge {
    override val parent: Challenge = FourthChallenge
    override val subchallengeIdx: Int = 1

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit // subchallenge 0 inits all data

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        return SubmissionFeedback(true, "")
    }
}