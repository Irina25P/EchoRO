package com.example.echoro.ui.screens.generatevoice

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echoro.ui.theme.BackgroundGray
import com.example.echoro.ui.theme.DisabledText
import com.example.echoro.ui.theme.NavyBlue
import com.example.echoro.ui.theme.Teal


@Composable
fun VoiceStylesSection(
    isGuest: Boolean,
    genderOptions: List<String>, selectedGender: String, onGenderSelected: (String) -> Unit,
    moodOptions: List<String>, selectedMood: String, onMoodSelected: (String) -> Unit,
    speechPace: Float, onPaceChanged: (Float) -> Unit
) {
    val alpha = if (isGuest) 0.5f else 1f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundGray.copy(alpha = alpha))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voice Styles", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyBlue.copy(alpha = alpha))

            if (isGuest) {
                Surface(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PRO Only", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        EchoRoDropdown(
            label = "Gender",
            options = genderOptions,
            selectedOption = selectedGender,
            onOptionSelected = onGenderSelected,
            isEnabled = !isGuest
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("Mood", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            moodOptions.forEach { mood ->
                val isSelected = mood == selectedMood
                FilterChip(
                    selected = isSelected && !isGuest,
                    onClick = { if (!isGuest) onMoodSelected(mood) },
                    label = { Text(mood.replaceFirstChar { it.uppercase() }) },
                    enabled = !isGuest,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Teal,
                        selectedLabelColor = Color.White,
                        disabledContainerColor = Color.Transparent,
                        disabledLabelColor = DisabledText
                    ),
                    trailingIcon = if (isGuest) {
                        { Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Speech Pace", color = Color.Gray, fontSize = 14.sp)
            Text("${speechPace.toInt()}%", color = if (isGuest) DisabledText else NavyBlue, fontWeight = FontWeight.Bold)
        }

        Slider(
            value = speechPace,
            onValueChange = onPaceChanged,
            valueRange = 0f..100f,
            enabled = !isGuest,
            colors = SliderDefaults.colors(
                thumbColor = Teal,
                activeTrackColor = Teal,
                disabledThumbColor = Color.LightGray,
                disabledActiveTrackColor = Color.LightGray
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Slow", color = Color.LightGray, fontSize = 12.sp)
            Text("Fast", color = Color.LightGray, fontSize = 12.sp)
        }
    }
}