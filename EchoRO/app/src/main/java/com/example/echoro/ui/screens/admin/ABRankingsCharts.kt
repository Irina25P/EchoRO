package com.example.echoro.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echoro.R
import com.example.echoro.network.MeasureRankingData
import com.example.echoro.network.ModelRanking
import com.example.echoro.ui.theme.BackgroundGray
import com.example.echoro.ui.theme.MiniChartColor
import com.example.echoro.ui.theme.NavyBlue
import com.example.echoro.ui.theme.PinkAccent
import com.example.echoro.ui.theme.ReindeerChartColor
import com.example.echoro.ui.theme.SparrowChartColor
import com.example.echoro.ui.theme.Teal

private val AB_MODELS_ORDER = listOf("Eagle", "Reindeer", "Sparrow", "Wolf")

private val MODEL_COLORS_BY_NAME = mapOf(
    "Eagle" to Teal,
    "Wolf" to MiniChartColor,
    "Reindeer" to ReindeerChartColor,
    "Sparrow" to SparrowChartColor
)

private val MODEL_COLORS = AB_MODELS_ORDER.map { MODEL_COLORS_BY_NAME.getValue(it) }

private fun modelColor(model: String): Color =
    MODEL_COLORS_BY_NAME[model] ?: Teal

// ---------------------------------------------------------------------------
// Top-level: all 4 measures in one collapsible card
// ---------------------------------------------------------------------------

@Composable
fun ABRankingsSection(
    totalTrials: Int,
    rankings: Map<String, MeasureRankingData>
) {
    val measures = listOf(
        "naturalness" to stringResource(R.string.naturalness_label),
        "intelligibility" to stringResource(R.string.intelligibility_label),
        "accent" to stringResource(R.string.accent_label),
        "word_accuracy" to stringResource(R.string.word_accuracy_short)
    )

    SectionTitle(stringResource(R.string.ab_testing_results_section))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryStatCard(
            title = stringResource(R.string.admin_total_ab_answers),
            value = totalTrials.toString(),
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    measures.forEach { (key, label) ->
        val data = rankings[key]
        if (data != null) {
            MeasureRankingCard(measureLabel = label, data = data)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// Per-measure card: win-rate matrix + BT ranking bar chart
// ---------------------------------------------------------------------------

@Composable
fun MeasureRankingCard(measureLabel: String, data: MeasureRankingData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BackgroundGray),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = measureLabel.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Win-rate matrix
            Text(
                text = stringResource(R.string.ab_win_rate_matrix_title),
                fontSize = 11.sp,
                color = Color.Gray,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            WinRateMatrix(
                winRates = data.win_rates,
                significance = data.significance
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            // Bradley-Terry ranking bars
            BTRankingBars(rankings = data.rankings)

            // Model color legend
            Spacer(modifier = Modifier.height(12.dp))
            ModelColorLegend()
        }
    }
}

// ---------------------------------------------------------------------------
// Win-rate matrix (4×4 heatmap)
// ---------------------------------------------------------------------------

@Composable
fun WinRateMatrix(
    winRates: Map<String, Map<String, Float>>,
    significance: Map<String, Map<String, Boolean>>
) {
    val models = AB_MODELS_ORDER
    val cellSize = 52.dp
    val headerSize = 48.dp

    Column {
        // Header row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(headerSize))  // top-left corner empty
            models.forEach { col ->
                Box(
                    modifier = Modifier
                        .size(cellSize)
                        .padding(1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = col.take(3),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = modelColor(col),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Data rows
        models.forEach { rowModel ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Row label
                Box(
                    modifier = Modifier.size(headerSize),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = rowModel.take(3),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = modelColor(rowModel),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
                models.forEach { colModel ->
                    if (rowModel == colModel) {
                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .padding(1.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.LightGray.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("—", fontSize = 12.sp, color = Color.Gray)
                        }
                    } else {
                        val rate = winRates[rowModel]?.get(colModel) ?: 50f
                        val isSig = significance[rowModel]?.get(colModel) ?: false
                        WinRateCell(rate = rate, isSignificant = isSig, modifier = Modifier.size(cellSize))
                    }
                }
            }
        }
    }
}

@Composable
fun WinRateCell(rate: Float, isSignificant: Boolean, modifier: Modifier = Modifier) {
    val fraction = (rate / 100f).coerceIn(0f, 1f)
    val bgColor = lerp(
        start = PinkAccent.copy(alpha = 0.7f),  // app pink for low win-rate
        stop = Teal.copy(alpha = 0.85f),        // app teal for high win-rate
        fraction = fraction
    )
    val nsColor = if (!isSignificant) Color.White.copy(alpha = 0.6f) else Color.Transparent
    Box(
        modifier = modifier
            .padding(1.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${rate.toInt()}%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            if (!isSignificant) {
                Text(
                    text = stringResource(R.string.ab_ns_label),
                    fontSize = 8.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Bradley-Terry Elo bar chart
// ---------------------------------------------------------------------------

@Composable
fun BTRankingBars(rankings: List<ModelRanking>) {
    if (rankings.isEmpty()) return

    val maxElo = (rankings.maxOfOrNull { it.elo } ?: 1100f).coerceAtLeast(1001f)
    val minElo = (rankings.minOfOrNull { it.elo } ?: 900f).coerceAtMost(999f)
    val range = (maxElo - minElo).coerceAtLeast(50f)

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rankings.forEach { ranking ->
            val fraction = ((ranking.elo - minElo) / range).coerceIn(0.1f, 1f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Model name
                Text(
                    text = ranking.model,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = modelColor(ranking.model),
                    modifier = Modifier.width(56.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Bar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .height(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(modelColor(ranking.model))
                    )
                    // CI error markers
                    val ciFraction = ((ranking.ci_low - minElo) / range).coerceIn(0f, 1f)
                    val ciFractionHigh = ((ranking.ci_high - minElo) / range).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(ciFractionHigh)
                            .height(20.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        // CI high tick
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(20.dp)
                                .background(Color.White.copy(alpha = 0.8f))
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(ciFraction)
                            .height(20.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        // CI low tick
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(20.dp)
                                .background(Color.White.copy(alpha = 0.8f))
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Elo value + CI
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(80.dp)) {
                    Text(
                        text = "${stringResource(R.string.ab_elo_label)} ${ranking.elo.toInt()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue
                    )
                    Text(
                        text = "[${ranking.ci_low.toInt()}, ${ranking.ci_high.toInt()}]",
                        fontSize = 8.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Legend
// ---------------------------------------------------------------------------

@Composable
fun ModelColorLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AB_MODELS_ORDER.forEachIndexed { i, model ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(MODEL_COLORS[i], RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = model, fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}
