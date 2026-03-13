package com.example.oriorent_interfaz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    idConversacion: Int,
    usuarioEmail: String,
    onBackClick: () -> Unit
) {
    val context   = LocalContext.current
    val dbHelper  = remember { OrioRentDBHelper(context) }
    val usuario   = remember(usuarioEmail) { dbHelper.obtenerUsuarioPorEmail(usuarioEmail) }
    val idUsuario = usuario?.id_usuario ?: -1

    val conversacion = remember(idConversacion) { dbHelper.obtenerConversacionPorId(idConversacion) }
    val idOtro = remember(conversacion, idUsuario) {
        if (conversacion?.id_usuario1 == idUsuario) conversacion?.id_usuario2 else conversacion?.id_usuario1
    } ?: -1
    val otroUsuario = remember(idOtro) { dbHelper.obtenerUsuarioPorId(idOtro) }
    val local       = remember(conversacion) { conversacion?.let { dbHelper.obtenerLocalPorId(it.id_local) } }

    var refreshKey by remember { mutableIntStateOf(0) }
    val mensajes = remember(refreshKey) {
        dbHelper.obtenerMensajesConversacion(idConversacion).also {
            // Marcar como leídos los mensajes del otro al abrir el chat
            dbHelper.marcarMensajesLeidos(idConversacion, idUsuario)
        }
    }

    var textoMensaje by remember { mutableStateOf("") }
    val listState    = rememberLazyListState()
    val scope        = rememberCoroutineScope()

    // Scroll al último mensaje cuando cambie la lista
    LaunchedEffect(mensajes.size) {
        if (mensajes.isNotEmpty()) listState.animateScrollToItem(mensajes.size - 1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF1A4A7A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                otroUsuario?.nombre?.take(1)?.uppercase() ?: "?",
                                color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(otroUsuario?.nombre ?: "Usuario", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            local?.let {
                                Text(it.nombre, fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(Modifier.fillMaxWidth(), shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = textoMensaje,
                        onValueChange = { textoMensaje = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Escribe un mensaje...", fontSize = 14.sp) },
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF0F2F5),
                            unfocusedContainerColor = Color(0xFFF0F2F5),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        maxLines = 4
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            val texto = textoMensaje.trim()
                            if (texto.isNotEmpty()) {
                                val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                                dbHelper.insertarMensaje(
                                    idConversacion = idConversacion,
                                    idRemitente    = idUsuario,
                                    contenido      = texto,
                                    fechaHora      = fecha
                                )
                                textoMensaje = ""
                                refreshKey++
                                scope.launch {
                                    if (mensajes.isNotEmpty()) listState.animateScrollToItem(mensajes.size)
                                }
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                if (textoMensaje.isNotBlank()) Color(0xFF1A4A7A) else Color(0xFFCCCCCC),
                                CircleShape
                            )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, "Enviar", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { pv ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(pv).padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }
            items(mensajes, key = { it.id_mensaje }) { mensaje ->
                val esMio = mensaje.id_remitente == idUsuario
                BurbujaMensaje(mensaje = mensaje, esMio = esMio)
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
fun BurbujaMensaje(mensaje: Mensaje, esMio: Boolean) {
    val bgColor   = if (esMio) Color(0xFF1A4A7A) else Color.White
    val textColor = if (esMio) Color.White else Color.Black
    val hora      = mensaje.fecha_hora.let {
        if (it.length >= 16) it.substring(11, 16) else it
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (esMio) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (esMio) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .background(
                        bgColor,
                        RoundedCornerShape(
                            topStart = 16.dp, topEnd = 16.dp,
                            bottomStart = if (esMio) 16.dp else 4.dp,
                            bottomEnd   = if (esMio) 4.dp  else 16.dp
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(mensaje.contenido, color = textColor, fontSize = 15.sp, lineHeight = 20.sp)
            }
            Spacer(Modifier.height(2.dp))
            Text(hora, fontSize = 11.sp, color = Color.Gray)
        }
    }
}
