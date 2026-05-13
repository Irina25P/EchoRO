package com.example.echoro.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.echoro.ui.screens.generatevoice.EchoRoTopBar
import com.example.echoro.ui.screens.generatevoice.LoadingScreen
import com.example.echoro.ui.theme.BackgroundGray
import com.example.echoro.ui.theme.MiniChartColor
import com.example.echoro.ui.theme.NavyBlue
import com.example.echoro.ui.theme.Teal
import com.example.echoro.viewmodel.admin.AdminEvent
import com.example.echoro.viewmodel.admin.AdminOSE
import com.example.echoro.viewmodel.admin.AdminViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel = viewModel(),
    onLogoutClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val timeFilters = listOf("7 Days", "14 Days", "30 Days", "Custom")
    var selectedFilter by remember { mutableStateOf(timeFilters[0]) }

    var showDatePicker by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()
    var customDateLabel by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.ose.collect { ose ->
            when (ose) {
                is AdminOSE.ShowMessage -> {
                    launch {
                        snackbarHostState.showSnackbar(
                            message = ose.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }

    if (state.isInitialLoading) {
        LoadingScreen()
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                        value = state.totalGenerations,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryStatCard(
                        title = "OVERALL AVERAGE\nMOS",
                        value = state.overallMos,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                SectionTitle("SUBJECTIVE METRICS COMPARISON")

                BarChartCard(
                    title = "1. INTELLIGIBILITY",
                    miniValue = state.miniIntelligibility,
                    largeValue = state.largeIntelligibility
                )
                Spacer(modifier = Modifier.height(16.dp))
                BarChartCard(
                    title = "2. NATURALNESS",
                    miniValue = state.miniNaturalness,
                    largeValue = state.largeNaturalness
                )
                Spacer(modifier = Modifier.height(16.dp))
                BarChartCard(
                    title = "3. ACCENT",
                    miniValue = state.miniAccent,
                    largeValue = state.largeAccent
                )

                Spacer(modifier = Modifier.height(32.dp))

                SectionTitle("AVERAGE WORD ACCURACY")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BackgroundGray),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AccuracyDonutChart(
                            title = "MINI MODEL",
                            percentage = state.miniWordAccuracy.toInt(),
                            color = MiniChartColor
                        )
                        AccuracyDonutChart(
                            title = "LARGE MODEL",
                            percentage = state.largeWordAccuracy.toInt(),
                            color = Teal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                SectionTitle("GENDER MATCH ACCURACY")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BackgroundGray),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AccuracyDonutChart(
                            title = "MINI MODEL",
                            percentage = state.miniGenderMatch.toInt(),
                            color = MiniChartColor
                        )
                        AccuracyDonutChart(
                            title = "LARGE MODEL",
                            percentage = state.largeGenderMatch.toInt(),
                            color = Teal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                SectionTitle("MODEL PERFORMANCE PROFILE")
                LineChartCard(
                    xLabels = listOf("Intelligibility", "Naturalness", "Accent", "Word Accuracy"),
                    miniPoints = listOf(
                        state.miniIntelligibility,
                        state.miniNaturalness,
                        state.miniAccent,
                        state.miniWordAccuracy / 20f
                    ),
                    largePoints = listOf(
                        state.largeIntelligibility,
                        state.largeNaturalness,
                        state.largeAccent,
                        state.largeWordAccuracy / 20f
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle("AVERAGE MOS TREND")
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(timeFilters) { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isSelected) Teal else BackgroundGray,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    selectedFilter = filter
                                    if (filter == "Custom") {
                                        showDatePicker = true
                                    } else {
                                        customDateLabel = ""
                                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                        val cal = Calendar.getInstance()
                                        val endDateStr = formatter.format(cal.time)

                                        when (filter) {
                                            "7 Days" -> cal.add(Calendar.DAY_OF_YEAR, -7)
                                            "14 Days" -> cal.add(Calendar.DAY_OF_YEAR, -14)
                                            "30 Days" -> cal.add(Calendar.DAY_OF_YEAR, -30)
                                        }
                                        val startDateStr = formatter.format(cal.time)
                                        viewModel.sendEvent(AdminEvent.LoadStats(startDateStr, endDateStr))
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (filter == "Custom") {
                                    Icon(
                                        imageVector = Icons.Filled.DateRange,
                                        contentDescription = "Calendar",
                                        tint = if (isSelected) Color.White else NavyBlue,
                                        modifier = Modifier.size(16.dp).padding(end = 4.dp)
                                    )
                                }
                                Text(
                                    text = if (filter == "Custom" && customDateLabel.isNotEmpty()) customDateLabel else filter,
                                    color = if (isSelected) Color.White else NavyBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isTrendLoading) {
                        CircularProgressIndicator(color = Teal)
                    } else {
                        LineChartCard(
                            xLabels = if (state.trendDates.isEmpty()) listOf("No Data") else state.trendDates,
                            miniPoints = if (state.trendMini.isEmpty()) listOf(0f) else state.trendMini,
                            largePoints = if (state.trendLarge.isEmpty()) listOf(0f) else state.trendLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = {
                        showDatePicker = false
                        if (customDateLabel.isEmpty()) selectedFilter = "7 Days"
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showDatePicker = false
                            val startMillis = dateRangePickerState.selectedStartDateMillis
                            val endMillis = dateRangePickerState.selectedEndDateMillis

                            if (startMillis != null && endMillis != null) {
                                val displayFormatter = SimpleDateFormat("dd MMM", Locale.getDefault())
                                val startDisplay = displayFormatter.format(Date(startMillis))
                                val endDisplay = displayFormatter.format(Date(endMillis))
                                customDateLabel = "$startDisplay - $endDisplay"

                                val apiFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val apiStart = apiFormatter.format(Date(startMillis))
                                val apiEnd = apiFormatter.format(Date(endMillis))

                                viewModel.sendEvent(AdminEvent.LoadStats(apiStart, apiEnd))
                            } else {
                                selectedFilter = "7 Days"
                            }
                        }) {
                            Text("Apply", color = Teal)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showDatePicker = false
                            if (customDateLabel.isEmpty()) selectedFilter = "7 Days"
                        }) {
                            Text("Cancel", color = Color.Gray)
                        }
                    }
                ) {
                    DateRangePicker(
                        state = dateRangePickerState,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}