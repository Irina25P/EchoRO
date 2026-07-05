package com.example.echoro.ui.screens.abtesting

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.echoro.viewmodel.abtesting.ABTestingEvent
import com.example.echoro.viewmodel.abtesting.ABTestingOSE
import com.example.echoro.viewmodel.abtesting.ABTestingViewModel

@Composable
fun ABTestingFlowScreen(
    totalCount: Int,
    onNavigateBack: () -> Unit,
    onNavigateToLanding: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ABTestingViewModel = viewModel(
        factory = ABTestingViewModel.Factory(context.applicationContext as Application, totalCount)
    )

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.ose.collect { ose ->
            when (ose) {
                is ABTestingOSE.NavigateBack -> onNavigateBack()
                is ABTestingOSE.NavigateToLanding -> onNavigateToLanding()
                is ABTestingOSE.ShowMessage -> { }
            }
        }
    }

    if (state.isSessionLoading) {
        com.example.echoro.ui.screens.generatevoice.LoadingScreen()
        return
    }

    ABTestingScreen(
        pageIndex = state.currentPage,
        totalCount = state.totalCount,
        modelA = state.modelA,
        modelB = state.modelB,
        audioUrlA = state.currentAudioUrlA,
        audioUrlB = state.currentAudioUrlB,
        text = state.currentText,
        description = state.currentDescription,
        isPairLoading = false,
        savedAnswer = state.currentAnswer,
        isLoading = state.isLoading,
        onSaveAnswer = { modelA, modelB, naturalness, intelligibility, accent, wordAccuracy ->
            viewModel.sendEvent(
                ABTestingEvent.SaveAnswer(modelA, modelB, naturalness, intelligibility, accent, wordAccuracy)
            )
        },
        onNext = { viewModel.sendEvent(ABTestingEvent.Next) },
        onSubmit = { viewModel.sendEvent(ABTestingEvent.Submit) },
        onNavigateBack = onNavigateBack
    )
}
