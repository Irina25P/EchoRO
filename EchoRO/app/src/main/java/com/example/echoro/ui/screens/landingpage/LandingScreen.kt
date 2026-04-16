package com.example.echoro.ui.screens.landingpage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echoro.ui.screens.generatevoice.EchoRoTopBar
import com.example.echoro.ui.theme.LightGrayBg
import com.example.echoro.ui.theme.NavyBlue
import com.example.echoro.ui.theme.Teal

@Composable
fun LandingScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onGuestClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(rememberScrollState())
    ) {
        EchoRoTopBar(
            actions = {
                Text(
                    text = "Login",
                    color = Color.White,
                    modifier = Modifier
                        .clickable { onLoginClick() }
                        .padding(end = 16.dp)
                )

                Button(
                    onClick = onRegisterClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Teal),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Register", fontWeight = FontWeight.Bold)
                }
            }
        )

        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            FirstSection(onGuestClick = onGuestClick, onRegisterClick = onRegisterClick)

            Spacer(modifier = Modifier.height(48.dp))

            IntroductionCard()

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Powerful Features",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue
            )
            Spacer(modifier = Modifier.height(24.dp))

            FeaturesSection()

            Spacer(modifier = Modifier.height(48.dp))
        }

        LandingFooter()
    }
}
