package com.example.echoro.ui.screens.abtesting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echoro.R
import com.example.echoro.ui.screens.feedback.AudioPlayerCard
import com.example.echoro.ui.screens.generatevoice.EchoRoPrimaryButton
import com.example.echoro.ui.screens.generatevoice.EchoRoTopBar
import com.example.echoro.ui.theme.NavyBlue
import com.example.echoro.ui.theme.Teal
import com.example.echoro.viewmodel.abtesting.ABTestAnswer

enum class PreferenceOption(val label: String) {
    VOICE_A("Voice A"),
    VOICE_B("Voice B"),
    EQUAL("Equal")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ABTestingScreen(
    pageIndex: Int,
    totalCount: Int,
    modelA: String,
    modelB: String,
    audioUrlA: String?,
    audioUrlB: String?,
    text: String,
    description: String,
    isPairLoading: Boolean,
    savedAnswer: ABTestAnswer?,
    isLoading: Boolean,
    onSaveAnswer: (
        modelA: String,
        modelB: String,
        naturalness: PreferenceOption,
        intelligibility: PreferenceOption,
        accent: PreferenceOption,
        wordAccuracy: PreferenceOption
    ) -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    val isLastPage = pageIndex == totalCount - 1

    val scrollState = rememberScrollState()
    LaunchedEffect(pageIndex) {
        scrollState.animateScrollTo(0)
    }

    var naturalnessPref by remember(pageIndex) { mutableStateOf(savedAnswer?.naturalness) }
    var intelligibilityPref by remember(pageIndex) { mutableStateOf(savedAnswer?.intelligibility) }
    var accentPref by remember(pageIndex) { mutableStateOf(savedAnswer?.accent) }
    var accuracyPref by remember(pageIndex) { mutableStateOf(savedAnswer?.wordAccuracy) }

    val isFormComplete = naturalnessPref != null &&
            intelligibilityPref != null &&
            accentPref != null &&
            accuracyPref != null

    fun saveAndProceed(action: () -> Unit) {
        if (isFormComplete) {
            onSaveAnswer(modelA, modelB, naturalnessPref!!, intelligibilityPref!!, accentPref!!, accuracyPref!!)
            action()
        }
    }

    Scaffold(
        topBar = {
            EchoRoTopBar(
                actions = {
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(containerColor = Teal),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(stringResource(R.string.back_button), fontWeight = FontWeight.Bold, color = Color.White)
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
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.ab_testing_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue
            )

            Text(
                text = stringResource(R.string.ab_test_progress, pageIndex + 1, totalCount),
                fontSize = 14.sp,
                color = Teal,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = stringResource(R.string.ab_testing_instructions),
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            if (text.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFFF4F6F9)),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Text:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyBlue,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(text = text, fontSize = 14.sp, color = Color.DarkGray)
                        if (description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Style:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyBlue,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(text = description, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            Text(stringResource(R.string.voice_a_label), fontWeight = FontWeight.Bold, color = Teal, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            if (isPairLoading || audioUrlA == null) {
                Box(modifier = Modifier.fillMaxWidth().height(72.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Teal)
                }
            } else {
                key(audioUrlA) {
                    AudioPlayerCard(audioUrl = audioUrlA, navyBlue = NavyBlue, teal = Teal)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(stringResource(R.string.voice_b_label), fontWeight = FontWeight.Bold, color = Teal, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            if (isPairLoading || audioUrlB == null) {
                Box(modifier = Modifier.fillMaxWidth().height(72.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Teal)
                }
            } else {
                key(audioUrlB) {
                    AudioPlayerCard(audioUrl = audioUrlB, navyBlue = NavyBlue, teal = Teal)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = Color.LightGray)
            Spacer(modifier = Modifier.height(24.dp))

            PreferenceQuestion(
                question = stringResource(R.string.ab_naturalness_question),
                selectedOption = naturalnessPref,
                onOptionSelected = { naturalnessPref = it }
            )

            PreferenceQuestion(
                question = stringResource(R.string.ab_intelligibility_question),
                selectedOption = intelligibilityPref,
                onOptionSelected = { intelligibilityPref = it }
            )

            PreferenceQuestion(
                question = stringResource(R.string.ab_accent_question),
                selectedOption = accentPref,
                onOptionSelected = { accentPref = it }
            )

            PreferenceQuestion(
                question = stringResource(R.string.ab_word_accuracy_question),
                selectedOption = accuracyPref,
                onOptionSelected = { accuracyPref = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Teal)
            } else if (isLastPage) {
                EchoRoPrimaryButton(
                    text = stringResource(R.string.submit_comparison_button),
                    onClick = { saveAndProceed(onSubmit) },
                    isEnabled = isFormComplete
                )
            } else {
                EchoRoPrimaryButton(
                    text = stringResource(R.string.next_button_label, pageIndex + 2, totalCount),
                    onClick = { saveAndProceed(onNext) },
                    isEnabled = isFormComplete
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PreferenceQuestion(
    question: String,
    selectedOption: PreferenceOption?,
    onOptionSelected: (PreferenceOption) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = question,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = NavyBlue,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PreferenceOption.entries.forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedOption == option,
                        onClick = { onOptionSelected(option) },
                        colors = RadioButtonDefaults.colors(selectedColor = Teal, unselectedColor = Color.Gray)
                    )
                    Text(
                        text = option.label,
                        fontSize = 14.sp,
                        color = NavyBlue,
                        modifier = Modifier.clickable { onOptionSelected(option) }
                    )
                }
            }
        }
    }
}