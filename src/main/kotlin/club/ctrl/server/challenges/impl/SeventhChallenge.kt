package club.ctrl.server.challenges.impl

import java.util.Random
import club.ctrl.server.challenges.Challenge
import club.ctrl.server.challenges.ChallengeCollection
import club.ctrl.server.challenges.Subchallenge
import club.ctrl.server.extensions.fillPlaceholders
import club.ctrl.server.extensions.findSerializable
import club.ctrl.server.extensions.upsertSerializable
import club.ctrl.server.server.routes.SubmissionFeedback
import com.mongodb.client.model.Filters
import kotlinx.serialization.Serializable

@Serializable
data class SeventhChallengeData(val userId: String, val names: List<String>, val numFamilies: Int)

object SeventhChallenge : Challenge {
    override val name: String = "File IO: The Basics"
    override val id: Int = 6
    override val subchallenges: List<Subchallenge> = listOf(SeventhChallengeP0, SeventhChallengeP1, SeventhChallengeP2)
    override val isTeamChallenge: Boolean = true
}

object SeventhChallengeP0 : Subchallenge {
    override val parent: Challenge = SeventhChallenge
    override val subchallengeIdx: Int = 0

    override fun loadMarkdown(userId: String, db: ChallengeCollection): String {
        val dataset = findSerializable<SeventhChallengeData>(db) {
            Filters.eq("userId", userId)
        } ?: return "No dataset was generated for this challenge"

        var content = loadLocalContent() ?: return "No subchallenge content found"

        val file = dataset.names.subList(0, dataset.names.size - 1).joinToString("\n") // hopefully ignoring last name

        content = content.fillPlaceholders(
            "file" to file,
            "name" to dataset.names.last()
        )

        return content
    }

    override fun onFirstOpen(userId: String, db: ChallengeCollection) {
        val firstNames = listOf("James", "Mary", "Michael", "Patricia", "John", "Jennifer", "Robert", "Linda", "David", "Elizabeth", "William", "Barbara", "Richard", "Susan", "Joseph", "Jessica", "Thomas", "Karen", "Christopher", "Sarah", "Charles", "Lisa", "Daniel", "Nancy", "Matthew", "Sandra", "Anthony", "Ashley", "Mark", "Emily", "Steven", "Kimberly", "Donald", "Betty", "Andrew", "Margaret", "Joshua", "Donna", "Paul", "Michelle", "Kenneth", "Carol", "Kevin", "Amanda", "Brian", "Melissa", "Timothy", "Deborah", "Ronald", "Stephanie", "Jason", "Rebecca", "George", "Sharon", "Edward", "Laura", "Jeffrey", "Cynthia", "Ryan", "Amy", "Jacob", "Kathleen", "Nicholas", "Angela", "Gary", "Dorothy", "Eric", "Shirley", "Jonathan", "Emma", "Stephen", "Brenda", "Larry", "Nicole", "Justin", "Pamela", "Benjamin", "Samantha", "Scott", "Anna", "Brandon", "Katherine", "Samuel", "Christine", "Gregory", "Debra", "Alexander", "Rachel", "Patrick", "Olivia", "Frank", "Carolyn", "Jack", "Maria", "Raymond", "Janet", "Dennis", "Heather", "Tyler", "Diane", "Aaron", "Catherine", "Jerry", "Julie", "Jose", "Victoria", "Nathan", "Helen", "Adam", "Joyce", "Henry", "Lauren", "Zachary", "Kelly", "Douglas", "Christina", "Peter", "Joan", "Noah", "Judith", "Kyle", "Ruth", "Ethan", "Hannah", "Christian", "Evelyn", "Jeremy", "Andrea", "Keith", "Virginia", "Austin", "Megan", "Sean", "Cheryl", "Roger", "Jacqueline", "Terry", "Madison", "Walter", "Sophia", "Dylan", "Abigail", "Gerald", "Teresa", "Carl", "Isabella", "Jordan", "Sara", "Bryan", "Janice", "Gabriel", "Martha", "Jesse", "Gloria", "Harold", "Kathryn", "Lawrence", "Ann", "Logan", "Charlotte", "Arthur", "Judy", "Bruce", "Amber", "Billy", "Julia", "Elijah", "Grace", "Joe", "Denise", "Alan", "Danielle", "Juan", "Natalie", "Liam", "Alice", "Willie", "Marilyn", "Mason", "Diana", "Albert", "Beverly", "Randy", "Jean", "Wayne", "Brittany", "Vincent", "Theresa", "Lucas", "Frances", "Caleb", "Kayla", "Luke", "Alexis", "Bobby", "Tiffany", "Isaac", "Lori", "Bradley", "Kathy")
        val lastNames = listOf("Smith", "Jones", "Taylor", "Brown", "Williams", "Wilson", "Johnson", "Davies", "Patel", "Robinson", "Wright", "Thompson", "Evans", "Walker", "White", "Roberts", "Green", "Hall", "Thomas", "Clarke", "Jackson", "Wood", "Harris", "Edwards", "Turner", "Martin", "Cooper", "Hill", "Ward", "Hughes", "Moore", "Clark", "King", "Harrison", "Lewis", "Baker", "Lee", "Allen", "Morris", "Khan", "Scott", "Watson", "Davis", "Parker", "James", "Bennett", "Young", "Phillips", "Richardson", "Mitchell", "Bailey", "Carter", "Cook", "Singh", "Shaw", "Bell", "Collins", "Morgan", "Kelly", "Begum", "Miller", "Cox", "Hussain", "Marshall", "Simpson", "Price", "Anderson", "Adams", "Wilkinson", "Ali", "Ahmed", "Foster", "Ellis", "Murphy", "Chapman", "Mason", "Gray", "Richards", "Webb", "Griffiths", "Hunt", "Palmer", "Campbell", "Holmes", "Mills", "Rogers", "Barnes", "Knight", "Matthews", "Barker", "Powell", "Stevens", "Kaur", "Fisher", "Butler", "Dixon", "Russell", "Harvey", "Pearson", "Graham", "Fletcher", "Murray", "Howard", "Shah", "Gibson", "Gill", "Fox", "Stewart", "Elliott", "Lloyd", "Andrews", "Ford", "Owen", "West", "Saunders", "Reynolds", "Day", "Walsh", "Brooks", "Atkinson", "Payne", "Cole", "Bradley", "Spencer", "Pearce", "Burton", "Lawrence", "Dawson", "Ball", "Rose", "Booth", "Grant", "Wells", "Watts", "Hudson", "Hart", "Armstrong", "Perry", "Newman", "Jenkins", "Hunter", "Webster", "Lowe", "Francis", "Page", "Hayes", "Carr", "Marsh", "Stone", "Riley", "Woods", "Gregory", "Barrett", "Berry", "Dunn", "Newton", "Holland", "Porter", "Oliver", "Ryan", "Reid", "Williamson", "Parsons", "O'Brien", "Bird", "Robertson", "Reed", "Bates", "Dean", "Walton", "Hawkins", "Cooke", "Harding", "Ross", "Henderson", "Kennedy", "Gardner", "Lane", "Burns", "Bishop", "Burgess", "Shepherd", "Nicholson", "Freeman", "Cross", "Hamilton", "Hodgson", "Warren", "Sutton", "Harper", "Yates", "Nicholls", "Robson", "Chambers", "Hardy", "Curtis", "Moss", "Long", "Akhtar", "Coleman")
        val rand = Random(System.currentTimeMillis())

        val limitedLastNames = lastNames.take(25) // use only first 25
        val usedLastNames = mutableMapOf<String, Int>() // i hate kotlin ;-; WTF IS THIS

        val names = List(100) {
            val first = firstNames[rand.nextInt(firstNames.size)]
            val last = limitedLastNames[rand.nextInt(limitedLastNames.size)]
            usedLastNames[last] = (usedLastNames[last] ?: 0) + 1
            "$first $last"
        }

        var numFamilies = 0
        for ((_, count) in usedLastNames) {
            if (count >= 3) {
                numFamilies += 1
            }
        }

        val dataset = SeventhChallengeData(userId, names, numFamilies)
        upsertSerializable(db, dataset) {
            Filters.eq("userId", userId)
        }
    }

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        val dataset = findSerializable<SeventhChallengeData>(db) {
            Filters.eq("userId", userId)
        } ?: return SubmissionFeedback(false, "No dataset generated")

