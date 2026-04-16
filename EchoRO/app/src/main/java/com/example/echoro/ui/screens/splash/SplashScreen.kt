package com.example.echoro.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echoro.R
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(onNavigateNext: () -> Unit) {
    val navyBlue = Color(0xFF283270)

    LaunchedEffect(key1 = true) {
        delay(2500)
        onNavigateNext()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = navyBlue
        )

        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_echoro),
            contentDescription = "EchoRO Logo",
            modifier = Modifier.fillMaxWidth(0.5f)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Romanian Text-to-Speech via Parler-TTS",
            fontSize = 16.sp,
            color = navyBlue,
            fontWeight = FontWeight.Normal
        )
    }
}
