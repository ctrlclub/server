package club.ctrl.server.challenges.impl

import club.ctrl.server.challenges.Challenge
import club.ctrl.server.challenges.ChallengeCollection
import club.ctrl.server.challenges.Subchallenge
import club.ctrl.server.server.routes.SubmissionFeedback

object ThirdChallenge : Challenge {
    override val name: String = "definitely near the edn"
    override val id: Int = 2
    override val subchallenges: List<Subchallenge> = listOf(ThirdChallengeP0)
}

object ThirdChallengeP0 : Subchallenge {
    override val parent: Challenge = SecondChallenge
    override val subchallengeIdx: Int = 0

    override fun onFirstOpen(userId: String, db: ChallengeCollection) {
        println("$userId opened the third challenge first sassignmenet yeah assignment!")
    }

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        return SubmissionFeedback(correct = submission == "hi", "The answer is hi")
    }
}
