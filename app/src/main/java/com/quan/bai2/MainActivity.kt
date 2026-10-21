package com.quan.bai2
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                LayoutBai2Screen(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

@Composable
fun LayoutBai2Screen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Khối các ô màu
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Ô 1: Toàn chiều ngang
            ColorBox(
                text = "1",
                color = Color(0xFF1E88E5),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
            )

            // Ô 2: Toàn chiều ngang
            ColorBox(
                text = "2",
                color = Color(0xFFE53935),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
            )

            // Hàng giữa: Ô 3, 4, 5 (mỗi ô weight 1) và 1 phần trống bên phải (weight 1)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColorBox(
                    text = "3",
                    color = Color(0xFFFFD54F),
                    textColor = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                ColorBox(
                    text = "4",
                    color = Color(0xFF43A047),
                    modifier = Modifier.weight(1f)
                )
                ColorBox(
                    text = "5",
                    color = Color(0xFF8E24AA),
                    modifier = Modifier.weight(1f)
                )
                // Khoảng trắng chừa ra bên phải bằng kích thước 1 ô
                Spacer(modifier = Modifier.weight(1f))
            }

            // Ô 6: Toàn chiều ngang
            ColorBox(
                text = "6",
                color = Color(0xFFFB8C00),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(125.dp)
            )
        }

        // Họ tên - MSSV căn giữa ở đáy
        Text(
            text = "Chu Mạnh Quân - BIT240189",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )
    }
}

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
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )
    }
}