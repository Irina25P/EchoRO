package com.example.echoro.ui.screens.generatevoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.echoro.R
import com.example.echoro.viewmodel.generateVoice.GenerateScreenEvent
import com.example.echoro.viewmodel.generateVoice.GenerateScreenOSE
import com.example.echoro.ui.screens.generate.GenerateViewModel
import com.example.echoro.ui.theme.BackgroundGray
import com.example.echoro.ui.theme.NavyBlue
import com.example.echoro.ui.theme.Teal
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateVoiceScreen(
    isGuest: Boolean,
    userId: Int = 1,
    viewModel: GenerateViewModel = viewModel(),
    onNavigateToFeedback: (String, String, String) -> Unit,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var textInput by remember { mutableStateOf("") }

    val modelOptions = listOf("Eagle", "Wolf", "Reindeer", "Sparrow")
    var selectedModel by remember { mutableStateOf("Eagle") }

    var selectedGender by remember { mutableStateOf("Female") }
    var speedIndex by remember { mutableIntStateOf(3) }
    var pitchIndex by remember { mutableIntStateOf(3) }
    var expressivenessIndex by remember { mutableIntStateOf(3) }
    var qualityIndex by remember { mutableIntStateOf(2) }

    val description = buildTtsDescription(selectedGender, speedIndex, pitchIndex, expressivenessIndex, qualityIndex)

    val state by viewModel.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.ose.collect { ose ->
            when (ose) {
                is GenerateScreenOSE.ShowError -> {
                    launch {
                        snackbarHostState.showSnackbar(
                            message = ose.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
                is GenerateScreenOSE.NavigateToFeedback -> {
                    onNavigateToFeedback(ose.audioUrl, ose.textUsed, ose.modelType)
                }
                else -> {}
            }
        }
    }

    if (state.isLoading) {
        LoadingScreen()
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                EchoRoTopBar(
                    actions = {
                        if (isGuest) {
                            Button(
                                onClick = onLoginClick,
                                colors = ButtonDefaults.buttonColors(containerColor = Teal),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(stringResource(R.string.login_button), fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        } else {
                            IconButton(
                                onClick = onLogoutClick,
                                modifier = Modifier.background(Teal, RoundedCornerShape(8.dp))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = stringResource(R.string.logout_cd),
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
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = textInput,
                    onValueChange = {
                        textInput = it
                        viewModel.sendEvent(GenerateScreenEvent.TextChanged(it))
                    },
                    placeholder = {
                        Text(stringResource(R.string.text_input_placeholder), color = Color.Gray)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = RoundedCornerShape(16.dp),
                    isError = state.textError != null,
                    supportingText = { if (state.textError != null) Text(state.textError!!, color = Color.Red) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BackgroundGray,
                        unfocusedContainerColor = BackgroundGray,
                        focusedBorderColor = NavyBlue,
                        unfocusedBorderColor = Color.Transparent,
                        errorContainerColor = BackgroundGray,
                        errorBorderColor = Color.Red
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                EchoRoDropdown(
                    label = stringResource(R.string.model_dropdown_label),
                    options = modelOptions,
                    selectedOption = selectedModel,
                    onOptionSelected = { selectedModel = it },
                    isEnabled = !isGuest
                )

                Spacer(modifier = Modifier.height(24.dp))

                VoiceStylesSection(
                    isGuest = isGuest,
                    selectedGender = selectedGender,
                    onGenderSelected = { selectedGender = it },
                    speedIndex = speedIndex,
                    onSpeedChanged = { speedIndex = it },
                    pitchIndex = pitchIndex,
                    onPitchChanged = { pitchIndex = it },
                    expressivenessIndex = expressivenessIndex,
                    onExpressivenessChanged = { expressivenessIndex = it },
                    qualityIndex = qualityIndex,
                    onQualityChanged = { qualityIndex = it },
                    description = description
                )

                Spacer(modifier = Modifier.height(32.dp))

                EchoRoPrimaryButton(
                    text = stringResource(R.string.generate_voice_button),
                    onClick = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendEvent(
                                GenerateScreenEvent.GenerateClicked(
                                    text = textInput,
                                    description = description,
                                    modelType = selectedModel
                                )
                            )
                        }
                    },
                    isEnabled = textInput.isNotBlank() && !state.isLoading
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

private val ttsSpeedDesc = listOf(
    "very slowly", "quite slowly", "slightly slowly", "moderate",
    "slightly fast", "quite fast", "very fast"
)
private val ttsPitchDesc = listOf(
    "very low-pitched", "quite low-pitched", "slightly low-pitched", "moderate pitch",
    "slightly high-pitched", "quite high-pitched", "very high-pitched"
)
private val ttsExpressivenessDesc = listOf(
    "very monotone", "quite monotone", "slightly monotone",
    "slightly expressive and animated", "very expressive and animated"
)
private val ttsQualityNoise = listOf("quite noisy", "slightly noisy", "clean", "very clean")
private val ttsQualityReverb = listOf(
    "quite distant-sounding", "slightly distant-sounding",
    "slightly close-sounding", "very close-sounding"
)
private val ttsQualityPesq = listOf(
    "slightly bad", "moderate",
    "good", "wonderful"
)

private fun buildTtsDescription(
    gender: String,
    speedIndex: Int,
    pitchIndex: Int,
    expressivenessIndex: Int,
    qualityIndex: Int
): String {
    return "A Romanian ${gender.lowercase()} speaker delivers a ${ttsExpressivenessDesc[expressivenessIndex]} speech " +
        "with a ${ttsSpeedDesc[speedIndex]} speed and ${ttsPitchDesc[pitchIndex]}. " +
        "The recording is ${ttsQualityNoise[qualityIndex]}, with the speaker's voice sounding " +
        "${ttsQualityReverb[qualityIndex]}. The speech quality is ${ttsQualityPesq[qualityIndex]}."
}