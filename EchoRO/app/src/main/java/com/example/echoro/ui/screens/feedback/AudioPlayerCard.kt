package com.example.echoro.ui.screens.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun AudioPlayerCard(navyBlue: Color, teal: Color) {
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0.1f) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(60.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val heights = listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.6f, 1f, 0.5f, 0.8f, 0.4f, 0.9f, 0.7f, 0.3f, 0.8f, 0.6f, 0.9f, 0.5f, 0.7f)
                heights.forEachIndexed { index, heightMultiplier ->
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .fillMaxHeight(heightMultiplier)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (index % 2 == 0) navyBlue.copy(alpha = 0.6f) else teal.copy(alpha = 0.6f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(navyBlue)
                    .clickable { isPlaying = !isPlaying },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Slider(
                value = progress,
                onValueChange = { progress = it },
                colors = SliderDefaults.colors(
                    thumbColor = teal,
                    activeTrackColor = teal,
                    inactiveTrackColor = Color(0xFFF4F6F9)
                )
            )
        }
    }
}