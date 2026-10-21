package com.example.coursemanagement.data.model

data class Course(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val categoryId: String = "",
    val instructor: String = "",
    val price: Double = 0.0,
    val duration: Double = 0.0,
    val thumbnail: String = "",
    val trailerUrl: String = "",
    val createdAt: String = ""
)
