package com.example.echoro.ui.screens.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echoro.ui.screens.generatevoice.EchoRoPrimaryButton
import com.example.echoro.ui.screens.generatevoice.EchoRoTopBar
import com.example.echoro.ui.theme.BackgroundGray
import com.example.echoro.ui.theme.NavyBlue
import com.example.echoro.ui.theme.Teal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    isGuest: Boolean = false,
    onLogoutClick: () -> Unit = {},
    onSubmitFeedback: (Int, Int, Int, Float, String) -> Unit
) {

    var intelligibility by remember { mutableIntStateOf(0) }
    var naturalness by remember { mutableIntStateOf(0) }
    var accent by remember { mutableIntStateOf(0) }
    var wordAccuracy by remember { mutableFloatStateOf(85f) }
    var comments by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            EchoRoTopBar(
                actions = {
                    if (isGuest) {
                        Button(
                            onClick = onLogoutClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Teal),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Login", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    } else {
                        IconButton(
                            onClick = onLogoutClick,
                            modifier = Modifier.background(Teal, RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            AudioPlayerCard(navyBlue = NavyBlue, teal = Teal)

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Detailed Feedback (including MOS)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue
            )

            Spacer(modifier = Modifier.height(24.dp))

            StarRatingCategory("1. Intelligibility", intelligibility) { intelligibility = it }
            Spacer(modifier = Modifier.height(16.dp))

            StarRatingCategory("2. Naturalness", naturalness) { naturalness = it }
            Spacer(modifier = Modifier.height(16.dp))

            StarRatingCategory("3. Accent", accent) { accent = it }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Percentage of words pronounced correctly",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = NavyBlue
            )
            Text(
                text = "Slide to indicate the percentage of correctly pronounced words",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Slider(
                    value = wordAccuracy,
                    onValueChange = { wordAccuracy = it },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = Teal,
                        activeTrackColor = Teal,
                        inactiveTrackColor = BackgroundGray
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "${wordAccuracy.toInt()}%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Teal,
                    modifier = Modifier.width(45.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 60.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0%", color = Color.Gray, fontSize = 12.sp)
                Text("100%", color = Color.Gray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = comments,
                onValueChange = { comments = it },
                placeholder = { Text("Optional comments...", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BackgroundGray,
                    unfocusedContainerColor = BackgroundGray,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            EchoRoPrimaryButton(
                text = "Submit Feedback",
                onClick = {
                    onSubmitFeedback(intelligibility, naturalness, accent, wordAccuracy, comments)
                },
                isEnabled = intelligibility > 0 && naturalness > 0 && accent > 0
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
