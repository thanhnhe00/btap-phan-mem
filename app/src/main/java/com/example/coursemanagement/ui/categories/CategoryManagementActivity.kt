package com.example.coursemanagement.ui.categories

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coursemanagement.data.model.Category
import com.example.coursemanagement.data.model.CategoryPayload
import com.example.coursemanagement.data.repository.CourseRepository
import com.example.coursemanagement.databinding.ActivityCategoryManagementBinding
import com.example.coursemanagement.databinding.DialogCategoryBinding
import com.example.coursemanagement.util.UiErrors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class CategoryManagementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryManagementBinding
    private val repo = CourseRepository()
    private val adapter = CategoryAdapter(::showCategoryDialog, ::confirmDelete)
    private var busy = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.recyclerCategories.layoutManager = LinearLayoutManager(this)
        binding.recyclerCategories.adapter = adapter
        binding.buttonAddCategory.setOnClickListener { showCategoryDialog(null) }
        binding.buttonRetry.setOnClickListener { refresh() }
        refresh()
    }

    private fun refresh() {
        if (busy) return
        lifecycleScope.launch {
            setBusy(true)
            binding.textStatus.visibility = View.GONE
            binding.buttonRetry.visibility = View.GONE
            try {
                val result = repo.getCategories()
                adapter.submitList(result)
                if (result.isEmpty()) showStatus("Chưa có danh mục. Hãy tạo danh mục đầu tiên.")
            } catch (error: Exception) {
                showStatus(UiErrors.message(error))
                binding.buttonRetry.visibility = View.VISIBLE
            } finally {
                setBusy(false)
            }
        }
    }

    private fun showCategoryDialog(category: Category?) {
        if (busy) return
        val form = DialogCategoryBinding.inflate(layoutInflater)
        if (category != null) {
            form.editName.setText(category.name)
            form.editDescription.setText(category.description)
        }
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(if (category == null) "Thêm danh mục" else "Sửa danh mục")
            .setView(form.root)
            .setNegativeButton("Hủy", null)
            .setPositiveButton("Lưu", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val name = form.editName.text?.toString()?.trim().orEmpty()
                val description = form.editDescription.text?.toString()?.trim().orEmpty()
                if (name.isBlank()) {
                    form.fieldName.error = "Vui lòng nhập tên danh mục"
                    return@setOnClickListener
                }
                if (description.isBlank()) {
                    form.fieldDescription.error = "Vui lòng nhập mô tả"
                    return@setOnClickListener
                }
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = false
                lifecycleScope.launch {
                    setBusy(true)
                    try {
                        val payload = CategoryPayload(name, description)
                        if (category == null) repo.addCategory(payload)
                        else repo.editCategory(category.id, payload)
                        dialog.dismiss()
                        setBusy(false)
                        refresh()
                    } catch (error: Exception) {
                        Toast.makeText(this@CategoryManagementActivity, UiErrors.message(error), Toast.LENGTH_LONG).show()
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = true
                        setBusy(false)
                    }
                }
            }
        }
        dialog.show()
    }

    private fun confirmDelete(category: Category) {
        if (busy) return
        MaterialAlertDialogBuilder(this)
            .setTitle("Xóa danh mục?")
            .setMessage("Chỉ xóa được “${category.name}” khi không có khóa học nào thuộc danh mục này.")
            .setNegativeButton("Hủy", null)
            .setPositiveButton("Kiểm tra và xóa") { _, _ ->
                lifecycleScope.launch {
                    setBusy(true)
                    try {
                        if (repo.deleteCategoryIfUnused(category.id)) {
                            Toast.makeText(this@CategoryManagementActivity, "Đã xóa danh mục", Toast.LENGTH_SHORT).show()
                        } else {
                            MaterialAlertDialogBuilder(this@CategoryManagementActivity)
                                .setTitle("Không thể xóa")
                                .setMessage("Danh mục đang có khóa học. Hãy xóa hoặc chuyển các khóa học sang danh mục khác trước.")
                                .setPositiveButton("Đã hiểu", null)
                                .show()
                        }
                    } catch (error: Exception) {
                        Toast.makeText(this@CategoryManagementActivity, UiErrors.message(error), Toast.LENGTH_LONG).show()
                    } finally {
                        setBusy(false)
                        refresh()
                    }
                }
            }.show()
    }

    private fun setBusy(value: Boolean) {
        busy = value
        binding.progress.visibility = if (value) View.VISIBLE else View.GONE
        binding.buttonAddCategory.isEnabled = !value
    }

    private fun showStatus(message: String) {
        binding.textStatus.text = message
        binding.textStatus.visibility = View.VISIBLE
    }
}
