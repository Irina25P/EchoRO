package com.example.echoro.ui.screens.admin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echoro.ui.theme.BackgroundGray
import com.example.echoro.ui.theme.GridLineColor
import com.example.echoro.ui.theme.NavyBlue


@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = NavyBlue,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
fun SummaryStatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundGray),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 28.sp, color = NavyBlue, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AccuracyDonutChart(title: String, percentage: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 16.dp.toPx()
                drawArc(
                    color = GridLineColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = (percentage / 100f) * 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
            Text(
                text = "$percentage%",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}