package com.example.oriorent_interfaz

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "App iniciada")
        setContent { MaterialTheme { OrioRentApp() } }

        lifecycleScope.launch {
            val cats = SupabaseRepository.obtenerCategorias()
            Log.d("SUPABASE_TEST", "Categorías: ${cats.size} → $cats")
        }
    }
}

@Composable
fun OrioRentApp() {
    var pantallaActual         by rememberSaveable { mutableStateOf("login") }
    var usuarioLogueadoEmail   by rememberSaveable { mutableStateOf("") }
    var idLocalSeleccionado    by rememberSaveable { mutableIntStateOf(-1) }
    var emailPropietarioVer    by rememberSaveable { mutableStateOf("") }
    var idConversacionActual   by rememberSaveable { mutableIntStateOf(-1) }

    when (pantallaActual) {

        "login" -> LoginScreen(
            onLoginSuccess = { email -> usuarioLogueadoEmail = email; pantallaActual = "main" },
            onRegistroClick = { pantallaActual = "registro" }
        )

        "registro" -> RegistroScreen(
            onRegistroSuccess = { pantallaActual = "login" },
            onBackClick       = { pantallaActual = "login" },
            onTermsClick      = { pantallaActual = "terms" }
        )

        "main" -> MainScreen(
            usuarioEmail          = usuarioLogueadoEmail,
            onLogout              = { pantallaActual = "login" },
            onAddLocalClick       = { pantallaActual = "form_local" },
            onLocalClick          = { id -> idLocalSeleccionado = id; pantallaActual = "detalles_local" },
            onPostalServiceClick  = { pantallaActual = "postal_service" },
            onFavouritesClick     = { pantallaActual = "favourites" },
            onProfileScreen       = { pantallaActual = "profile" }
        )

        "form_local" -> LocalFormScreen(
            usuarioEmail = usuarioLogueadoEmail,
            onBackClick  = { pantallaActual = "main" },
            onSuccess    = { pantallaActual = "main" }
        )

        "detalles_local" -> LocalDetailsScreen(
            idLocal          = idLocalSeleccionado,
            usuarioEmail     = usuarioLogueadoEmail,
            onBackClick      = { pantallaActual = "main" },
            onReservaSuccess = { pantallaActual = "my_bookings" },
            onOwnerClick     = { ownerEmail ->
                emailPropietarioVer = ownerEmail
                pantallaActual = "owner_profile"
            },
            onContactarClick = { idConv ->
                idConversacionActual = idConv
                pantallaActual = "chat"
            }
        )

        "chat" -> ChatScreen(
            idConversacion = idConversacionActual,
            usuarioEmail   = usuarioLogueadoEmail,
            onBackClick    = { pantallaActual = "postal_service" }
        )

        "postal_service" -> PostalService(
            usuarioEmail          = usuarioLogueadoEmail,
            onBack                = { pantallaActual = "main" },
            onConversacionClick   = { id -> idConversacionActual = id; pantallaActual = "chat" }
        )

        "favourites" -> FavouritesScreen(
            usuarioEmail  = usuarioLogueadoEmail,
            onBackClick   = { pantallaActual = "main" },
            onLocalClick  = { id -> idLocalSeleccionado = id; pantallaActual = "detalles_local" }
        )

        "profile" -> ProfileScreen(
            usuarioEmail          = usuarioLogueadoEmail,
            onLogoutClick         = { pantallaActual = "login" },
            onMainClick           = { pantallaActual = "main" },
            onPostalServiceClick  = { pantallaActual = "postal_service" },
            onAddLocalClick       = { pantallaActual = "form_local" },
            onFavouritesClick     = { pantallaActual = "favourites" },
            onPublicProfileClick  = { pantallaActual = "public_profile" },
            onMyBookingsClick     = { pantallaActual = "my_bookings" }
        )

        // Perfil PROPIO del usuario logueado
        "public_profile" -> UserPublicProfileScreen(
            usuarioEmail    = usuarioLogueadoEmail,
            emailPerfil     = usuarioLogueadoEmail,   // viendo mi propio perfil
            onBackClick     = { pantallaActual = "profile" },
            onAddLocalClick = { pantallaActual = "form_local" },
            onLocalClick    = { id -> idLocalSeleccionado = id; pantallaActual = "detalles_local" }
        )

        // Perfil PÚBLICO del propietario de un local
        "owner_profile" -> UserPublicProfileScreen(
            usuarioEmail    = usuarioLogueadoEmail,
            emailPerfil     = emailPropietarioVer,    // viendo el perfil de otro
            onBackClick     = { pantallaActual = "detalles_local" },
            onAddLocalClick = { pantallaActual = "form_local" },
            onLocalClick    = { id -> idLocalSeleccionado = id; pantallaActual = "detalles_local" }
        )

        "terms" -> TermsScreen(
            onBackClick = { pantallaActual = "registro" }
        )

        "my_bookings" -> MyBookingsScreen(
            usuarioEmail = usuarioLogueadoEmail,
            onBackClick  = { pantallaActual = "profile" }
        )
    }
}