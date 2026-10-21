package com.example.coursemanagement.ui.courses

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.coursemanagement.R
import com.example.coursemanagement.data.model.Category
import com.example.coursemanagement.data.model.Course
import com.example.coursemanagement.data.model.CoursePayload
import com.example.coursemanagement.data.repository.CourseRepository
import com.example.coursemanagement.databinding.ActivityCourseFormBinding
import com.example.coursemanagement.util.CourseValidator
import com.example.coursemanagement.util.UiErrors
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CourseFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCourseFormBinding
    private val repo = CourseRepository()
    private var categories: List<Category> = emptyList()
    private var editingCourse: Course? = null
    private var busy = false
    private var formReady = false
    private val courseId by lazy { intent.getStringExtra(EXTRA_COURSE_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = if (courseId == null) "Thêm khóa học" else "Sửa khóa học"
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.buttonSave.setOnClickListener { saveCourse() }
        binding.buttonPreview.setOnClickListener { previewImage() }
        loadForm()
    }

    private fun loadForm() {
        lifecycleScope.launch {
            setBusy(true)
            try {
                val categoryTask = async { repo.getCategories() }
                val courseTask = if (courseId == null) null else async { repo.getCourse(courseId!!) }
                categories = categoryTask.await()
                val course = courseTask?.await()
                if (categories.isEmpty()) {
                    showStatus("MockAPI chưa có danh mục. Hãy tạo danh mục trước khi thêm khóa học.")
                    return@launch
                }
                val labels = categories.map { it.name }
                binding.spinnerCategory.adapter = ArrayAdapter(
                    this@CourseFormActivity, android.R.layout.simple_spinner_item, labels
                ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
                if (course != null) {
                    editingCourse = course
                    binding.editTitle.setText(course.title)
                    binding.editDescription.setText(course.description)
                    binding.editInstructor.setText(course.instructor)
                    binding.editPrice.setText(course.price.toPlainString())
                    binding.editDuration.setText(course.duration.toPlainString())
                    binding.editThumbnail.setText(course.thumbnail)
                    binding.editTrailer.setText(course.trailerUrl)
                    val index = categories.indexOfFirst { it.id == course.categoryId }
                    if (index < 0) {
                        showStatus("Danh mục của khóa học không còn tồn tại. Hãy chọn danh mục mới.")
                    } else {
                        binding.spinnerCategory.setSelection(index)
                    }
                    previewImage()
                }
                formReady = true
                binding.buttonSave.isEnabled = true
            } catch (error: Exception) {
                showStatus(UiErrors.message(error))
            } finally {
                setBusy(false)
            }
        }
    }

    private fun previewImage() {
        val url = binding.editThumbnail.text?.toString().orEmpty().trim()
        if (!CourseValidator.isHttpUrl(url)) {
            binding.editThumbnail.error = "Nhập URL ảnh hợp lệ trước khi xem"
            return
        }
        Glide.with(binding.imagePreview).load(url)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_error)
            .centerCrop()
            .into(binding.imagePreview)
    }

    private fun saveCourse() {
        if (busy || !formReady || categories.isEmpty()) return
        val categoryId = categories.getOrNull(binding.spinnerCategory.selectedItemPosition)?.id.orEmpty()
        val title = binding.editTitle.text?.toString().orEmpty().trim()
        val description = binding.editDescription.text?.toString().orEmpty().trim()
        val instructor = binding.editInstructor.text?.toString().orEmpty().trim()
        val priceText = binding.editPrice.text?.toString().orEmpty().trim()
        val durationText = binding.editDuration.text?.toString().orEmpty().trim()
        val thumbnail = binding.editThumbnail.text?.toString().orEmpty().trim()
        val trailer = binding.editTrailer.text?.toString().orEmpty().trim()
        val error = CourseValidator.validate(
            title, description, instructor, priceText, durationText, thumbnail, trailer,
            categoryId, categories.map { it.id }.toSet()
        )
        if (error != null) {
            showStatus(error)
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            return
        }
        val payload = CoursePayload(
            title, description, categoryId, instructor,
            priceText.toDouble(), durationText.toDouble(), thumbnail, trailer,
            editingCourse?.createdAt?.takeIf { it.isNotBlank() }
                ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        )
        lifecycleScope.launch {
            setBusy(true)
            try {
                // Lấy lại categories ngay trước POST/PUT để hạn chế dùng categoryId đã bị xóa.
                val freshCategoryIds = repo.getCategories().map { it.id }.toSet()
                if (categoryId !in freshCategoryIds) {
                    showStatus("Danh mục vừa chọn đã bị xóa. Hãy tải lại biểu mẫu.")
                    return@launch
                }
                if (courseId == null) repo.addCourse(payload) else repo.editCourse(courseId!!, payload)
                Toast.makeText(this@CourseFormActivity, "Lưu khóa học thành công", Toast.LENGTH_SHORT).show()
                finish() // Màn danh sách/chi tiết sẽ refresh trong onResume()
            } catch (error: Exception) {
                showStatus(UiErrors.message(error))
            } finally {
                setBusy(false)
            }
        }
    }

    private fun Double.toPlainString(): String =
        if (this % 1.0 == 0.0) this.toLong().toString() else this.toString()

    private fun showStatus(message: String) {
        binding.textStatus.text = message
        binding.textStatus.visibility = View.VISIBLE
    }

    private fun setBusy(value: Boolean) {
        busy = value
        binding.progress.visibility = if (value) View.VISIBLE else View.GONE
        binding.buttonSave.isEnabled = !value && formReady && categories.isNotEmpty()
        binding.buttonPreview.isEnabled = !value
    }

    companion object { const val EXTRA_COURSE_ID = "courseId" }
}
