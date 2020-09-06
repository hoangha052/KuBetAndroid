package com.ku.kuvn.api

data class BaseResponse<T>(val data: T, val code: Int, val message: String, val status: Boolean)