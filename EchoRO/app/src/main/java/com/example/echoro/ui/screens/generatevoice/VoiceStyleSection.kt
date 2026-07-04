package com.example.echoro.ui.screens.generatevoice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echoro.R
import com.example.echoro.ui.theme.BackgroundGray
import com.example.echoro.ui.theme.DisabledText
import com.example.echoro.ui.theme.NavyBlue
import com.example.echoro.ui.theme.Teal
import kotlin.math.roundToInt

private val speedLabels = listOf(
    "Very Slow", "Slow", "Slightly Slow", "Moderate", "Slightly Fast", "Fast", "Very Fast"
)
private val pitchLabels = listOf(
    "Very Low", "Low", "Slightly Low", "Moderate", "Slightly High", "High", "Very High"
)
private val expressivenessLabels = listOf(
    "Very Monotone", "Monotone", "Slightly Monotone", "Slightly Expressive", "Very Expressive"
)
private val qualityLabels = listOf("Lo-fi", "Room", "Clean", "Studio")

@Composable
fun VoiceStylesSection(
    isGuest: Boolean,
    selectedGender: String,
    onGenderSelected: (String) -> Unit,
    speedIndex: Int,
    onSpeedChanged: (Int) -> Unit,
    pitchIndex: Int,
    onPitchChanged: (Int) -> Unit,
    expressivenessIndex: Int,
    onExpressivenessChanged: (Int) -> Unit,
    qualityIndex: Int,
    onQualityChanged: (Int) -> Unit,
    description: String
) {
    val alpha = if (isGuest) 0.5f else 1f
    var previewExpanded by remember { mutableStateOf(false) }

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
            Text(
                stringResource(R.string.voice_styles_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue.copy(alpha = alpha)
            )
            if (isGuest) {
                Surface(color = Color.LightGray, shape = RoundedCornerShape(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            stringResource(R.string.pro_only_badge),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(stringResource(R.string.gender_label), color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        GenderToggle(
            selected = selectedGender,
            onSelected = { if (!isGuest) onGenderSelected(it) },
            isEnabled = !isGuest
        )

        Spacer(modifier = Modifier.height(24.dp))

        DiscreteSliderRow(
            label = stringResource(R.string.speed_label),
            valueLabel = speedLabels[speedIndex],
            index = speedIndex,
            maxIndex = 6,
            onIndexChanged = onSpeedChanged,
            isEnabled = !isGuest
        )

        Spacer(modifier = Modifier.height(24.dp))

        DiscreteSliderRow(
            label = stringResource(R.string.pitch_label),
            valueLabel = pitchLabels[pitchIndex],
            index = pitchIndex,
            maxIndex = 6,
            onIndexChanged = onPitchChanged,
            isEnabled = !isGuest
        )

        Spacer(modifier = Modifier.height(24.dp))

        DiscreteSliderRow(
            label = stringResource(R.string.expressiveness_label),
            valueLabel = expressivenessLabels[expressivenessIndex],
            index = expressivenessIndex,
            maxIndex = 4,
            onIndexChanged = onExpressivenessChanged,
            isEnabled = !isGuest
        )

        Spacer(modifier = Modifier.height(24.dp))

        DiscreteSliderRow(
            label = stringResource(R.string.recording_quality_label),
            valueLabel = qualityLabels[qualityIndex],
            index = qualityIndex,
            maxIndex = 3,
            onIndexChanged = onQualityChanged,
            isEnabled = !isGuest
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { previewExpanded = !previewExpanded }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.description_preview_label),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = NavyBlue.copy(alpha = alpha)
            )
            Icon(
                imageVector = if (previewExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = NavyBlue.copy(alpha = alpha)
            )
        }

        AnimatedVisibility(visible = previewExpanded) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color.DarkGray.copy(alpha = alpha),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = alpha))
                        .border(1.dp, Color.LightGray.copy(alpha = alpha), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun GenderToggle(
    selected: String,
    onSelected: (String) -> Unit,
    isEnabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
    ) {
        listOf("Female", "Male").forEach { gender ->
            val isSelected = gender == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected && isEnabled) Teal else Color.Transparent)
                    .clickable(enabled = isEnabled) { onSelected(gender) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = gender,
                    color = when {
                        isSelected && isEnabled -> Color.White
                        !isEnabled -> DisabledText
                        else -> NavyBlue
                    },
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun DiscreteSliderRow(
    label: String,
    valueLabel: String,
    index: Int,
    maxIndex: Int,
    onIndexChanged: (Int) -> Unit,
    isEnabled: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = Color.Gray, fontSize = 14.sp)
            Text(
                valueLabel,
                color = if (isEnabled) NavyBlue else DisabledText,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Slider(
            value = index.toFloat(),
            onValueChange = { onIndexChanged(it.roundToInt()) },
            valueRange = 0f..maxIndex.toFloat(),
            steps = maxIndex - 1,
            enabled = isEnabled,
            colors = SliderDefaults.colors(
                thumbColor = Teal,
                activeTrackColor = Teal,
                inactiveTrackColor = Color.LightGray,
                disabledThumbColor = Color.LightGray,
                disabledActiveTrackColor = Color.LightGray
            )
        )
    }
}