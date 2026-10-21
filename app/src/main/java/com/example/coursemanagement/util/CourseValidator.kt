package com.example.coursemanagement.util

import java.net.URI

/** Kiểm tra dữ liệu độc lập với Android, có thể unit test trên JVM. */
object CourseValidator {
    fun validate(
        title: String,
        description: String,
        instructor: String,
        priceText: String,
        durationText: String,
        thumbnail: String,
        trailerUrl: String,
        categoryId: String,
        availableCategoryIds: Set<String>
    ): String? {
        if (title.isBlank()) return "Tên khóa học không được để trống."
        if (description.isBlank()) return "Mô tả không được để trống."
        if (instructor.isBlank()) return "Tên giảng viên không được để trống."
        if (categoryId.isBlank() || categoryId !in availableCategoryIds) {
            return "Vui lòng chọn danh mục hợp lệ từ MockAPI."
        }
        val price = priceText.trim().toDoubleOrNull()
        if (price == null || !price.isFinite() || price < 0.0) return "Học phí phải là số không âm."
        val duration = durationText.trim().toDoubleOrNull()
        if (duration == null || !duration.isFinite() || duration <= 0.0) {
            return "Thời lượng phải là số lớn hơn 0."
        }
        if (!isHttpUrl(thumbnail)) return "URL ảnh bìa phải là liên kết HTTP/HTTPS hợp lệ."
        if (!isHttpUrl(trailerUrl)) return "URL trailer phải là liên kết HTTP/HTTPS hợp lệ."
        return null
    }

    fun isHttpUrl(raw: String): Boolean = try {
        val uri = URI(raw.trim())
        (uri.scheme.equals("https", true) || uri.scheme.equals("http", true)) &&
            !uri.host.isNullOrBlank() && uri.userInfo == null
    } catch (_: Exception) {
        false
    }
}
