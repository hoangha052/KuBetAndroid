package com.ku.kuvn.api

import com.google.gson.annotations.SerializedName

data class MenuResponse(@SerializedName("total_pages") val totalPages: Int,
                        @SerializedName("items") val menus: List<Menu>)