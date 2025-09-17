package club.ctrl.server.challenges.impl

import club.ctrl.server.challenges.Challenge
import club.ctrl.server.challenges.Subchallenge
import club.ctrl.server.server.routes.SubmissionFeedback

object SecondChallenge : Challenge {
    override val name: String = "Not the beginning anymore"
    override val id: Int = 1
    override val subchallenges: List<Subchallenge> = listOf(SecondChallengeP0, SecondChallengeP1, SecondChallengeP2)
}

object SecondChallengeP0 : Subchallenge {
    override val parent: Challenge = SecondChallenge
    override val subchallengeIdx: Int = 0

    override fun onFirstOpen(userId: String) {
        println("$userId opened the second challenge first sassignmenet yeah assignment!")
    }

    override fun onSubmit(userId: String, submission: String): SubmissionFeedback {
        return SubmissionFeedback(correct = submission == "hi", "The answer is hi")
    }
}

object SecondChallengeP1 : Subchallenge {
    override val parent: Challenge = SecondChallenge
    override val subchallengeIdx: Int = 1

    override fun onFirstOpen(userId: String) {
        println("$userId opened the second second yeah assignment!")
    }

    override fun onSubmit(userId: String, submission: String): SubmissionFeedback {
        return SubmissionFeedback(correct = submission == "hi", "The answer is hi")
    }
}

object SecondChallengeP2 : Subchallenge {
    override val parent: Challenge = SecondChallenge
    override val subchallengeIdx: Int = 2

    override fun onFirstOpen(userId: String) {
        println("$userId opened the third second yeah assignment!")
    }

    override fun onSubmit(userId: String, submission: String): SubmissionFeedback {
        return SubmissionFeedback(correct = submission == "hi", "The answer is hi")
    }
}
