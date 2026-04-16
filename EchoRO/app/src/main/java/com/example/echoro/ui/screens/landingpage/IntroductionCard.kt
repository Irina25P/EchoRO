package com.example.echoro.ui.screens.landingpage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echoro.ui.theme.AcademicCardInnerBg
import com.example.echoro.ui.theme.NavyBlue


@Composable
fun IntroductionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = NavyBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("BACHELOR'S THESIS", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AI-Powered Romanian Text-to-Speech Synthesis",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Developed by", color = Color.LightGray, fontSize = 12.sp)
                    Text("Păcurar Irina", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                Text("Scientific Coordinator", color = Color.LightGray, fontSize = 12.sp)
                Text("Mărginean Anca", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AcademicCardInnerBg)
                    .padding(16.dp)
            ) {
                Column {
                    Text("Technical University of Cluj-Napoca", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Faculty of Automation and Computer Science", color = Color.LightGray, fontSize = 11.sp)
                }
            }
        }
    }
}