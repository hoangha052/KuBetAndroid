package com.ku.kuvn.ui.home

import com.ku.kuvn.api.Banner
import com.ku.kuvn.api.Menu

sealed class HomeItem

data class BannerItem(val banners: List<Banner>) : HomeItem()
data class GameItem(val data: String = "") : HomeItem()
data class SportItem(val data: String = "") : HomeItem()
data class MenuItem(val menu: Menu) : HomeItem()