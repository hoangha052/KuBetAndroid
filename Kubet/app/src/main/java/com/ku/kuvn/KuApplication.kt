package com.ku.kuvn

import android.app.Application
import com.ku.kuvn.data.HomeRepository
import com.ku.kuvn.utils.ConnectionUtilsImpl

class KuApplication: Application() {

    private lateinit var homeRepository: HomeRepository

    override fun onCreate() {
        super.onCreate()
        homeRepository = HomeRepository(ConnectionUtilsImpl(this))
    }

    fun getHomeRepository() = homeRepository
}