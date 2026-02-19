package com.example.oriorent_interfaz

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    onMyBookingsClick: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { OrioRentDBHelper(context) }
    val usuario = remember(usuarioEmail) { dbHelper.obtenerUsuarioPorEmail(usuarioEmail) }

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
                ProfileItem(icon = Icons.Default.Settings, label = "Ajustes", onClick = { /* Ajustes */ })
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
