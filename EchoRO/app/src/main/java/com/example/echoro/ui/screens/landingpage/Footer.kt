package com.example.echoro.ui.screens.landingpage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echoro.ui.theme.NavyBlue

@Composable
fun LandingFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavyBlue)
            .padding(vertical = 32.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("EchoRO", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("AI-Powered Romanian TTS", color = Color.LightGray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Text("© 2026 EchoRO - Bachelor's Thesis", color = Color.LightGray, fontSize = 10.sp)
        Text("Technical University of Cluj-Napoca", color = Color.LightGray, fontSize = 10.sp)
        Text("Computer Science", color = Color.LightGray, fontSize = 10.sp)
    }
}
