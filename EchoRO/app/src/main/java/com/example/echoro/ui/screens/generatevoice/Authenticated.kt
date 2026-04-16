package com.example.echoro.ui.screens.generatevoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.echoro.ui.theme.BackgroundGray
import com.example.echoro.ui.theme.NavyBlue
import com.example.echoro.ui.theme.Teal
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateVoiceScreen(
    isGuest: Boolean,
    onGenerateClick: (String) -> Unit,
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

    var speechPace by remember { mutableFloatStateOf(50f) } // 0 to 100

    var isLoading by remember { mutableStateOf(false) }

    if (isLoading) {
        LoadingScreen()

        LaunchedEffect(Unit) {
            delay(5000)
            isLoading = false
            onGenerateClick(textInput)
        }
    } else {
        Scaffold(
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
                    onValueChange = { textInput = it },
                    placeholder = {
                        Text(
                            "Type text to synthesize in romanian",
                            color = Color.Gray
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BackgroundGray,
                        unfocusedContainerColor = BackgroundGray,
                        focusedBorderColor = NavyBlue,
                        unfocusedBorderColor = Color.Transparent
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
                            isLoading = true
                        }
                    },
                    isEnabled = textInput.isNotBlank()
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}