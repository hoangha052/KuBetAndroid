package com.ku.kuvn.api

import com.google.gson.annotations.SerializedName

data class Blog(val id: String, val title: String, val description: String,
                val content: String, val thumb: String,
                @SerializedName("published_time") val publishedTime: String,
                @SerializedName("last_modifier") val lastModifier: String)