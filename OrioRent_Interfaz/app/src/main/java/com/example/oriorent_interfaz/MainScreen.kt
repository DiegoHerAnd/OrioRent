package com.example.oriorent_interfaz

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit,
    onAddLocalClick: () -> Unit,
    onLocalClick: (Int) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var searchText by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val dbHelper = remember { OrioRentDBHelper(context) }
    val localesOriginales by remember { mutableStateOf(dbHelper.obtenerLocales()) }

    // Filtrar locales según el texto de búsqueda
    val localesFiltrados = remember(searchText, localesOriginales) {
        if (searchText.isBlank()) {
            localesOriginales
        } else {
            localesOriginales.filter { local ->
                local.nombre.contains(searchText, ignoreCase = true) ||
                local.descripcion.contains(searchText, ignoreCase = true) ||
                local.direccion.contains(searchText, ignoreCase = true)
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
                // Barra de búsqueda funcional
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = { Text("Search by name, description or address...") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar"
                        )
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = { searchText = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                            }
                        } else {
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = "Opciones"
                            )
                        }
                    },
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF1976D2)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de Favoritos y Categorías
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            Log.d("MainScreen", "Favoritos clicked")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("FAVORITOS")
                    }

                    Button(
                        onClick = {
                            Log.d("MainScreen", "Categorías clicked")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Default.AccountBox,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CATEGORÍAS")
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Email, contentDescription = "Mensajes") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Añadir") },
                    selected = selectedTab == 2,
                    onClick = { 
                        selectedTab = 2
                        onAddLocalClick()
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Sección "¡Lo mas buscado!" (solo se muestra si no hay búsqueda activa)
            if (searchText.isEmpty()) {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "¡Lo mas buscado!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                CategoryCard(
                                    title = "Fiesta",
                                    color = Color(0xFF1976D2)
                                )
                            }
                            item {
                                CategoryCard(
                                    title = "Reunion",
                                    color = Color(0xFF1976D2)
                                )
                            }
                        }
                    }
                }
            }

            // Sección "Locales Disponibles"
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (searchText.isEmpty()) "Locales Disponibles" else "Resultados de búsqueda (${localesFiltrados.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Muestra los locales filtrados
            if (localesFiltrados.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No se han encontrado locales que coincidan con tu búsqueda.")
                    }
                }
            } else {
                items(localesFiltrados) { local ->
                    LocalCard(
                        precio = "${local.precio_base}€ / ${local.tipo_precio}",
                        descripcion = "${local.nombre} - ${local.direccion}",
                        onClick = { onLocalClick(local.id_local) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            // Espaciado final y botón de cerrar sesión
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Cerrar Sesión")
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(140.dp)
            .height(80.dp)
            .clickable {
                Log.d("MainScreen", "Category clicked: $title")
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomStart
        ) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun LocalCard(
    precio: String,
    descripcion: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Imagen del local (placeholder con color)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFF1976D2))
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }

            // Información del local
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = precio,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}
