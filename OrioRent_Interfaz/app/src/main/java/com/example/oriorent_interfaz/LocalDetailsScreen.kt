package com.example.oriorent_interfaz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(local?.nombre ?: "Detalles del Local") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (local == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Cargando...")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Imagen Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1976D2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = local.nombre,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${local.precio_base}€ / ${local.tipo_precio}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF1976D2),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = local.direccion,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = local.descripcion,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Capacidad: ${local.capacidad} personas",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(24.dp))

                if (esPropietario) {
                    Surface(
                        color = Color.LightGray,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Eres el propietario de este local",
                            modifier = Modifier.padding(16.dp),
                            color = Color.DarkGray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
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
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                    ) {
                        Text("ALQUILAR AHORA")
                    }
                }
            }
        }
    }
}
