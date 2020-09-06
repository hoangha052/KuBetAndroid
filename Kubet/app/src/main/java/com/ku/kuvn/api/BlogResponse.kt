package com.ku.kuvn.api

import com.google.gson.annotations.SerializedName

data class BlogResponse(@SerializedName("total_pages") val totalPages: Int,
                        @SerializedName("items") val gifts: List<Blog>)