package com.example.oriorent_interfaz

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "App iniciada")

        setContent {
            MaterialTheme {
                OrioRentApp()
            }
        }
    }
}

@Composable
fun OrioRentApp() {
    var pantallaActual by remember { mutableStateOf("login") }

    when (pantallaActual) {
        "login" -> LoginScreen(
            onLoginSuccess = { pantallaActual = "main" },
            onRegistroClick = { pantallaActual = "registro" }
        )
        "registro" -> RegistroScreen(
            onRegistroSuccess = { pantallaActual = "login" },
            onBackClick = { pantallaActual = "login" }
        )
        "main" -> MainScreen(
            onLogout = { pantallaActual = "login" }
        )
    }
}