        val file = dataset.names.joinToString("\n")

        if (submission.contentEquals(file)) {
            return SubmissionFeedback(true, "Good")
        }

        return SubmissionFeedback(false, "Try again")
    }
}

object SeventhChallengeP1 : Subchallenge {
    override val parent: Challenge = SeventhChallenge
    override val subchallengeIdx: Int = 1

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit // subchallenge 0 inits all data

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        val dataset = findSerializable<SeventhChallengeData>(db) {
            Filters.eq("userId", userId)
        } ?: return SubmissionFeedback(false, "No dataset generated")

        val sortedBySurname = dataset.names.sortedBy { it.substringAfterLast(" ") }
        val file = sortedBySurname.joinToString("\n")

        if (submission.contentEquals(file)) {
            return SubmissionFeedback(true, "Good")
        }

        return SubmissionFeedback(false, "Try again")
    }
}

object SeventhChallengeP2 : Subchallenge {
    override val parent: Challenge = SeventhChallenge
    override val subchallengeIdx: Int = 1

    override fun onFirstOpen(userId: String, db: ChallengeCollection) = Unit // subchallenge 0 inits all data

    override fun onSubmit(userId: String, submission: String, db: ChallengeCollection): SubmissionFeedback {
        submission.toIntOrNull() ?: return SubmissionFeedback(false, "The answer must be an integer")
        val int = submission.toInt()

        val dataset = findSerializable<SeventhChallengeData>(db) {
            Filters.eq("userId", userId)
        } ?: return SubmissionFeedback(false, "No dataset generated")

        if(dataset.numFamilies == int) {
            return SubmissionFeedback(true, "")
        }

        return SubmissionFeedback(false, "Incorrect answer. Your answer was too ${if(int > dataset.numFamilies) { "high" } else { "low" }}")
    }
}