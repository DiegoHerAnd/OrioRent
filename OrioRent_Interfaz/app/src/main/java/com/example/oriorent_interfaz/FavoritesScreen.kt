package com.example.oriorent_interfaz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    userEmail: String,
    onBackClick: () -> Unit,
    onLocalClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { OrioRentDBHelper(context) }
    val usuario = remember { dbHelper.obtenerUsuarioPorEmail(userEmail) }
    
    // Función para cargar los favoritos actuales
    fun getFavoritos() = dbHelper.obtenerLocales().filter { local ->
        dbHelper.esFavorito(usuario?.id_usuario ?: 0, local.id_local)
    }

    var localesFavoritos by remember { mutableStateOf(getFavoritos()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Favoritos") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (localesFavoritos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Aún no tienes locales favoritos.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(localesFavoritos) { local ->
                    FeaturedLocalCard(
                        local = local,
                        isFavorite = true, // Siempre es true en esta pantalla
                        onFavoriteToggle = {
                            // Quitamos de favoritos en la DB
                            dbHelper.toggleFavorito(usuario?.id_usuario ?: 0, local.id_local)
                            // Actualizamos la lista de la pantalla para que desaparezca
                            localesFavoritos = getFavoritos()
                        },
                        onClick = { onLocalClick(local.id_local) }
                    )
                }
            }
        }
    }
}
