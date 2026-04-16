package com.example.echoro.ui.screens.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.echoro.MainActivity

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}