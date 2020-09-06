package com.ku.kuvn.api.league

import com.google.gson.annotations.SerializedName

data class Match(val time: String, val home: Team, val away: Team,
                 @SerializedName("home_score") val homeScore: Int,
                 @SerializedName("away_score") val awayScore: Int)