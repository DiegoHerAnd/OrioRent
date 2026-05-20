package com.example.oriorent_interfaz

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    usuarioEmail: String,
    onLogoutClick: () -> Unit,
    onMainClick: () -> Unit,
    onPostalServiceClick: () -> Unit,
    onAddLocalClick: () -> Unit,
    onFavouritesClick: () -> Unit,
    onPublicProfileClick: () -> Unit,
    onMyBookingsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val dbHelper = OrioRentDB
    var usuario by remember(usuarioEmail) { mutableStateOf<Usuario?>(null) }

    LaunchedEffect(usuarioEmail) {
        usuario = dbHelper.obtenerUsuarioPorEmail(usuarioEmail)
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1A4A7A)
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null, tint = Color.White) },
                    label = { Text("Inicio", color = Color.White) },
                    selected = false,
                    onClick = onMainClick
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
                    selected = true,
                    onClick = { }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .padding(24.dp)
        ) {
            // Foto y Nombre (Clickeable para ver perfil público)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPublicProfileClick() }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE9ECEF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = usuario?.nombre ?: "Usuario",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Secciones
            ProfileSection(title = "Transacciones") {
                ProfileItem(icon = Icons.Default.List, label = "Alquileres", onClick = onMyBookingsClick)
                ProfileItem(icon = Icons.Default.AccountBox, label = "Metodo de pago", onClick = { /* Pagos */ })
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProfileSection(title = "Cuenta") {
                ProfileItem(icon = Icons.Default.FavoriteBorder, label = "Favoritos", onClick = onFavouritesClick)
                ProfileItem(icon = Icons.Default.Settings, label = "Ajustes", onClick = onSettingsClick)
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProfileSection(title = "Soporte") {
                ProfileItem(icon = Icons.Default.Info, label = "¿Necesitas ayuda?", onClick = { /* Ayuda */ })
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón Cerrar Sesión
            TextButton(
                onClick = onLogoutClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Cerrar Sesion",
                    color = Color.Gray,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
fun ProfileItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = Color.Black
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    usuarioEmail: String,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val dbHelper = OrioRentDB
    var usuario by remember(usuarioEmail) { mutableStateOf<Usuario?>(null) }
    var notificaciones by remember { mutableStateOf(true) }
    var perfilPublico by remember { mutableStateOf(true) }
    var mostrarEmail by remember { mutableStateOf(false) }

    LaunchedEffect(usuarioEmail) {
        usuario = dbHelper.obtenerUsuarioPorEmail(usuarioEmail)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1A4A7A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (usuario?.nombre?.take(1) ?: "?").uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(usuario?.nombre ?: "Usuario", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(usuarioEmail, color = Color.Gray, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(28.dp))

            SettingsSection("Cuenta") {
                SettingsInfoRow(Icons.Default.Person, "Nombre", usuario?.nombre ?: "Sin nombre")
                SettingsInfoRow(Icons.Default.Email, "Correo", usuarioEmail)
            }

            Spacer(Modifier.height(24.dp))

            SettingsSection("Preferencias") {
                SettingsSwitchRow(Icons.Default.Notifications, "Notificaciones", "Avisos de reservas y mensajes", notificaciones) {
                    notificaciones = it
                }
                SettingsSwitchRow(Icons.Default.Person, "Perfil publico", "Permite que otros vean tus locales", perfilPublico) {
                    perfilPublico = it
                }
                SettingsSwitchRow(Icons.Default.Email, "Mostrar email", "Visible en tu perfil publico", mostrarEmail) {
                    mostrarEmail = it
                }
            }

            Spacer(Modifier.height(24.dp))

            SettingsSection("Seguridad") {
                ProfileItem(icon = Icons.Default.Lock, label = "Cambiar contrasena", onClick = { })
                ProfileItem(icon = Icons.Default.Delete, label = "Eliminar cuenta", onClick = { })
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A4A7A)),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
            ) {
                Text("Cerrar sesion")
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun SettingsInfoRow(icon: ImageVector, title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(26.dp), tint = Color.Black)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium, color = Color.Black)
            Text(value, color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(26.dp), tint = Color.Black)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium, color = Color.Black)
            Text(subtitle, color = Color.Gray, fontSize = 13.sp)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
