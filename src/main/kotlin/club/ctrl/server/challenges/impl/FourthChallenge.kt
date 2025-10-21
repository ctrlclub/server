package club.ctrl.server.challenges.impl

import club.ctrl.server.challenges.Challenge
import club.ctrl.server.challenges.ChallengeCollection
import club.ctrl.server.challenges.Subchallenge
import club.ctrl.server.server.routes.SubmissionFeedback

object FourthChallenge : Challenge {
    override val name: String = "Basics: Fix the bugs!"
    override val id: Int = 3
    override val subchallenges: List<Subchallenge> = listOf(FourthChallengeP0, FourthChallengeP1)
    override val isTeamChallenge: Boolean = false
}

object FourthChallengeP0 : Subchallenge {
    override val parent: Challenge = FourthChallenge
    override val subchallengeIdx: Int = 0

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        // hard coding the python output... (please dont hate me)
        val answer = "Craig has £{bank_dictionary['Craig']}\n" +
                "James has £0\n" +
                "Craig is transferring £50 to James...\n" +
                "Craig has £73\n" +
                "James has £23\n" +
                "The richest person is Adam with £500."

        return generateFeedback(submission.replace(" ", ""), answer.replace(" ", "").replace("\n", ""))
    }
}

object FourthChallengeP1 : Subchallenge {
    override val parent: Challenge = FourthChallenge
    override val subchallengeIdx: Int = 1

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        // hard coding the python output... (please dont hate me)
        val answer = "Craig has £123\n" +
                "James has £0\n" +
                "Craig is transferring £50 to James...\n" +
                "Craig has £73\n" +
                "James has £50\n" +
                "The richest person is Adam with £525.0."

        return generateFeedback(submission.replace(" ", ""), answer.replace(" ", "").replace("\n", ""))
    }
}

fun getLevenshteinDistance(a: String, b: String): Int {
    val dp = Array(a.length + 1) { IntArray(b.length + 1) }
    for (i in 0..a.length) dp[i][0] = i
    for (j in 0..b.length) dp[0][j] = j

    for (i in 1..a.length) {
        for (j in 1..b.length) {
            val cost = if (a[i - 1] == b[j - 1]) 0 else 1
            dp[i][j] = minOf(
                dp[i - 1][j] + 1,      // deletion
                dp[i][j - 1] + 1,      // insertion
                dp[i - 1][j - 1] + cost // substitution
            )
        }
    }
    return dp[a.length][b.length]
}

fun getStringSimilarity(a: String, b: String): Double {
    if (a.isEmpty() && b.isEmpty()) return 1.0
    val distance = getLevenshteinDistance(a, b)
    val maxLen = maxOf(a.length, b.length)
    return (1.0 - distance.toDouble() / maxLen)
}

fun generateFeedback(submission: String, answer: String): SubmissionFeedback {
    val similarity = getStringSimilarity(submission, answer)
    if (similarity == 1.0) {
        return SubmissionFeedback(true, "")
    } else if (similarity > 0.8 && similarity < 1.0) {
        return SubmissionFeedback(false, "Make sure you copy the output correctly!")
    }
    return SubmissionFeedback(false, "That doesn't look right!")
}