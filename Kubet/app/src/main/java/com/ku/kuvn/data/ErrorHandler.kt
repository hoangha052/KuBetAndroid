package com.ku.kuvn.data

import okio.IOException
import retrofit2.HttpException
import java.net.HttpURLConnection

interface ErrorHandler {

    fun getError(throwable: Throwable): ErrorEntity
}

class GeneralErrorHandlerImpl : ErrorHandler {

    override fun getError(throwable: Throwable): ErrorEntity {
        return when(throwable) {
            is IOException -> ErrorEntity.Network
            is HttpException -> {
                when(throwable.code()) {
                    // not found
                    HttpURLConnection.HTTP_NOT_FOUND -> ErrorEntity.NotFound

                    // all the others will be treated as unknown error
                    else -> ErrorEntity.Unknown
                }
            }
            else -> ErrorEntity.Unknown
        }
    }
}