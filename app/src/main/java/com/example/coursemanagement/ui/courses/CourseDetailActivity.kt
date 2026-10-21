package com.example.coursemanagement.ui.courses

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.coursemanagement.R
import com.example.coursemanagement.data.model.Course
import com.example.coursemanagement.data.repository.CourseRepository
import com.example.coursemanagement.databinding.ActivityCourseDetailBinding
import com.example.coursemanagement.ui.video.TrailerActivity
import com.example.coursemanagement.util.Formatters
import com.example.coursemanagement.util.UiErrors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CourseDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCourseDetailBinding
    private val repo = CourseRepository()
    private var course: Course? = null
    private var loading = false
    private val courseId by lazy { intent.getStringExtra(EXTRA_COURSE_ID).orEmpty() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }
        if (courseId.isBlank()) {
            finish()
            return
        }
        binding.buttonRetry.setOnClickListener { refresh() }
        binding.buttonTrailer.setOnClickListener {
            val activeCourse = course ?: return@setOnClickListener
            startActivity(Intent(this, TrailerActivity::class.java).apply {
                putExtra(TrailerActivity.EXTRA_URL, activeCourse.trailerUrl)
                putExtra(TrailerActivity.EXTRA_TITLE, activeCourse.title)
            })
        }
        binding.buttonEdit.setOnClickListener {
            startActivity(Intent(this, CourseFormActivity::class.java).apply {
                putExtra(CourseFormActivity.EXTRA_COURSE_ID, courseId)
            })
        }
        binding.buttonDelete.setOnClickListener { confirmDelete() }
    }

    override fun onResume() {
        super.onResume()
        if (courseId.isNotBlank()) refresh()
    }

    private fun refresh() {
        if (loading) return
        lifecycleScope.launch {
            loading = true
            binding.progress.visibility = View.VISIBLE
            binding.textStatus.visibility = View.GONE
            binding.buttonRetry.visibility = View.GONE
            try {
                val courseRequest = async { repo.getCourse(courseId) }
                val categoriesRequest = async { repo.getCategories() }
                val loaded = courseRequest.await()
                val name = categoriesRequest.await().find { it.id == loaded.categoryId }?.name
                    ?: "Không tìm thấy danh mục (ID ${loaded.categoryId})"
                course = loaded
                binding.textTitle.text = loaded.title
                binding.textDescription.text = loaded.description
                binding.textCategory.text = "Danh mục: $name"
                binding.textInstructor.text = "Giảng viên: ${loaded.instructor}"
                binding.textDuration.text = "Thời lượng: ${Formatters.hours(loaded.duration)}"
                binding.textPrice.text = Formatters.price(loaded.price)
                binding.textCreatedAt.text = "Ngày tạo: ${loaded.createdAt}"
                Glide.with(binding.imageThumbnail).load(loaded.thumbnail)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_error).centerCrop().into(binding.imageThumbnail)
                binding.scrollContent.visibility = View.VISIBLE
            } catch (error: Exception) {
                binding.scrollContent.visibility = View.GONE
                binding.textStatus.text = UiErrors.message(error)
                binding.textStatus.visibility = View.VISIBLE
                binding.buttonRetry.visibility = View.VISIBLE
            } finally {
                loading = false
                binding.progress.visibility = View.GONE
            }
        }
    }

    private fun confirmDelete() {
        val activeCourse = course ?: return
        MaterialAlertDialogBuilder(this)
            .setTitle("Xóa khóa học?")
            .setMessage("Bạn có chắc muốn xóa “${activeCourse.title}”? Không thể hoàn tác.")
            .setNegativeButton("Hủy", null)
            .setPositiveButton("Xóa") { _, _ ->
                lifecycleScope.launch {
                    binding.progress.visibility = View.VISIBLE
                    binding.buttonDelete.isEnabled = false
                    try {
                        repo.deleteCourse(courseId)
                        Toast.makeText(this@CourseDetailActivity, "Đã xóa khóa học", Toast.LENGTH_SHORT).show()
                        finish()
                    } catch (error: Exception) {
                        Toast.makeText(this@CourseDetailActivity, UiErrors.message(error), Toast.LENGTH_LONG).show()
                    } finally {
                        binding.buttonDelete.isEnabled = true
                        binding.progress.visibility = View.GONE
                    }
                }
            }.show()
    }

    companion object { const val EXTRA_COURSE_ID = "courseId" }
}
