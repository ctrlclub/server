package club.ctrl.server.challenges

import club.ctrl.server.challenges.impl.FirstChallenge
import club.ctrl.server.challenges.impl.SecondChallenge
import club.ctrl.server.challenges.impl.ThirdChallenge


object ChallengeManager {
    val challenges: List<Challenge> = listOf(FirstChallenge, SecondChallenge, ThirdChallenge)
}
