package com.example.echoro.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echoro.ui.theme.BackgroundGray
import com.example.echoro.ui.theme.GridLineColor
import com.example.echoro.ui.theme.MiniChartColor
import com.example.echoro.ui.theme.ReindeerChartColor
import com.example.echoro.ui.theme.SparrowChartColor
import com.example.echoro.ui.theme.Teal


@Composable
fun BarChartCard(title: String, values: List<Pair<String, Float>>) {
    var selectedIndex by remember { mutableIntStateOf(-1) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BackgroundGray),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    for (i in 5 downTo 0) {
                        Text(text = i.toString(), fontSize = 10.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (i in 0..5) {
                            HorizontalDivider(color = GridLineColor, thickness = 1.dp)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val modelColors = listOf(Teal, MiniChartColor, ReindeerChartColor, SparrowChartColor)
                        values.forEachIndexed { index, (label, value) ->
                            BarColumn(
                                value = value,
                                label = label,
                                color = modelColors[index % modelColors.size],
                                isSelected = selectedIndex == index,
                                onClick = {
                                    selectedIndex = if (selectedIndex == index) -1 else index
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BarColumn(
    value: Float,
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val heightFraction = (value / 5f).coerceIn(0f, 1f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .width(50.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Empty space above the bar
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight((1f - heightFraction).coerceAtLeast(0.0001f))
            )

            // The bar itself; value is drawn at the top edge so it is always visible
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(heightFraction.coerceAtLeast(0.0001f))
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(color),
                contentAlignment = Alignment.TopCenter
            ) {
                if (isSelected) {
                    Text(
                        text = "%.1f".format(value),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )
    }
}