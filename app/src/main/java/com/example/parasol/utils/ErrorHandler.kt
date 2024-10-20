package com.example.parasol.utils

import retrofit2.HttpException
import java.io.IOException

object ErrorHandler {
    fun handleError(exception: Exception): String {
        return when (exception) {
            is IOException -> "Network error: ${exception.message}"
            is HttpException -> "HTTP error: ${exception.message}"
            else -> "Unexpected error: ${exception.message}"
        }
    }
}
