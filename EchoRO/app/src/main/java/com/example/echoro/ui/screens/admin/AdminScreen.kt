package com.example.echoro.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echoro.ui.screens.generatevoice.EchoRoTopBar
import com.example.echoro.ui.theme.BackgroundGray
import com.example.echoro.ui.theme.MiniChartColor
import com.example.echoro.ui.theme.NavyBlue
import com.example.echoro.ui.theme.Teal

@Composable
fun AdminDashboardScreen(
    onLogoutClick: () -> Unit
) {
    Scaffold(
        topBar = {
            EchoRoTopBar(
                actions = {
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

            Text(
                text = "EchoRO Statistics",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue
            )
            Text(
                text = "(Admin View)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = NavyBlue.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryStatCard(
                    title = "TOTAL GENERATIONS",
                    value = "1,204",
                    modifier = Modifier.weight(1f)
                )
                SummaryStatCard(
                    title = "OVERALL AVERAGE\nMOS",
                    value = "4.5",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            SectionTitle("SUBJECTIVE METRICS COMPARISON")

            BarChartCard(title = "1. INTELLIGIBILITY", miniValue = 4.2f, largeValue = 4.7f)
            Spacer(modifier = Modifier.height(16.dp))
            BarChartCard(title = "2. NATURALNESS", miniValue = 4.1f, largeValue = 4.8f)
            Spacer(modifier = Modifier.height(16.dp))
            BarChartCard(title = "3. ACCENT", miniValue = 4.0f, largeValue = 4.6f)

            Spacer(modifier = Modifier.height(32.dp))

            SectionTitle("AVERAGE WORD ACCURACY")
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BackgroundGray),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AccuracyDonutChart(title = "MINI MODEL", percentage = 88, color = MiniChartColor)
                    AccuracyDonutChart(title = "LARGE MODEL", percentage = 95, color = Teal)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            SectionTitle("MODEL PERFORMANCE PROFILE")
            LineChartCard(
                xLabels = listOf("Intelligibility", "Naturalness", "Accent", "Word Accuracy"),
                miniPoints = listOf(4.2f, 4.1f, 4.0f, 4.4f),
                largePoints = listOf(4.7f, 4.8f, 4.6f, 4.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            SectionTitle("AVERAGE MOS TREND (LAST 7 DAYS)")
            LineChartCard(
                xLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
                miniPoints = listOf(3.8f, 3.9f, 3.7f, 4.0f, 3.8f, 3.9f, 4.1f),
                largePoints = listOf(4.5f, 4.6f, 4.7f, 4.8f, 4.6f, 4.7f, 4.8f)
            )

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}