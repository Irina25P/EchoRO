package com.example.echoro.ui.screens.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.echoro.R
import com.example.echoro.viewmodel.feedback.FeedbackEvent
import com.example.echoro.viewmodel.feedback.FeedbackOSE
import com.example.echoro.viewmodel.feedback.FeedbackViewModel
import com.example.echoro.ui.screens.generatevoice.EchoRoPrimaryButton
import com.example.echoro.ui.screens.generatevoice.EchoRoTopBar
import com.example.echoro.ui.theme.BackgroundGray
import com.example.echoro.ui.theme.NavyBlue
import com.example.echoro.ui.theme.Teal
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    audioUrl: String,
    textUsed: String,
    modelType: String,
    isGuest: Boolean = false,
    userId: Int = 1,
    viewModel: FeedbackViewModel = viewModel(),
    onLogoutClick: () -> Unit = {},
    onNavigateBack: () -> Unit
) {
    var intelligibility by remember { mutableIntStateOf(0) }
    var naturalness by remember { mutableIntStateOf(0) }
    var accent by remember { mutableIntStateOf(0) }
    var wordAccuracy by remember { mutableFloatStateOf(85f) }
    var comments by remember { mutableStateOf("") }
    var genderRespected by remember { mutableStateOf<Boolean?>(null) }

    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.ose.collect { ose ->
            when (ose) {
                is FeedbackOSE.ShowMessage -> {
                    launch {
                        snackbarHostState.showSnackbar(
                            message = ose.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
                is FeedbackOSE.NavigateBack -> {
                    onNavigateBack()
                }
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            EchoRoTopBar(
                actions = {
                    if (isGuest) {
                        Button(
                            onClick = onLogoutClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Teal),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text(stringResource(R.string.login_button), fontWeight = FontWeight.Bold, color = Color.White) }
                    } else {
                        IconButton(
                            onClick = onLogoutClick,
                            modifier = Modifier.background(Teal, RoundedCornerShape(8.dp))
                        ) { Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = stringResource(R.string.logout_cd), tint = Color.White) }
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

            AudioPlayerCard(
                audioUrl = audioUrl,
                navyBlue = NavyBlue,
                teal = Teal
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.gender_match_question),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = NavyBlue
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = genderRespected == true,
                    onClick = { genderRespected = true },
                    colors = RadioButtonDefaults.colors(selectedColor = Teal, unselectedColor = Color.Gray)
                )
                Text(stringResource(R.string.yes_option), fontSize = 16.sp, color = NavyBlue, modifier = Modifier.clickable { genderRespected = true })

                Spacer(modifier = Modifier.width(32.dp))

                RadioButton(
                    selected = genderRespected == false,
                    onClick = { genderRespected = false },
                    colors = RadioButtonDefaults.colors(selectedColor = Teal, unselectedColor = Color.Gray)
                )
                Text(stringResource(R.string.no_option), fontSize = 16.sp, color = NavyBlue, modifier = Modifier.clickable { genderRespected = false })
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.detailed_feedback_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue
            )

            Spacer(modifier = Modifier.height(24.dp))

            StarRatingCategory(stringResource(R.string.intelligibility_category), intelligibility) { intelligibility = it }
            Spacer(modifier = Modifier.height(16.dp))

            StarRatingCategory(stringResource(R.string.naturalness_category), naturalness) { naturalness = it }
            Spacer(modifier = Modifier.height(16.dp))

            StarRatingCategory(stringResource(R.string.accent_category), accent) { accent = it }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.word_accuracy_label),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = NavyBlue
            )
            Text(
                text = stringResource(R.string.word_accuracy_helper),
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
                placeholder = { Text(stringResource(R.string.comments_placeholder), color = Color.Gray) },
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
                text = if (state.isLoading) stringResource(R.string.submitting_text) else stringResource(R.string.submit_feedback_button),
                onClick = {
                    viewModel.sendEvent(
                        FeedbackEvent.SubmitClicked(
                            audioUrl = audioUrl,
                            modelType = modelType,
                            intelligibility = intelligibility,
                            naturalness = naturalness,
                            accent = accent,
                            wordAccuracy = wordAccuracy,
                            genderRespected = genderRespected!!,
                            comments = comments
                        )
                    )
                },
                isEnabled = intelligibility > 0 && naturalness > 0 && accent > 0 && genderRespected != null && !state.isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}