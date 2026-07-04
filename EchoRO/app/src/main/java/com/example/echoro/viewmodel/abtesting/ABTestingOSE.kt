package com.example.echoro.viewmodel.abtesting

sealed class ABTestingOSE {
    object NavigateBack : ABTestingOSE()
    object NavigateToLanding : ABTestingOSE()
    data class ShowMessage(val message: String) : ABTestingOSE()
}
