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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

    val modelOptions = listOf("Mini", "Large")
    var selectedModel by remember { mutableStateOf(if (isGuest) "Mini" else "Large") }

    val genderOptions = listOf("Female", "Male")
    var selectedGender by remember { mutableStateOf("Female") }

    val moodOptions = listOf("clear", "natural", "steady", "professional", "calm", "crisp", "warm")
    var selectedMood by remember { mutableStateOf("natural") }

    var speechPace by remember { mutableFloatStateOf(50f) }

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
                        Text("Type text to synthesize in romanian", color = Color.Gray)
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
                    label = "Model",
                    options = modelOptions,
                    selectedOption = selectedModel,
                    onOptionSelected = { selectedModel = it },
                    isEnabled = !isGuest
                )

                Spacer(modifier = Modifier.height(24.dp))

                VoiceStylesSection(
                    isGuest = isGuest,
                    genderOptions = genderOptions,
                    selectedGender = selectedGender,
                    onGenderSelected = { selectedGender = it },
                    moodOptions = moodOptions,
                    selectedMood = selectedMood,
                    onMoodSelected = { selectedMood = it },
                    speechPace = speechPace,
                    onPaceChanged = { speechPace = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                EchoRoPrimaryButton(
                    text = "Generate Voice",
                    onClick = {
                        if (textInput.isNotBlank()) {
                            val paceWord = when {
                                speechPace < 25f -> "slow"
                                speechPace > 75f -> "fast"
                                else -> "fluent"
                            }
                            val dynamicDescription = "A ${selectedGender.lowercase()} Romanian speaker delivers the text with a $selectedMood voice, $paceWord pace in a clean background."

                            viewModel.sendEvent(
                                GenerateScreenEvent.GenerateClicked(
                                    text = textInput,
                                    description = dynamicDescription,
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