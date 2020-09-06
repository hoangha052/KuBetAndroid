package com.ku.kuvn.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectionUtilsImpl(val app: Application) {

    fun isOnline(): Boolean {
        val connMgr = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }
}

fun Context.isOnline(): Boolean {
    val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
    return networkInfo?.isConnected == true
}