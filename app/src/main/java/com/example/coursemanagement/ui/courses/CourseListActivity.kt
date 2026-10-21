package com.example.coursemanagement.ui.courses

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coursemanagement.R
import com.example.coursemanagement.data.model.Category
import com.example.coursemanagement.data.model.Course
import com.example.coursemanagement.data.remote.ApiProvider
import com.example.coursemanagement.data.repository.CourseRepository
import com.example.coursemanagement.databinding.ActivityCourseListBinding
import com.example.coursemanagement.ui.categories.CategoryManagementActivity
import com.example.coursemanagement.util.UiErrors
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Locale

class CourseListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCourseListBinding
    private val repo = CourseRepository()
    private val adapter = CourseAdapter { course ->
        startActivity(Intent(this, CourseDetailActivity::class.java).apply {
            putExtra(CourseDetailActivity.EXTRA_COURSE_ID, course.id)
        })
    }
    private var categories: List<Category> = emptyList()
    private var courses: List<Course> = emptyList()
    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerCourses.layoutManager = LinearLayoutManager(this)
        binding.recyclerCourses.adapter = adapter
        binding.editSearch.addTextChangedListener { filterCourses() }
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) = filterCourses()
            override fun onNothingSelected(parent: AdapterView<*>?) = filterCourses()
        }
        binding.buttonAddCourse.setOnClickListener {
            startActivity(Intent(this, CourseFormActivity::class.java))
        }
        binding.buttonManageCategories.setOnClickListener {
            startActivity(Intent(this, CategoryManagementActivity::class.java))
        }
        binding.buttonRetry.setOnClickListener { refresh() }
    }

    /** Khi quay lại từ Thêm/Sửa/Xóa, luôn lấy dữ liệu mới từ MockAPI. */
    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        if (loading) return
        if (!ApiProvider.isConfigured) {
            binding.textStatus.text = "Chưa cấu hình MockAPI. Sửa mockApiBaseUrl trong gradle.properties rồi Sync/Run."
            binding.textStatus.visibility = View.VISIBLE
            binding.buttonRetry.visibility = View.GONE
            return
        }
        lifecycleScope.launch {
            loading = true
            binding.progress.visibility = View.VISIBLE
            binding.textStatus.visibility = View.GONE
            binding.buttonRetry.visibility = View.GONE
            try {
                val catRequest = async { repo.getCategories() }
                val courseRequest = async { repo.getCourses() }
                val previousSelectedCategoryId = selectedCategoryId()
                categories = catRequest.await()
                courses = courseRequest.await()
                renderCategories(previousSelectedCategoryId)
                filterCourses()
            } catch (error: Exception) {
                adapter.submitList(emptyList())
                binding.textStatus.text = UiErrors.message(error)
                binding.textStatus.visibility = View.VISIBLE
                binding.buttonRetry.visibility = View.VISIBLE
            } finally {
                loading = false
                binding.progress.visibility = View.GONE
            }
        }
    }

    private fun renderCategories(previousId: String?) {
        val labels = listOf(getString(R.string.all_categories)) + categories.map { it.name }
        binding.spinnerCategory.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, labels
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        val newIndex = categories.indexOfFirst { it.id == previousId }
        binding.spinnerCategory.setSelection(if (newIndex == -1) 0 else newIndex + 1)
    }

    private fun selectedCategoryId(): String? {
        val selected = binding.spinnerCategory.selectedItemPosition
        return categories.getOrNull(selected - 1)?.id
    }

    private fun filterCourses() {
        val search = binding.editSearch.text?.toString()?.trim()?.lowercase(Locale.ROOT).orEmpty()
        val selectedId = selectedCategoryId()
        val names = categories.associate { it.id to it.name }
        val rows = courses.asSequence()
            .filter { selectedId == null || it.categoryId == selectedId }
            .filter { it.title.lowercase(Locale.ROOT).contains(search) }
            .map { CourseRow(it, names[it.categoryId] ?: "Không tìm thấy danh mục") }
            .toList()
        adapter.submitList(rows)
        if (!loading) {
            binding.textStatus.text = if (courses.isEmpty()) "Chưa có khóa học nào trên MockAPI." else "Không có khóa học phù hợp."
            binding.textStatus.visibility = if (rows.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}
