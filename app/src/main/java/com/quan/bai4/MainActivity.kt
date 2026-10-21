package com.quan.bai4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quan.bai4.data.Student
import com.quan.bai4.ui.StudentViewModel
import com.quan.bai4.ui.screens.StudentAddEditScreen
import com.quan.bai4.ui.screens.StudentDetailScreen
import com.quan.bai4.ui.screens.StudentListScreen
import com.quan.bai4.ui.theme.Bai4Theme

sealed class AppScreen {
    data object List : AppScreen()
    data class Detail(val studentId: Int) : AppScreen()
    data class AddEdit(val studentId: Int? = null) : AppScreen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Bai4Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    StudentApp()
                }
            }
        }
    }
}

@Composable
fun StudentApp(viewModel: StudentViewModel = viewModel()) {
    val students by viewModel.studentList.collectAsState()
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.List) }

    when (val screen = currentScreen) {
        is AppScreen.List -> {
            StudentListScreen(
                students = students,
                onStudentClick = { student ->
                    currentScreen = AppScreen.Detail(student.id)
                },
                onAddStudentClick = {
                    currentScreen = AppScreen.AddEdit(null)
                }
            )
        }

        is AppScreen.Detail -> {
            val student = students.find { it.id == screen.studentId }
            if (student != null) {
                BackHandler {
                    currentScreen = AppScreen.List
                }
                StudentDetailScreen(
                    student = student,
                    onBack = {
                        currentScreen = AppScreen.List
                    },
                    onEditClick = {
                        currentScreen = AppScreen.AddEdit(student.id)
                    },
                    onDeleteConfirmed = { studentToDelete ->
                        viewModel.deleteStudent(studentToDelete) {
                            currentScreen = AppScreen.List
                        }
                    }
                )
            } else {
                currentScreen = AppScreen.List
            }
        }

        is AppScreen.AddEdit -> {
            val studentToEdit = screen.studentId?.let { id ->
                students.find { it.id == id }
            }

            BackHandler {
                currentScreen = if (screen.studentId != null) {
                    AppScreen.Detail(screen.studentId)
                } else {
                    AppScreen.List
                }
            }

            StudentAddEditScreen(
                student = studentToEdit,
                onBack = {
                    currentScreen = if (screen.studentId != null) {
                        AppScreen.Detail(screen.studentId)
                    } else {
                        AppScreen.List
                    }
                },
                onSaveStudent = { studentToSave ->
                    if (studentToEdit != null) {
                        viewModel.updateStudent(studentToSave) {
                            currentScreen = AppScreen.Detail(studentToSave.id)
                        }
                    } else {
                        viewModel.insertStudent(studentToSave) {
                            currentScreen = AppScreen.List
                        }
                    }
                }
            )
        }
    }
}