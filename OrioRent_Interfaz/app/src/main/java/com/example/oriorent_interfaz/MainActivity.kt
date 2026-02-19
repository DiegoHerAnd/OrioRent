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
            onTermsClick = { pantallaActual = "terms" }
        )
        "main" -> MainScreen(
            usuarioEmail = usuarioLogueadoEmail,
            onLogout = { pantallaActual = "login" },
            onAddLocalClick = { pantallaActual = "form_local" },
            onLocalClick = { id ->
                idLocalSeleccionado = id
                pantallaActual = "detalles_local"
            },
            onPostalServiceClick = { pantallaActual = "postal_service" },
            onFavouritesClick = { pantallaActual = "favourites" },
            onProfileScreen = { pantallaActual = "profile" }
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
        "postal_service" -> PostalService(
            onBack = { pantallaActual = "main" }
        )
        "favourites" -> FavouritesScreen(
            usuarioEmail = usuarioLogueadoEmail,
            onBackClick = { pantallaActual = "main" },
            onLocalClick = { id ->
                idLocalSeleccionado = id
                pantallaActual = "detalles_local"
            }
        )
        "profile" -> ProfileScreen(
            usuarioEmail = usuarioLogueadoEmail,
            onLogoutClick = { pantallaActual = "login" },
            onMainClick = { pantallaActual = "main" },
            onPostalServiceClick = { pantallaActual = "postal_service" },
            onAddLocalClick = { pantallaActual = "form_local" },
            onFavouritesClick = { pantallaActual = "favourites" },
            onPublicProfileClick = { pantallaActual = "public_profile" },
            onMyBookingsClick = { pantallaActual = "my_bookings" }
        )
        "public_profile" -> UserPublicProfileScreen(
            usuarioEmail = usuarioLogueadoEmail,
            onBackClick = { pantallaActual = "profile" },
            onAddLocalClick = { pantallaActual = "form_local" },
            onLocalClick = { id ->
                idLocalSeleccionado = id
                pantallaActual = "detalles_local"
            }
        )
        "terms" -> TermsScreen(
            onBackClick = { pantallaActual = "registro" }
        )
        "my_bookings" -> MyBookingsScreen(
            usuarioEmail = usuarioLogueadoEmail,
            onBackClick = { pantallaActual = "profile" }
        )
    }
}
