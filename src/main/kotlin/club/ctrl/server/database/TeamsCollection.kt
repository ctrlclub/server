package club.ctrl.server.database

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document
import kotlin.random.Random

const val TEAMS = "teams"
const val TEAM_CODES = "teamcodes"

@Serializable
data class Team(val teamId: Int, val userIds: List<String>, val owner: String)

@Serializable
data class TeamCode(val teamCode: Int, val teamId: Int)

// returns the users owning team
fun registerUserInTeam(userId: String, teamCode: Int, db: MongoDatabase): Team? {
    val teamId = lookupTeamCode(teamCode, db)
    teamId ?: return null // if the team didnt exist, return null indicating err

    val teams = db.getCollection(TEAMS).find(Filters.eq("teamId", teamId)).filterNotNull()
    if(teams.isEmpty()) {
        val newTeam = Team(teamId, mutableListOf(userId), userId)
        db.getCollection(TEAMS).insertOne(Document.parse(Json.encodeToString(newTeam))) // add new team to db
        return newTeam
    }

    val teamToDeserialize = teams.first()
    teamToDeserialize.remove("_id")
    val owningTeam = Json.decodeFromString<Team>(teamToDeserialize.toJson())
    val newOwningTeam = Team(owningTeam.teamId, owningTeam.userIds + userId, owningTeam.owner.ifBlank { owningTeam.userIds.first() })

    db.getCollection(TEAMS).replaceOne(Filters.eq("teamId", teamId), Document.parse(Json.encodeToString(newOwningTeam)))
    return newOwningTeam
}

fun removeUserInTeam(userId: String, db: MongoDatabase) {
    val team = getUserTeam(userId, db)
    team ?: return

    if(team.userIds.size == 1) { // if they were the only member, just delete the team obj in the database - itll be remade later if needed
        db.getCollection(TEAMS).deleteOne(Filters.eq("teamId", team.teamId))
        return
    }

    val newMembers = team.userIds - userId
    val newTeam = Team(team.teamId, newMembers, if(team.owner == userId) { newMembers.first() } else { team.owner })
    db.getCollection(TEAMS).replaceOne(Filters.eq("teamId", team.teamId), Document.parse(Json.encodeToString(newTeam)))
}

// returns the users team or null if the user is not in a team
fun getUserTeam(userId: String, db: MongoDatabase): Team? {
    val teams = db.getCollection(TEAMS).find()
    val team = teams.find { d->d.getList("userIds", String::class.java).any { it.equals(userId) } }

    team ?: return null

    team.remove("_id")
    val teamObj = Json.decodeFromString<Team>(team.toJson())

    return teamObj
}

fun clearTeams(db: MongoDatabase) {
    db.getCollection(TEAMS).deleteMany(Filters.empty())
}

fun populateTeams(count: Int, db: MongoDatabase): List<TeamCode> {
    db.getCollection(TEAMS).deleteMany(Filters.empty())

    val teamCodes = db.getCollection(TEAM_CODES)
    teamCodes.deleteMany(Filters.empty())

    val codes = genTeamCodes(count)
    val newEntries = mutableListOf<TeamCode>()
    codes.forEachIndexed { index, value ->
        newEntries.add(TeamCode(value, index + 1))
    }

    newEntries.forEach {
        val json = Json.encodeToString(it)
        teamCodes.insertOne(Document.parse(json))
    }

    return newEntries;
}

fun lookupTeamCode(teamCode: Int, db: MongoDatabase): Int? {
    val results = db.getCollection(TEAM_CODES).find(Filters.eq("teamCode", teamCode)).filterNotNull()

    if(results.isEmpty()) return null

    return results.first().getInteger("teamId")
}

private fun genTeamCodes(n: Int): Set<Int> {
    val numbers = mutableSetOf<Int>()
    while (numbers.size < n) {
        val num = Random(System.currentTimeMillis()).nextInt(11111111, 100000000) // upper bound exclusive
        numbers.add(num)
    }
    return numbers
}
