package com.example.echoro.ui.screens.admin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echoro.ui.theme.GridLineColor
import com.example.echoro.ui.theme.MiniChartColor
import com.example.echoro.ui.theme.ReindeerChartColor
import com.example.echoro.ui.theme.SparrowChartColor
import com.example.echoro.ui.theme.Teal

data class ChartSeries(val label: String, val points: List<Float>, val color: Color)

@Composable
fun LineChartCard(xLabels: List<String>, series: List<ChartSeries>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(modifier = Modifier.fillMaxWidth()) {

                Column(
                    modifier = Modifier
                        .height(200.dp)
                        .padding(top = 16.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    for (i in 5 downTo 0) {
                        Text(text = i.toString(), fontSize = 10.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(end = 16.dp, top = 16.dp, bottom = 8.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val width = size.width
                            val height = size.height

                            val xStep = width / (xLabels.size - 1).coerceAtLeast(1)
                            val yStep = height / 5f

                            for (i in 0..5) {
                                val y = height - (i * yStep)
                                drawLine(
                                    color = GridLineColor,
                                    start = Offset(0f, y),
                                    end = Offset(width, y),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }

                            fun drawDataLine(points: List<Float>, color: Color) {
                                val path = Path()
                                points.forEachIndexed { index, value ->
                                    val x = index * xStep
                                    val y = height - ((value / 5f) * height)

                                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)

                                    drawCircle(color = color, radius = 5.dp.toPx(), center = Offset(x, y))
                                }
                                drawPath(path = path, color = color, style = Stroke(width = 2.dp.toPx()))
                            }

                            series.forEach { s -> drawDataLine(s.points, s.color) }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        xLabels.forEach { label ->
                            Text(text = label, fontSize = 9.sp, color = Color.Gray, textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                series.forEachIndexed { index, s ->
                    if (index > 0) Spacer(modifier = Modifier.width(16.dp))
                    LegendItem(s.label, s.color)
                }
            }
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 12.sp, color = color)
    }
}