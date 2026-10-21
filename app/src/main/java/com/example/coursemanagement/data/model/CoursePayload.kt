package com.example.coursemanagement.data.model

/** POST/PUT body: id do MockAPI tự sinh, ứng dụng không gửi id. */
data class CoursePayload(
    val title: String,
    val description: String,
    val categoryId: String,
    val instructor: String,
    val price: Double,
    val duration: Double,
    val thumbnail: String,
    val trailerUrl: String,
    val createdAt: String
)
