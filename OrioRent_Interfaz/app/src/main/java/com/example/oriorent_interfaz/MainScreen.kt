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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    usuarioEmail: String,
    onLogout: () -> Unit,
    onAddLocalClick: () -> Unit,
    onLocalClick: (Int) -> Unit,
    onPostalServiceClick: () -> Unit,
    onFavouritesClick: () -> Unit,
    onProfileScreen: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var showCategories by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }

    val dbHelper = OrioRentDB
    val scope = rememberCoroutineScope()

    var usuario by remember { mutableStateOf<Usuario?>(null) }
    LaunchedEffect(usuarioEmail) { usuario = dbHelper.obtenerUsuarioPorEmail(usuarioEmail) }
    val idUsuario = usuario?.id_usuario ?: -1

    var refreshLocales by remember { mutableIntStateOf(0) }
    var localesOriginales by remember { mutableStateOf<List<Local>>(emptyList()) }
    LaunchedEffect(refreshLocales) { localesOriginales = dbHelper.obtenerLocales() }

    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }
    LaunchedEffect(Unit) { categorias = dbHelper.obtenerCategorias() }
    val categoriasPorId = remember(categorias) { categorias.associateBy { it.id_categoria } }
    val categoriaSeleccionada = selectedCategoryId?.let { categoriasPorId[it] }

    val localesFiltrados = remember(searchText, selectedCategoryId, localesOriginales, categoriasPorId) {
        localesOriginales.filter { local ->
            val coincideCategoria = selectedCategoryId == null || local.id_categoria == selectedCategoryId
            val categoriaNombre = categoriasPorId[local.id_categoria]?.nombre.orEmpty()
            val coincideBusqueda = searchText.isBlank() ||
                local.nombre.contains(searchText, ignoreCase = true) ||
                local.descripcion.contains(searchText, ignoreCase = true) ||
                local.direccion.contains(searchText, ignoreCase = true) ||
                categoriaNombre.contains(searchText, ignoreCase = true)

            coincideCategoria && coincideBusqueda
        }
    }

    fun categoryImage(idCategoria: Int): Int =
        if (idCategoria == 1) R.drawable.fiesta else R.drawable.logooriorent

    val categoriasVisibles = remember(categorias, localesOriginales) {
        if (categorias.isNotEmpty()) {
            categorias
        } else {
            localesOriginales
                .map { local -> Categoria(local.id_categoria, "Categoria ${local.id_categoria}", "") }
                .distinctBy { it.id_categoria }
        }
    }

    val categoriasPopulares = remember(categoriasVisibles, localesOriginales) {
        categoriasVisibles
            .filter { categoria -> localesOriginales.any { it.id_categoria == categoria.id_categoria } }
            .ifEmpty { categoriasVisibles }
            .take(6)
    }

    val emptyMessage = when {
        localesOriginales.isEmpty() -> "Aun no hay locales publicados."
        categoriaSeleccionada != null -> "No hay locales en ${categoriaSeleccionada.nombre}."
        searchText.isNotBlank() -> "No hay locales para \"$searchText\"."
        else -> "No hay locales disponibles."
    }

    val emptyActionText = when {
        selectedCategoryId != null && searchText.isNotBlank() -> "Quitar filtros"
        selectedCategoryId != null -> "Ver todas las categorias"
        searchText.isNotBlank() -> "Limpiar busqueda"
        else -> ""
    }

    val clearFilters = {
        searchText = ""
        selectedCategoryId = null
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onFavouritesClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A4A7A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("FAVORITOS", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { showCategories = !showCategories },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showCategories || selectedCategoryId != null) Color(0xFF2C6AA0) else Color(0xFF1A4A7A)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(categoriaSeleccionada?.nombre?.uppercase() ?: "CATEGORIAS", fontSize = 12.sp, maxLines = 1)
                    }
                }

                if (showCategories) {
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCategoryId == null,
                                onClick = { selectedCategoryId = null },
                                label = { Text("Todas") },
                                leadingIcon = if (selectedCategoryId == null) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null
                            )
                        }
                        items(categoriasVisibles) { categoria ->
                            FilterChip(
                                selected = selectedCategoryId == categoria.id_categoria,
                                onClick = { selectedCategoryId = categoria.id_categoria },
                                label = { Text(categoria.nombre, maxLines = 1) },
                                leadingIcon = if (selectedCategoryId == categoria.id_categoria) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null
                            )
                        }
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
                    onClick = onPostalServiceClick
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
                    onClick = onProfileScreen
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
                    items(categoriasPopulares) { categoria ->
                        CategoryItemCard(
                            title = categoria.nombre,
                            imageRes = categoryImage(categoria.id_categoria),
                            selected = selectedCategoryId == categoria.id_categoria,
                            count = localesOriginales.count { it.id_categoria == categoria.id_categoria },
                            onClick = {
                                selectedCategoryId = if (selectedCategoryId == categoria.id_categoria) null else categoria.id_categoria
                            }
                        )
                    }
                }
            }

            if (selectedCategoryId != null || searchText.isNotBlank()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = categoriaSeleccionada?.let { "${it.nombre} · ${localesFiltrados.size} resultados" }
                                ?: "${localesFiltrados.size} resultados",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        TextButton(onClick = clearFilters) {
                            Text("Limpiar")
                        }
                    }
                }
            }

            if (localesFiltrados.isEmpty()) {
                item {
                    EmptyLocalResults(
                        message = emptyMessage,
                        actionText = emptyActionText,
                        onActionClick = clearFilters
                    )
                }
                return@LazyColumn
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
                        var isFav by remember { mutableStateOf(false) }
                        LaunchedEffect(idUsuario, local.id_local) { isFav = dbHelper.esFavorito(idUsuario, local.id_local) }
                        FeaturedLocalCard(
                            local = local,
                            isFavorite = isFav,
                            onFavoriteToggle = {
                                scope.launch {
                                    isFav = dbHelper.toggleFavorito(idUsuario, local.id_local)
                                    refreshLocales++
                                }
                            },
                            onClick = { onLocalClick(local.id_local) },
                            modifier = Modifier.width(200.dp)
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
                        var isFav by remember { mutableStateOf(false) }
                        LaunchedEffect(idUsuario, local.id_local) { isFav = dbHelper.esFavorito(idUsuario, local.id_local) }
                        FeaturedLocalCard(
                            local = local,
                            isFavorite = isFav,
                            onFavoriteToggle = {
                                scope.launch {
                                    isFav = dbHelper.toggleFavorito(idUsuario, local.id_local)
                                    refreshLocales++
                                }
                            },
                            onClick = { onLocalClick(local.id_local) },
                            modifier = Modifier.width(200.dp)
                        )
                    }
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
fun CategoryItemCard(
    title: String,
    imageRes: Int,
    selected: Boolean,
    count: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(86.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFF2C6AA0) else Color(0xFF1A4A7A)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 5.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2
                )
                Text(
                    text = "$count locales",
                    color = Color.White.copy(alpha = 0.78f),
                    fontSize = 12.sp
                )
            }
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.width(64.dp).fillMaxHeight()
            )
        }
    }
}

@Composable
fun EmptyLocalResults(
    message: String,
    actionText: String,
    onActionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            tint = Color(0xFF1A4A7A),
            modifier = Modifier.size(42.dp)
        )
        Text(
            text = message,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF1A1A1A)
        )
        Text(
            text = "Prueba otra busqueda o revisa todas las categorias disponibles.",
            color = Color.Gray,
            fontSize = 14.sp
        )
        if (actionText.isNotBlank()) {
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A4A7A)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(actionText)
            }
        }
    }
}

@Composable
fun FeaturedLocalCard(
    local: Local,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    fun formatPrice(value: Double): String =
        if (value % 1.0 == 0.0) value.toInt().toString()
        else String.format(Locale.US, "%.2f", value)

    Card(
        modifier = modifier.clickable { onClick() },
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
                    text = "${formatPrice(local.precio_base)}€/${local.tipo_precio}",
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
