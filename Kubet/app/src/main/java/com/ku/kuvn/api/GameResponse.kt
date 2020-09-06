package com.ku.kuvn.api

data class GameResponse(val data: List<Game>, val code: Int,
                        val message: String, val status: Boolean)