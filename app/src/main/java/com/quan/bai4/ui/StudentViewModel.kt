package com.quan.bai4.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.quan.bai4.data.Student
import com.quan.bai4.data.StudentDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudentViewModel(application: Application) : AndroidViewModel(application) {
    private val studentDao = StudentDatabase.getDatabase(application).studentDao()

    val studentList: StateFlow<List<Student>> = studentDao.getAllStudents()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedStudent = MutableStateFlow<Student?>(null)
    val selectedStudent: StateFlow<Student?> = _selectedStudent.asStateFlow()

    fun selectStudent(student: Student?) {
        _selectedStudent.value = student
    }

    fun insertStudent(student: Student, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            studentDao.insertStudent(student)
            onComplete()
        }
    }

    fun updateStudent(student: Student, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            studentDao.updateStudent(student)
            if (_selectedStudent.value?.id == student.id) {
                _selectedStudent.value = student
            }
            onComplete()
        }
    }

    fun deleteStudent(student: Student, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            studentDao.deleteStudent(student)
            if (_selectedStudent.value?.id == student.id) {
                _selectedStudent.value = null
            }
            onComplete()
        }
    }
}
