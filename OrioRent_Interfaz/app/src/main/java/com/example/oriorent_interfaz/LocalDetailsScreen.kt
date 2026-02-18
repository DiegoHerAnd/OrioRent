package com.example.oriorent_interfaz

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalDetailsScreen(
    idLocal: Int,
    usuarioEmail: String,
    onBackClick: () -> Unit,
    onReservaSuccess: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { OrioRentDBHelper(context) }
    val local = remember { dbHelper.obtenerLocalPorId(idLocal) }
    val usuario = remember { dbHelper.obtenerUsuarioPorEmail(usuarioEmail) }

    val esPropietario = local?.id_propietario == usuario?.id_usuario

    if (local == null) {
        Scaffold {
            Box(modifier = Modifier.fillMaxSize().padding(it), contentAlignment = Alignment.Center) {
                Text("Local no encontrado o cargando...")
            }
        }
        return
    }

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${local.precio_base}€",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "/ ${local.tipo_precio}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    Button(
                        onClick = {
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val hoy = sdf.format(Date())
                            dbHelper.insertarReserva(
                                idUsuario = usuario?.id_usuario ?: 0,
                                idLocal = local.id_local,
                                fechaInicio = hoy,
                                fechaFin = hoy,
                                precioTotal = local.precio_base
                            )
                            onReservaSuccess()
                        },
                        enabled = !esPropietario,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (esPropietario) "Eres el dueño" else "Reservar")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            val scrollState = rememberScrollState()

            Column(modifier = Modifier.verticalScroll(scrollState)) {
                // --- Image Header ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.fiesta),
                        contentDescription = "Imagen del local",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // --- Content Card ---
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = (-20).dp), // Overlap effect
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = local.nombre,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                        Text(
                            text = local.direccion,
                            color = Color.Gray,
                            fontSize = 16.sp
                        )

                        Divider(modifier = Modifier.padding(vertical = 16.dp))

                        Text(
                            text = "Información",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        InfoRow(icon = Icons.Default.Person, text = "${local.capacidad} personas")

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Descripción",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = local.descripcion,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Ubicación",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Map Placeholder
                        Text(
                            text = local.descripcion,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Map placeholder", color = Color.DarkGray)
                        }
                    }
                }
            }

            // --- Floating Buttons ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.7f), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
                IconButton(
                    onClick = { /* TODO: Favorite action */ },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.7f), CircleShape)
                ) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorito")
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 16.sp)
    }
}