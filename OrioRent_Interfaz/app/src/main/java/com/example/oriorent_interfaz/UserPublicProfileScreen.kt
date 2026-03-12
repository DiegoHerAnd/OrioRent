package com.example.oriorent_interfaz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPublicProfileScreen(
    usuarioEmail: String,          // email del usuario logueado (para favoritos, etc.)
    emailPerfil: String = usuarioEmail, // email del perfil que se muestra (puede ser otro)
    onBackClick: () -> Unit,
    onAddLocalClick: () -> Unit,
    onLocalClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { OrioRentDBHelper(context) }

    val usuarioLogueado = remember(usuarioEmail) { dbHelper.obtenerUsuarioPorEmail(usuarioEmail) }
    val idUsuarioLogueado = usuarioLogueado?.id_usuario ?: -1

    // Datos del perfil que estamos viendo
    val perfilUsuario = remember(emailPerfil) { dbHelper.obtenerUsuarioPorEmail(emailPerfil) }
    val idPerfil = perfilUsuario?.id_usuario ?: -1

    val esMiPerfil = idUsuarioLogueado == idPerfil

    var refreshCount by remember { mutableIntStateOf(0) }
    val misLocales = remember(refreshCount, idPerfil) {
        dbHelper.obtenerLocales().filter { it.id_propietario == idPerfil }
    }

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (esMiPerfil) "Mi perfil" else perfilUsuario?.nombre ?: "Perfil",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { pv ->
        Column(Modifier.fillMaxSize().padding(pv).background(Color.White)) {

            // ── Cabecera ──────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(perfilUsuario?.nombre ?: "Usuario", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) {
                            Icon(Icons.Default.Star, null, tint = Color.LightGray, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(4.dp))
                        Text("0 (0)", color = Color.Gray, fontSize = 14.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, Modifier.size(16.dp), tint = Color.Gray)
                        Spacer(Modifier.width(8.dp))
                        Text("${misLocales.size} ${if (misLocales.size == 1) "local" else "locales"}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, Modifier.size(16.dp), tint = Color.Gray)
                        Spacer(Modifier.width(8.dp))
                        Text("Ubicación no disponible", color = Color.Gray, fontSize = 14.sp)
                    }
                }

                // Avatar con inicial
                Box(
                    modifier = Modifier.size(80.dp).clip(CircleShape).background(Color(0xFF1A4A7A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (perfilUsuario?.nombre?.take(1) ?: "?").uppercase(),
                        color = Color.White, fontWeight = FontWeight.Bold, fontSize = 32.sp
                    )
                }
            }

            // ── Tabs ──────────────────────────────────────────────────────
            Box(
                modifier = Modifier.fillMaxWidth().height(56.dp)
                    .background(Color(0xFF1A4A7A))
            ) {
                Row(Modifier.fillMaxSize()) {
                    ProfileTabItem(
                        title = misLocales.size.toString(), subtitle = "Locales",
                        selected = selectedTab == 0, modifier = Modifier.weight(1f),
                        onClick = { selectedTab = 0 }
                    )
                    ProfileTabItem(
                        title = "0", subtitle = "Valoraciones",
                        selected = selectedTab == 1, modifier = Modifier.weight(1f),
                        onClick = { selectedTab = 1 }
                    )
                    if (esMiPerfil) {
                        ProfileTabItem(
                            icon = Icons.Default.Add, subtitle = "Subir",
                            selected = selectedTab == 2, modifier = Modifier.weight(1f),
                            onClick = { selectedTab = 2 }
                        )
                    }
                }
            }

            // ── Contenido de tab ──────────────────────────────────────────
            Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.TopCenter) {
                when (selectedTab) {
                    0 -> {
                        if (misLocales.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Nada en alquiler todavía...", fontSize = 18.sp, color = Color.Black)
                                if (esMiPerfil) {
                                    Spacer(Modifier.height(24.dp))
                                    Button(onClick = onAddLocalClick,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A4A7A)),
                                        shape = RoundedCornerShape(20.dp), modifier = Modifier.width(150.dp)) {
                                        Text("Subir local")
                                    }
                                }
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(misLocales) { local ->
                                    FeaturedLocalCard(
                                        local = local,
                                        isFavorite = dbHelper.esFavorito(idUsuarioLogueado, local.id_local),
                                        onFavoriteToggle = { dbHelper.toggleFavorito(idUsuarioLogueado, local.id_local); refreshCount++ },
                                        onClick = { onLocalClick(local.id_local) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                    1 -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text("No hay valoraciones todavía.", color = Color.Gray) }
                    2 -> {
                        // Tab "Subir" solo aparece en mi propio perfil
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Button(onClick = onAddLocalClick,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A4A7A)),
                                shape = RoundedCornerShape(20.dp)) { Text("Subir nuevo local") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileTabItem(
    title: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    subtitle: String,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxHeight().clickable { onClick() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (title != null) {
            Text(title, color = if (selected) Color(0xFFFFC107) else Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        } else if (icon != null) {
            Icon(icon, null, tint = if (selected) Color(0xFFFFC107) else Color.White, modifier = Modifier.size(22.dp))
        }
        Text(subtitle, color = if (selected) Color(0xFFFFC107) else Color.White, fontSize = 13.sp)
    }
}