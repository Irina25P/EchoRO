package com.example.echoro.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.echoro.R
import com.example.echoro.ui.screens.generatevoice.EchoRoTopBar
import com.example.echoro.ui.screens.generatevoice.LoadingScreen
import com.example.echoro.ui.theme.BackgroundGray
import com.example.echoro.ui.theme.MiniChartColor
import com.example.echoro.ui.theme.NavyBlue
import com.example.echoro.ui.theme.ReindeerChartColor
import com.example.echoro.ui.theme.SparrowChartColor
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

    val timeFilters = listOf(
        stringResource(R.string.admin_filter_7_days),
        stringResource(R.string.admin_filter_14_days),
        stringResource(R.string.admin_filter_30_days),
        stringResource(R.string.admin_filter_custom)
    )
    val strCustom = stringResource(R.string.admin_filter_custom)
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
                    text = stringResource(R.string.admin_statistics_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
                Text(
                    text = stringResource(R.string.admin_view_subtitle),
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
                        title = stringResource(R.string.admin_total_generations),
                        value = state.totalGenerations,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryStatCard(
                        title = stringResource(R.string.admin_overall_mos),
                        value = state.overallMos,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                SectionTitle(stringResource(R.string.admin_subjective_metrics))

                BarChartCard(
                    title = stringResource(R.string.admin_intelligibility_chart),
                    values = listOf(
                        "Eagle" to state.eagleIntelligibility,
                        "Wolf" to state.wolfIntelligibility,
                        "Reindeer" to state.reindeerIntelligibility,
                        "Sparrow" to state.sparrowIntelligibility
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                BarChartCard(
                    title = stringResource(R.string.admin_naturalness_chart),
                    values = listOf(
                        "Eagle" to state.eagleNaturalness,
                        "Wolf" to state.wolfNaturalness,
                        "Reindeer" to state.reindeerNaturalness,
                        "Sparrow" to state.sparrowNaturalness
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                BarChartCard(
                    title = stringResource(R.string.admin_accent_chart),
                    values = listOf(
                        "Eagle" to state.eagleAccent,
                        "Wolf" to state.wolfAccent,
                        "Reindeer" to state.reindeerAccent,
                        "Sparrow" to state.sparrowAccent
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                SectionTitle(stringResource(R.string.admin_word_accuracy_section))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BackgroundGray),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            AccuracyDonutChart("Eagle",    state.eagleWordAccuracy.toInt(),    Teal)
                            AccuracyDonutChart("Wolf",     state.wolfWordAccuracy.toInt(),     MiniChartColor)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            AccuracyDonutChart("Reindeer", state.reindeerWordAccuracy.toInt(), ReindeerChartColor)
                            AccuracyDonutChart("Sparrow",  state.sparrowWordAccuracy.toInt(),  SparrowChartColor)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                SectionTitle(stringResource(R.string.admin_gender_match_section))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BackgroundGray),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            AccuracyDonutChart("Eagle",    state.eagleGenderMatch.toInt(),    Teal)
                            AccuracyDonutChart("Wolf",     state.wolfGenderMatch.toInt(),     MiniChartColor)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            AccuracyDonutChart("Reindeer", state.reindeerGenderMatch.toInt(), ReindeerChartColor)
                            AccuracyDonutChart("Sparrow",  state.sparrowGenderMatch.toInt(),  SparrowChartColor)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                SectionTitle(stringResource(R.string.admin_model_profile_section))
                LineChartCard(
                    xLabels = listOf("Intelligibility", "Naturalness", "Accent", "Word Accuracy"),
                    series = listOf(
                        ChartSeries("Eagle",    listOf(state.eagleIntelligibility,    state.eagleNaturalness,    state.eagleAccent,    state.eagleWordAccuracy    / 20f), Teal),
                        ChartSeries("Wolf",     listOf(state.wolfIntelligibility,     state.wolfNaturalness,     state.wolfAccent,     state.wolfWordAccuracy     / 20f), MiniChartColor),
                        ChartSeries("Reindeer", listOf(state.reindeerIntelligibility, state.reindeerNaturalness, state.reindeerAccent, state.reindeerWordAccuracy / 20f), ReindeerChartColor),
                        ChartSeries("Sparrow",  listOf(state.sparrowIntelligibility,  state.sparrowNaturalness,  state.sparrowAccent,  state.sparrowWordAccuracy  / 20f), SparrowChartColor)
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle(stringResource(R.string.admin_mos_trend_section))
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
                                if (filter == strCustom) {
                                    Icon(
                                        imageVector = Icons.Filled.DateRange,
                                        contentDescription = stringResource(R.string.calendar_cd),
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
                            xLabels = if (state.trendDates.isEmpty()) listOf(stringResource(R.string.no_data_label)) else state.trendDates,
                            series = listOf(
                                ChartSeries("Eagle",    if (state.trendEagle.isEmpty())    listOf(0f) else state.trendEagle,    Teal),
                                ChartSeries("Wolf",     if (state.trendWolf.isEmpty())     listOf(0f) else state.trendWolf,     MiniChartColor),
                                ChartSeries("Reindeer", if (state.trendReindeer.isEmpty()) listOf(0f) else state.trendReindeer, ReindeerChartColor),
                                ChartSeries("Sparrow",  if (state.trendSparrow.isEmpty())  listOf(0f) else state.trendSparrow,  SparrowChartColor)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                SectionTitle(stringResource(R.string.ab_testing_results_section))

                SummaryStatCard(
                    title = stringResource(R.string.admin_total_ab_answers),
                    value = state.abTotalResults.toString(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BackgroundGray),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = stringResource(R.string.ab_preference_description),
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        ABPreferenceRow(stringResource(R.string.naturalness_label),    state.abNaturalnessVoiceAPct,    state.abNaturalnessVoiceBPct,    state.abNaturalnessEqualPct)
                        ABPreferenceRow(stringResource(R.string.intelligibility_label), state.abIntelligibilityVoiceAPct, state.abIntelligibilityVoiceBPct, state.abIntelligibilityEqualPct)
                        ABPreferenceRow(stringResource(R.string.accent_label),          state.abAccentVoiceAPct,          state.abAccentVoiceBPct,          state.abAccentEqualPct)
                        ABPreferenceRow(stringResource(R.string.word_accuracy_short),   state.abWordAccuracyVoiceAPct,    state.abWordAccuracyVoiceBPct,    state.abWordAccuracyEqualPct)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                ABRankingsSection(
                    totalTrials = state.abTotalTrials,
                    rankings = state.abRankings
                )

                Spacer(modifier = Modifier.height(48.dp))
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = {
                        showDatePicker = false
                        if (customDateLabel.isEmpty()) selectedFilter = timeFilters[0]
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
                                selectedFilter = timeFilters[0]
                            }
                        }) {
                            Text(stringResource(R.string.apply_button), color = Teal)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showDatePicker = false
                            if (customDateLabel.isEmpty()) selectedFilter = timeFilters[0]
                        }) {
                            Text(stringResource(R.string.cancel_button), color = Color.Gray)
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

@Composable
fun ABPreferenceRow(label: String, voiceAPct: Float, voiceBPct: Float, equalPct: Float) {
    val colorA = Teal
    val colorB = MiniChartColor
    val colorEqual = Color(0xFFB0BEC5)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp)
    ) {
        Text(text = label, fontSize = 13.sp, color = NavyBlue, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 6.dp))

        // Segmented bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
        ) {
            if (voiceAPct > 0f) Box(Modifier.fillMaxHeight().weight(voiceAPct).background(colorA))
            if (voiceBPct > 0f) Box(Modifier.fillMaxHeight().weight(voiceBPct).background(colorB))
            val remainWeight = maxOf(100f - voiceAPct - voiceBPct, 0f)
            if (remainWeight > 0f) Box(Modifier.fillMaxHeight().weight(remainWeight).background(colorEqual))
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LegendDot(color = colorA, label = stringResource(R.string.ab_legend_a_format, voiceAPct))
            LegendDot(color = colorB, label = stringResource(R.string.ab_legend_b_format, voiceBPct))
            LegendDot(color = colorEqual, label = stringResource(R.string.ab_legend_equal_format, equalPct))
        }
    }
}

@Composable
fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, shape = RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
    }
}