package com.example.echoro.ui.screens.feedback

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun StarRatingCategory(title: String, currentRating: Int, onRatingSelected: (Int) -> Unit) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= currentRating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = "Star $i",
                    tint = if (i <= currentRating) Color.DarkGray else Color.LightGray,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onRatingSelected(i) }
                )
            }
        }
    }
}