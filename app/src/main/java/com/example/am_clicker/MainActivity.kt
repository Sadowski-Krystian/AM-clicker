package com.example.am_clicker

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- TRYB PEŁNOEKRANOWY (IMMERSIVE MODE) ---

        // 1. Pozwalamy aplikacji rysować się na całym ekranie (pod paskami)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 2. Pobieramy kontroler do zarządzania paskami
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        // 3. Paski pojawią się tylko po pociągnięciu od krawędzi ekranu i zaraz znikną
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // 4. Ukrywamy całkowicie pasek statusu (góra) i nawigacji (dół)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        // -------------------------------------------

        setContent {
            // Using the built-in standard theme so we don't need a Theme.kt file!
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}