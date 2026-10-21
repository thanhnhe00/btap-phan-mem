package com.example.coursemanagement.data.repository

import com.example.coursemanagement.data.model.CategoryPayload
import com.example.coursemanagement.data.model.CoursePayload
import com.example.coursemanagement.data.remote.ApiProvider

/** Single source of truth: MockAPI. Không có danh sách được hardcode trong app. */
class CourseRepository {
    private val api get() = ApiProvider.service

    suspend fun getCategories() = api.getCategories()
    suspend fun getCourses() = api.getCourses()
    suspend fun getCourse(id: String) = api.getCourse(id)
    suspend fun addCourse(course: CoursePayload) = api.createCourse(course)
    suspend fun editCourse(id: String, course: CoursePayload) = api.updateCourse(id, course)
    suspend fun deleteCourse(id: String) = api.deleteCourse(id)
    suspend fun addCategory(category: CategoryPayload) = api.createCategory(category)
    suspend fun editCategory(id: String, category: CategoryPayload) = api.updateCategory(id, category)

    /** MockAPI không có FK: kiểm tra trước khi DELETE từ phía Android. */
    suspend fun deleteCategoryIfUnused(categoryId: String): Boolean {
        val hasCourse = api.getCourses().any { it.categoryId == categoryId }
        if (hasCourse) return false
        api.deleteCategory(categoryId)
        return true
    }
}
