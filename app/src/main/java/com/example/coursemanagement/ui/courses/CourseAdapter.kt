package com.example.coursemanagement.ui.courses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coursemanagement.R
import com.example.coursemanagement.data.model.Course
import com.example.coursemanagement.databinding.ItemCourseBinding
import com.example.coursemanagement.util.Formatters

data class CourseRow(val course: Course, val categoryName: String)

class CourseAdapter(private val onClick: (Course) -> Unit) :
    ListAdapter<CourseRow, CourseAdapter.CourseHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseHolder {
        val binding = ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseHolder, position: Int) = holder.bind(getItem(position))

    inner class CourseHolder(private val binding: ItemCourseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(row: CourseRow) {
            binding.textTitle.text = row.course.title
            binding.textInstructor.text = "Giảng viên: ${row.course.instructor}"
            binding.textCategory.text = "Danh mục: ${row.categoryName}"
            binding.textPrice.text = Formatters.price(row.course.price)
            Glide.with(binding.imageThumbnail)
                .load(row.course.thumbnail)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_error)
                .centerCrop()
                .into(binding.imageThumbnail)
            binding.cardCourse.setOnClickListener { onClick(row.course) }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<CourseRow>() {
            override fun areItemsTheSame(oldItem: CourseRow, newItem: CourseRow) =
                oldItem.course.id == newItem.course.id

            override fun areContentsTheSame(oldItem: CourseRow, newItem: CourseRow) = oldItem == newItem
        }
    }
}
