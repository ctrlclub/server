package club.ctrl.server.challenges

import club.ctrl.server.challenges.impl.FifthChallenge
import club.ctrl.server.challenges.impl.FirstChallenge
import club.ctrl.server.challenges.impl.SecondChallenge
import club.ctrl.server.challenges.impl.ThirdChallenge
import club.ctrl.server.challenges.impl.FourthChallenge
import club.ctrl.server.challenges.impl.SeventhChallenge


object ChallengeManager {
    val challenges: List<Challenge> = listOf(FirstChallenge, SecondChallenge, ThirdChallenge, FourthChallenge, FifthChallenge,
        SeventhChallenge
    )
}
