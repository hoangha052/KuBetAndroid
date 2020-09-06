package com.ku.kuvn.api.league

data class League(val id: String, val name: String, val thumb: String,
                  private val standings: List<Standing>?, private val matches: List<Match>?,
                  private val histories: List<Match>?) {

    fun getStandings() = standings ?: emptyList()
    fun getMatches() = matches ?: emptyList()
    fun getHistories() = histories ?: emptyList()
}

fun League.getTopTeams(): List<Team> {
    val standings = getStandings()
    return if (standings.size == 1) {
        standings[0].teams.take(4)
    } else {
        standings.filter { it.teams.isNotEmpty() }
            .map { it.teams[0] }.sortedByDescending { it.points }
    }
}