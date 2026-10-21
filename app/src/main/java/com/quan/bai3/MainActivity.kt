package com.quan.bai3

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MainAppNavigation()
            }
        }
    }
}

@Composable
fun MainAppNavigation() {
    var currentScreen by remember { mutableIntStateOf(1) }
    var userName by remember { mutableStateOf("") }
    var mssv by remember { mutableStateOf("") }

    if (currentScreen == 1) {
        ScreenOne(
            userName = userName,
            onUserNameChange = { userName = it },
            mssv = mssv,
            onMssvChange = { mssv = it },
            onNavigateToScreenTwo = {
                currentScreen = 2
            }
        )
    } else {
        ScreenTwo(
            userName = userName,
            mssv = mssv,
            onBackToScreenOne = {
                currentScreen = 1
            }
        )
    }
}

// ==================== SCREEN 1 ====================
@Composable
fun ScreenOne(
    userName: String,
    onUserNameChange: (String) -> Unit,
    mssv: String,
    onMssvChange: (String) -> Unit,
    onNavigateToScreenTwo: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding() // Tự động co và đẩy giao diện lên trên khi bàn phím mở
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. KHỐI CÁC Ô MÀU (Tinh chỉnh chiều cao gọn gàng)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Hàng 1: Ô 1 & Ô 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ColorBox(text = "1", color = Color(0xFF1E88E5), modifier = Modifier.weight(1f))
                ColorBox(text = "2", color = Color(0xFFE53935), modifier = Modifier.weight(1f))
            }

            // Hàng 2: Ô 3, Ô 4, Ô 5
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ColorBox(text = "3", color = Color(0xFFFFD54F), textColor = Color.Black, modifier = Modifier.weight(1f))
                ColorBox(text = "4", color = Color(0xFF43A047), modifier = Modifier.weight(1f))
                ColorBox(text = "5", color = Color(0xFF8E24AA), modifier = Modifier.weight(2f))
            }

            // Hàng 3: Ô 6
            ColorBox(
                text = "6",
                color = Color(0xFFFB8C00),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. KHỐI NHẬP LIỆU & NÚT BẤM (Không lo bị che)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Nhap thong tin sinh vien",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            OutlinedTextField(
                value = userName,
                onValueChange = onUserNameChange,
                placeholder = { Text("Enter your name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = mssv,
                onValueChange = onMssvChange,
                placeholder = { Text("Enter your student ID") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    focusManager.clearFocus()
                    // Validate không được để trống
                    if (userName.trim().isEmpty() || mssv.trim().isEmpty()) {
                        Toast.makeText(
                            context,
                            "Vui lòng nhập đầy đủ UserName và MSSV!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        onNavigateToScreenTwo()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(top = 6.dp, bottom = 10.dp)
                    .width(130.dp)
                    .height(44.dp)
            ) {
                Text(
                    text = "Click me",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

// ==================== SCREEN 2 ====================
@Composable
fun ScreenTwo(
    userName: String,
    mssv: String,
    onBackToScreenOne: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        // Nút Back ở vị trí Top-Left
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(Color(0xFFF57C00), shape = RoundedCornerShape(6.dp))
                .clickable { onBackToScreenOne() }
                .padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Back",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        // Khối hiển thị thông tin ở chính giữa Screen 2
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Screen 2",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "Name: $userName",
                fontSize = 18.sp,
                color = Color(0xFF333333)
            )

            Text(
                text = "Student ID: $mssv",
                fontSize = 18.sp,
                color = Color(0xFF333333)
            )
        }
    }
}

// ==================== COMPONENT Ô MÀU ====================
@Composable
fun ColorBox(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}