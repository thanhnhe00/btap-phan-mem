package com.example.coursemanagement.util

import java.io.IOException
import retrofit2.HttpException

object UiErrors {
    fun message(error: Throwable): String = when (error) {
        is HttpException -> when (error.code()) {
            404 -> "Không tìm thấy dữ liệu (404). Kiểm tra resources trên MockAPI."
            429 -> "API đang bị giới hạn yêu cầu (429). Vui lòng thử lại."
            else -> "Lỗi HTTP ${error.code()}. Kiểm tra endpoint MockAPI."
        }
        is IOException -> "Không kết nối được Internet/MockAPI. Kiểm tra mạng và URL API."
        else -> error.localizedMessage ?: "Đã xảy ra lỗi. Vui lòng thử lại."
    }
}
