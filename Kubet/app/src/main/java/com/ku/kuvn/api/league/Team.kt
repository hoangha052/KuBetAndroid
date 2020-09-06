package com.ku.kuvn.api.league

data class Team(val name: String, val win: Int, val lose: Int, val draw: Int,
                val rank: Int, val points: Int, val logo: String)