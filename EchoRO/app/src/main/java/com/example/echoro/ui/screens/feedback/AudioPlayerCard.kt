package com.example.echoro.ui.screens.feedback

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AudioPlayerCard(
    audioUrl: String,
    navyBlue: Color,
    teal: Color
) {
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var isPrepared by remember { mutableStateOf(false) }

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(audioUrl) {
        val mp = MediaPlayer().apply {
            val fullUrl = if (audioUrl.startsWith("http")) audioUrl else "http://10.0.2.2:8000$audioUrl"
            setDataSource(fullUrl)
            prepareAsync()

            setOnPreparedListener {
                isPrepared = true
            }

            setOnCompletionListener {
                isPlaying = false
                progress = 1f
            }
        }
        mediaPlayer = mp

        onDispose {
            mp.release()
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying && mediaPlayer != null) {
            val mp = mediaPlayer!!
            if (mp.duration > 0) {
                progress = mp.currentPosition.toFloat() / mp.duration.toFloat()
            }
            delay(50)
        }
    }

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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
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
                    .background(navyBlue.copy(alpha = if (isPrepared) 1f else 0.5f))
                    .clickable(enabled = isPrepared) {
                        mediaPlayer?.let { mp ->
                            if (mp.isPlaying) {
                                mp.pause()
                                isPlaying = false
                            } else {
                                if (progress >= 1f) {
                                    mp.seekTo(0)
                                    progress = 0f
                                }
                                mp.start()
                                isPlaying = true
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.White.copy(alpha = if (isPrepared) 1f else 0.5f),
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Slider(
                value = progress,
                onValueChange = { newValue ->
                    progress = newValue
                    mediaPlayer?.let { mp ->
                        val newPosition = (newValue * mp.duration).toInt()
                        mp.seekTo(newPosition)
                    }
                },
                colors = SliderDefaults.colors(
                    thumbColor = teal,
                    activeTrackColor = teal,
                    inactiveTrackColor = Color(0xFFF4F6F9)
                )
            )
        }
    }
}