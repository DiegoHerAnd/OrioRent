@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.oriorent_interfaz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PostalService(
    usuarioEmail: String,
    onBack: () -> Unit,
    onConversacionClick: (Int) -> Unit
) {
    val dbHelper = OrioRentDB
    val usuario  = remember(usuarioEmail) { dbHelper.obtenerUsuarioPorEmail(usuarioEmail) }
    val idUsuario = usuario?.id_usuario ?: -1

    var refreshKey by remember { mutableIntStateOf(0) }
    val conversaciones = remember(refreshKey, idUsuario) {
        dbHelper.obtenerConversacionesUsuario(idUsuario)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Mensajes", modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                    }
                },
                // Espacio vacío para centrar el título
                actions = { IconButton(onClick = {}) {} },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { pv ->
        if (conversaciones.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(pv), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("💬", fontSize = 48.sp)
                    Text("No tienes mensajes todavía.", color = Color.Gray, fontSize = 16.sp)
                    Text("Contacta con el dueño de un local\npara iniciar una conversación.",
                        color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(pv).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                items(conversaciones, key = { it.id_conversacion }) { conv ->
                    ConversacionItem(
                        conv = conv,
                        idUsuarioActual = idUsuario,
                        dbHelper = dbHelper,
                        onClick = { onConversacionClick(conv.id_conversacion) }
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun ConversacionItem(
    conv: Conversacion,
    idUsuarioActual: Int,
    dbHelper: OrioRentDB,
    onClick: () -> Unit
) {
    // El "otro" usuario de la conversación
    val idOtro = if (conv.id_usuario1 == idUsuarioActual) conv.id_usuario2 else conv.id_usuario1
    val otroUsuario = remember(idOtro) { dbHelper.obtenerUsuarioPorId(idOtro) }
    val local       = remember(conv.id_local) { dbHelper.obtenerLocalPorId(conv.id_local) }
    val inicial     = otroUsuario?.nombre?.take(1)?.uppercase() ?: "?"

    val tieneNoLeidos = conv.mensajes_no_leidos > 0

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            // Avatar con inicial
            Box(
                Modifier.size(52.dp).clip(CircleShape).background(Color(0xFF1A4A7A)),
                contentAlignment = Alignment.Center
            ) {
                Text(inicial, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text(
                        otroUsuario?.nombre ?: "Usuario",
                        fontWeight = FontWeight.Bold, fontSize = 15.sp,
                        color = Color.Black, modifier = Modifier.weight(1f)
                    )
                    Text(
                        conv.ultima_fecha.take(10),   // solo fecha yyyy-MM-dd
                        color = Color.Gray, fontSize = 12.sp
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    local?.nombre ?: "",
                    color = Color(0xFF1A4A7A), fontSize = 13.sp, fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(2.dp))
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text(
                        conv.ultimo_mensaje,
                        color = if (tieneNoLeidos) Color.Black else Color.Gray,
                        fontSize = 13.sp,
                        fontWeight = if (tieneNoLeidos) FontWeight.Medium else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (tieneNoLeidos) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            Modifier.size(20.dp).clip(CircleShape).background(Color(0xFF1A4A7A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                conv.mensajes_no_leidos.toString(),
                                color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}