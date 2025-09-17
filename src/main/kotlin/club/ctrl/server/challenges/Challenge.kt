package club.ctrl.server.challenges

import club.ctrl.server.server.routes.SubmissionFeedback
import java.nio.charset.StandardCharsets
import kotlin.collections.getOrNull
import kotlin.io.readBytes
import kotlin.io.use


interface Challenge {
    val name: String
    val id: Int
    val subchallenges: List<Subchallenge>
}

fun Challenge.getSubchallenge(idx: Int): Subchallenge? =
    subchallenges.getOrNull(idx)



interface Subchallenge {
    val parent: Challenge
    val subchallengeIdx: Int

    // Hook for when the user first opens / unlocks a specific subchallenge.
    // Could be used to pre-init any values, such as a dataset for the user to use.
    fun onFirstOpen(userId: String): Unit

    // Hook for when the user opens a subchallenge in the webpage.
    fun loadMarkdown(userId: String): String = loadLocalContent() ?: "No content available for this challenge"

    // Used when a user requests to submit an answer.
    fun onSubmit(userId: String, submission: String): SubmissionFeedback

    // Internal helper function to load local markdown from the resources/ folder
    private fun loadLocalContent(): String? {
        val classLoader = Thread.currentThread().contextClassLoader
        val markdownFile = "content/challenge${parent.id}/sc$subchallengeIdx.md" // e.g. `content/challengeid/sc0.md`
        val inputStream = classLoader.getResourceAsStream(markdownFile)

        return inputStream?.use { stream ->
            val bytes = stream.readBytes()
            String(bytes, StandardCharsets.UTF_8)
        }
    }
}
