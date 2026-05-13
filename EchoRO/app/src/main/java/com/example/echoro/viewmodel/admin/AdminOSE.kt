package com.example.echoro.viewmodel.admin

sealed class AdminOSE {
    data class ShowMessage(val message: String) : AdminOSE()
}