package com.example.oriorent_interfaz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
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
fun MyBookingsScreen(
    usuarioEmail: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { OrioRentDBHelper(context) }
    val usuario = remember(usuarioEmail) { dbHelper.obtenerUsuarioPorEmail(usuarioEmail) }
    val idUsuario = usuario?.id_usuario ?: -1

    // refreshKey fuerza la relecture de la BD cuando se cancela
    var refreshKey by remember { mutableIntStateOf(0) }
    val reservas by remember(refreshKey, idUsuario) { mutableStateOf(dbHelper.obtenerReservasUsuario(idUsuario)) }

    var reservaACancelar by remember { mutableStateOf<Reserva?>(null) }

    // ── Diálogo de confirmación de cancelación ────────────────────────────
    if (reservaACancelar != null) {
        val localNombre = remember(reservaACancelar) {
            dbHelper.obtenerLocalPorId(reservaACancelar!!.id_local)?.nombre ?: "este local"
        }
        AlertDialog(
            onDismissRequest = { reservaACancelar = null },
            title = { Text("¿Cancelar reserva?", fontWeight = FontWeight.Bold) },
            text = { Text("Vas a cancelar la reserva de \"$localNombre\". Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        dbHelper.cancelarReserva(reservaACancelar!!.id_reserva)
                        reservaACancelar = null
                        refreshKey++          // ← fuerza actualizar la lista
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) { Text("Sí, cancelar") }
            },
            dismissButton = {
                TextButton(onClick = { reservaACancelar = null }) { Text("Volver") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Alquileres", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { pv ->
        if (reservas.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(pv).background(Color.White), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("📭", fontSize = 56.sp)
                    Text("No tienes ninguna reserva todavía.", color = Color.Gray, fontSize = 16.sp)
                    Button(onClick = onBackClick, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A4A7A)), shape = RoundedCornerShape(20.dp)) {
                        Text("Explorar locales")
                    }
                }
            }
        } else {
            val activas   = reservas.filter { it.estado != "Cancelada" }
            val historial = reservas.filter { it.estado == "Cancelada" }

            LazyColumn(
                Modifier.fillMaxSize().padding(pv).background(Color.White).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(Modifier.height(4.dp)) }

                if (activas.isNotEmpty()) {
                    item {
                        Text("Reservas activas", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A4A7A))
                    }
                    items(activas, key = { it.id_reserva }) { reserva ->
                        val local = remember(reserva.id_local) { dbHelper.obtenerLocalPorId(reserva.id_local) }
                        BookingItem(
                            reserva = reserva,
                            local = local,
                            onCancelarClick = { reservaACancelar = reserva }
                        )
                    }
                }

                if (historial.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text("Historial", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Gray)
                    }
                    items(historial, key = { it.id_reserva }) { reserva ->
                        val local = remember(reserva.id_local) { dbHelper.obtenerLocalPorId(reserva.id_local) }
                        BookingItem(reserva = reserva, local = local, onCancelarClick = null)
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun BookingItem(
    reserva: Reserva,
    local: Local?,
    onCancelarClick: (() -> Unit)?
) {
    val cancelada = reserva.estado == "Cancelada"
    val estadoColor = when (reserva.estado) {
        "Confirmada" -> Color(0xFF4CAF50)
        "Cancelada"  -> Color(0xFF9E9E9E)
        "Pendiente"  -> Color(0xFFFFA726)
        else         -> Color(0xFF4CAF50)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (cancelada) Color(0xFFF5F5F5) else Color(0xFFF0F2F5)),
        elevation = CardDefaults.cardElevation(if (cancelada) 0.dp else 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Nombre + estado
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(
                    local?.nombre ?: "Local desconocido",
                    fontSize = 17.sp, fontWeight = FontWeight.Bold,
                    color = if (cancelada) Color.Gray else Color(0xFF1A4A7A),
                    modifier = Modifier.weight(1f)
                )
                Surface(color = estadoColor.copy(alpha = if (cancelada) 0.5f else 1f), shape = RoundedCornerShape(16.dp)) {
                    Text(reserva.estado, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            // Fechas
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, null, Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (reserva.fecha_inicio == reserva.fecha_fin || reserva.fecha_fin.isEmpty())
                        reserva.fecha_inicio
                    else "${reserva.fecha_inicio}  →  ${reserva.fecha_fin}",
                    fontSize = 14.sp, color = Color.Gray
                )
            }

            Spacer(Modifier.height(10.dp))

            // Precio + botón cancelar
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(
                    "${"%.2f".format(reserva.precio_total)}€",
                    fontSize = 18.sp, fontWeight = FontWeight.Bold,
                    color = if (cancelada) Color.Gray else Color.Black
                )
                if (onCancelarClick != null) {
                    OutlinedButton(
                        onClick = onCancelarClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) { Text("Cancelar", fontSize = 13.sp) }
                }
            }
        }
    }
}