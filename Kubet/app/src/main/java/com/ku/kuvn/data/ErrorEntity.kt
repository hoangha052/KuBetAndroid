package com.ku.kuvn.data

sealed class ErrorEntity {

    object Network : ErrorEntity()

    object NotFound : ErrorEntity()

    object Unknown : ErrorEntity()
}