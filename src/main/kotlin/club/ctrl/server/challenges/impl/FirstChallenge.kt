package club.ctrl.server.challenges.impl

import club.ctrl.server.challenges.Challenge
import club.ctrl.server.challenges.Subchallenge

object FirstChallenge : Challenge {
    override val name: String = "The Beginning"
    override val id: Int = 0
    override val subchallenges: List<Subchallenge> = listOf(FirstChallengeP0, FirstChallengeP1)
}

object FirstChallengeP0 : Subchallenge {
    override val parent: Challenge = FirstChallenge
    override val subchallengeIdx: Int = 0

    override fun onFirstOpen(userId: String) {
        println("$userId opened the assignment!")
    }

    override fun onSubmit(userId: String, submission: String): Boolean {
        println("$userId guessed $submission. Returning false.")
        return false;
    }
}

object FirstChallengeP1 : Subchallenge {
    override val parent: Challenge = FirstChallenge
    override val subchallengeIdx: Int = 1

    override fun onFirstOpen(userId: String) {
        println("$userId opened the second assignment!")
    }

    override fun onSubmit(userId: String, submission: String): Boolean {
        println("$userId guessed $submission on the second assignment. Returning false.")
        return false;
    }
}
