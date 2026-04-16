package com.example.echoro.ui.screens.landingpage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echoro.ui.theme.NavyBlue
import com.example.echoro.ui.theme.PinkAccent
import com.example.echoro.ui.theme.Teal
import com.example.echoro.ui.theme.TextGray


@Composable
fun FeaturesSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FeatureCard(
            icon = Icons.Default.Bolt,
            iconBgColor = NavyBlue,
            title = "Dual AI Models",
            description = buildAnnotatedString {
                append("Fast ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Mini Model") }
                append(" for instant guest access, or highly accurate ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Large Model") }
                append(" with advanced voice customization for Pro users.")
            }
        )

        FeatureCard(
            icon = Icons.Default.Mic,
            iconBgColor = Teal,
            title = "High-Fidelity Audio",
            description = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Natural intonation") }
                append(" and ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("perfect Romanian pronunciation") }
                append(" powered by Parler-TTS with context and emotion understanding.")
            }
        )

        FeatureCard(
            icon = Icons.Default.BarChart,
            iconBgColor = PinkAccent,
            title = "Academic Evaluation",
            description = buildAnnotatedString {
                append("Built-in ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("MOS (Mean Opinion Score)") }
                append(" feedback system for research evaluation and advancing Romanian TTS technology.")
            }
        )
    }
}

@Composable
fun FeatureCard(icon: ImageVector, iconBgColor: Color, title: String, description: androidx.compose.ui.text.AnnotatedString) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyBlue)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = description, fontSize = 14.sp, color = TextGray, lineHeight = 20.sp)
        }
    }
}