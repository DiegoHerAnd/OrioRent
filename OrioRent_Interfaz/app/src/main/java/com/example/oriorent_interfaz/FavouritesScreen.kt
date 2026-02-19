package com.example.oriorent_interfaz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    usuarioEmail: String,
    onBackClick: () -> Unit,
    onLocalClick: (Int) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val dbHelper = remember { OrioRentDBHelper(context) }
    val usuario = remember(usuarioEmail) { dbHelper.obtenerUsuarioPorEmail(usuarioEmail) }
    val idUsuario = usuario?.id_usuario ?: -1

    var refreshCount by remember { mutableIntStateOf(0) }
    
    val localesFavoritos = remember(refreshCount) {
        val todos = dbHelper.obtenerLocales()
        todos.filter { dbHelper.esFavorito(idUsuario, it.id_local) }
    }

    val localesFiltrados = remember(searchText, localesFavoritos) {
        if (searchText.isBlank()) {
            localesFavoritos
        } else {
            localesFavoritos.filter {
                it.nombre.contains(searchText, ignoreCase = true) ||
                it.descripcion.contains(searchText, ignoreCase = true) ||
                it.direccion.contains(searchText, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                    Text(
                        "Mis Favoritos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    placeholder = { Text("Search in favourites", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(25.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFEFEFEF),
                        unfocusedContainerColor = Color(0xFFEFEFEF),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { paddingValues ->
        if (localesFiltrados.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchText.isEmpty()) "Aún no tienes locales favoritos." 
                           else "No se han encontrado resultados.",
                    color = Color.Gray
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
            ) {
                items(localesFiltrados) { local ->
                    FeaturedLocalCard(
                        local = local,
                        isFavorite = true,
                        onFavoriteToggle = {
                            dbHelper.toggleFavorito(idUsuario, local.id_local)
                            refreshCount++
                        },
                        onClick = { onLocalClick(local.id_local) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
