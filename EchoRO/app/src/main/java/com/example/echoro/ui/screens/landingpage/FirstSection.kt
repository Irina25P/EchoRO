package com.example.echoro.ui.screens.landingpage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echoro.R
import com.example.echoro.ui.theme.NavyBlue
import com.example.echoro.ui.theme.Teal
import com.example.echoro.ui.theme.TextGray


@Composable
fun FirstSection(
    onGuestClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onABTestClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Teal),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val heights = listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.6f, 1f, 0.5f, 0.8f, 0.4f, 0.9f, 0.7f, 0.3f, 0.8f, 0.6f, 0.9f, 0.5f, 0.7f)
            heights.forEach { heightMultiplier ->
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight(heightMultiplier)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.8f))
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = stringResource(R.string.landing_main_title),
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
        color = NavyBlue,
        textAlign = TextAlign.Center,
        lineHeight = 34.sp
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = stringResource(R.string.landing_description),
        fontSize = 14.sp,
        color = TextGray,
        textAlign = TextAlign.Center,
        lineHeight = 20.sp
    )

    Spacer(modifier = Modifier.height(24.dp))

    LandingButton(
        text = stringResource(R.string.try_guest_button),
        icon = Icons.Default.AutoAwesome,
        isOutlined = false,
        onClick = onGuestClick
    )

    Spacer(modifier = Modifier.height(12.dp))

    LandingButton(
        text = stringResource(R.string.login_register_button),
        icon = null,
        isOutlined = true,
        onClick = onRegisterClick
    )

    Spacer(modifier = Modifier.height(12.dp))

    LandingButton(
        text = stringResource(R.string.try_ab_testing_button),
        icon = null,
        isOutlined = false,
        onClick = onABTestClick
    )
}
