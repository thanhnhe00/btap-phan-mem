package com.example.coursemanagement.data.remote

import com.example.coursemanagement.data.model.Category
import com.example.coursemanagement.data.model.CategoryPayload
import com.example.coursemanagement.data.model.Course
import com.example.coursemanagement.data.model.CoursePayload
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MockApiService {
    @GET("categories")
    suspend fun getCategories(): List<Category>

    @POST("categories")
    suspend fun createCategory(@Body category: CategoryPayload): Category

    @PUT("categories/{id}")
    suspend fun updateCategory(@Path("id") id: String, @Body category: CategoryPayload): Category

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String): Category

    @GET("courses")
    suspend fun getCourses(): List<Course>

    @GET("courses/{id}")
    suspend fun getCourse(@Path("id") id: String): Course

    @POST("courses")
    suspend fun createCourse(@Body course: CoursePayload): Course

    @PUT("courses/{id}")
    suspend fun updateCourse(@Path("id") id: String, @Body course: CoursePayload): Course

    @DELETE("courses/{id}")
    suspend fun deleteCourse(@Path("id") id: String): Course
}
