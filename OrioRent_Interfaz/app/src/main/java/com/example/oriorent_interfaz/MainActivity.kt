package com.example.oriorent_interfaz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    var usuarioLogueadoEmail by remember { mutableStateOf("") }
    var idLocalSeleccionado by remember { mutableIntStateOf(-1) }

    when (pantallaActual) {
        "login" -> LoginScreen(
            onLoginSuccess = { email ->
                usuarioLogueadoEmail = email
                pantallaActual = "main"
            },
            onRegistroClick = { pantallaActual = "registro" }
        )
        "registro" -> RegistroScreen(
            onRegistroSuccess = { pantallaActual = "login" },
            onBackClick = { pantallaActual = "login" },
            onTermsClick = { pantallaActual = "terms" } // Este es el que faltaba
        )
        "main" -> MainScreen(
            userEmail = usuarioLogueadoEmail,
            onLogout = { pantallaActual = "login" },
            onAddLocalClick = { pantallaActual = "form_local" },
            onLocalClick = { id ->
                idLocalSeleccionado = id
                pantallaActual = "detalles_local"
            },
            onProfileClick = { pantallaActual = "perfil" },
            onFavoritesClick = { pantallaActual = "favoritos" }
        )
        "form_local" -> LocalFormScreen(
            usuarioEmail = usuarioLogueadoEmail,
            onBackClick = { pantallaActual = "main" },
            onSuccess = { pantallaActual = "main" }
        )
        "detalles_local" -> LocalDetailsScreen(
            idLocal = idLocalSeleccionado,
            usuarioEmail = usuarioLogueadoEmail,
            onBackClick = { pantallaActual = "main" },
            onReservaSuccess = { pantallaActual = "main" }
        )
        "perfil" -> ProfileScreen(
            usuarioEmail = usuarioLogueadoEmail,
            onBackClick = { pantallaActual = "main" },
            onLogoutClick = { pantallaActual = "login" }
        )
        "favoritos" -> FavoritesScreen(
            userEmail = usuarioLogueadoEmail,
            onBackClick = { pantallaActual = "main" },
            onLocalClick = { id ->
                idLocalSeleccionado = id
                pantallaActual = "detalles_local"
            }
        )
        "terms" -> TermsAndConditionsScreen(
            onBackClick = { pantallaActual = "registro" }
        )
    }
}
