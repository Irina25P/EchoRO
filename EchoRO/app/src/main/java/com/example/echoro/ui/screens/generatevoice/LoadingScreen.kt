package com.example.echoro.ui.screens.generatevoice

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echoro.R
import com.example.echoro.ui.theme.NavyBlue
import com.example.echoro.ui.theme.Teal

@Composable
fun LoadingScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_echoro),
            contentDescription = "EchoRO Logo",
            modifier = Modifier.fillMaxWidth(0.5f)
        )

        Spacer(modifier = Modifier.height(64.dp))

        CircularProgressIndicator(
            color = NavyBlue,
            strokeWidth = 4.dp,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Generating audio with trained Parler-TTS model",
            color = NavyBlue,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Estimated time: 5-15 seconds",
            color = Teal,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}
