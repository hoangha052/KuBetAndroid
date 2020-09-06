package com.ku.kuvn.api.league

data class LeagueResponse(val data: List<League>, val code: Int,
                          val message: String, val status: Boolean)