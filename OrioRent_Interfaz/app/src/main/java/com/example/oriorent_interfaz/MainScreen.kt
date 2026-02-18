package com.example.oriorent_interfaz

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userEmail: String,
    onLogout: () -> Unit,
    onAddLocalClick: () -> Unit,
    onLocalClick: (Int) -> Unit,
    onProfileClick: () -> Unit,
    onFavoritesClick: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val dbHelper = remember { OrioRentDBHelper(context) }
    val usuario = remember { dbHelper.obtenerUsuarioPorEmail(userEmail) }
    
    val localesOriginales by remember { mutableStateOf(dbHelper.obtenerLocales()) }

    val localesFiltrados = remember(searchText, localesOriginales) {
        if (searchText.isBlank()) {
            localesOriginales
        } else {
            localesOriginales.filter {
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
                // Barra de Búsqueda
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    placeholder = { Text("Search", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(25.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFEFEFEF),
                        unfocusedContainerColor = Color(0xFFEFEFEF),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de Favoritos y Categorías
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onFavoritesClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A4A7A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("FAVORITOS", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { /* Categorías */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A4A7A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CATEGORIAS", fontSize = 12.sp)
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1A4A7A)
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null, tint = Color.White) },
                    label = { Text("Inicio", color = Color.White) },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.White) },
                    label = { Text("Buzón", color = Color.White) },
                    selected = false,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = null, tint = Color.White) },
                    label = { Text("Subir", color = Color.White) },
                    selected = false,
                    onClick = onAddLocalClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) },
                    label = { Text("Tú", color = Color.White) },
                    selected = false,
                    onClick = onProfileClick
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            item {
                Text(
                    text = "¡Lo mas buscado!",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { CategoryItemCard("Fiesta", R.drawable.fiesta) }
                    item { CategoryItemCard("Reunion", R.drawable.logooriorent) }
                }
            }

            item {
                Text(
                    text = "Destacados",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(localesFiltrados) { local ->
                        var isFav by remember { mutableStateOf(dbHelper.esFavorito(usuario?.id_usuario ?: 0, local.id_local)) }
                        FeaturedLocalCard(
                            local = local,
                            isFavorite = isFav,
                            onFavoriteToggle = {
                                isFav = dbHelper.toggleFavorito(usuario?.id_usuario ?: 0, local.id_local)
                            },
                            onClick = { onLocalClick(local.id_local) }
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Lo mas reciente...",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(localesFiltrados.reversed()) { local ->
                        var isFav by remember { mutableStateOf(dbHelper.esFavorito(usuario?.id_usuario ?: 0, local.id_local)) }
                        FeaturedLocalCard(
                            local = local,
                            isFavorite = isFav,
                            onFavoriteToggle = {
                                isFav = dbHelper.toggleFavorito(usuario?.id_usuario ?: 0, local.id_local)
                            },
                            onClick = { onLocalClick(local.id_local) }
                        )
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(20.dp))
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
fun CategoryItemCard(title: String, imageRes: Int) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(70.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A4A7A))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f).padding(start = 12.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.width(60.dp).fillMaxHeight()
            )
        }
    }
}

@Composable
fun FeaturedLocalCard(
    local: Local,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                Image(
                    painter = painterResource(if (local.id_categoria == 1) R.drawable.fiesta else R.drawable.logooriorent),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )
                IconButton(
                    onClick = { onFavoriteToggle() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "${local.precio_base}€/${local.tipo_precio}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = local.nombre,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }
    }
}
