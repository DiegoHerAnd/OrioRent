package com.example.oriorent_interfaz

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalDetailsScreen(
    idLocal: Int,
    usuarioEmail: String,
    onBackClick: () -> Unit,
    onReservaSuccess: () -> Unit,
    onOwnerClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val dbHelper = remember { OrioRentDBHelper(context) }
    val local = remember(idLocal) { dbHelper.obtenerLocalPorId(idLocal) }
    val usuario = remember(usuarioEmail) { dbHelper.obtenerUsuarioPorEmail(usuarioEmail) }
    val idUsuario = usuario?.id_usuario ?: -1
    val propietario = remember(local) { local?.let { dbHelper.obtenerUsuarioPorId(it.id_propietario) } }

    var isFavorite by remember { mutableStateOf(dbHelper.esFavorito(idUsuario, idLocal)) }
    var mostrarDialogoReserva by remember { mutableStateOf(false) }
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    var errorMensaje by remember { mutableStateOf("") }

    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val sdfDisplay = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    var fechaInicio by remember { mutableStateOf(Calendar.getInstance()) }
    var fechaFin by remember { mutableStateOf(Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }) }
    var horaInicio by remember { mutableIntStateOf(9) }
    var horaFin by remember { mutableIntStateOf(11) }

    val esPorHoras = local?.tipo_precio?.lowercase() == "hora"
    val esPropietario = local?.id_propietario == idUsuario

    val precioTotal = remember(fechaInicio, fechaFin, horaInicio, horaFin, local) {
        val precio = local?.precio_base ?: 0.0
        when (local?.tipo_precio?.lowercase()) {
            "hora" -> precio * max(1, horaFin - horaInicio)
            "semana" -> {
                val dias = max(1L, ceil((fechaFin.timeInMillis - fechaInicio.timeInMillis) / 86_400_000.0).toLong())
                precio * ceil(dias / 7.0)
            }
            else -> {
                val dias = max(1L, ceil((fechaFin.timeInMillis - fechaInicio.timeInMillis) / 86_400_000.0).toLong())
                precio * dias
            }
        }
    }

    if (local == null) {
        Scaffold { Box(Modifier.fillMaxSize().padding(it), contentAlignment = Alignment.Center) { Text("Local no encontrado") } }
        return
    }

    // ── Diálogo de selección ──────────────────────────────────────────────
    if (mostrarDialogoReserva) {
        ReservaDialog(
            tipoPrecio = local.tipo_precio,
            precioBase = local.precio_base,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin,
            horaInicio = horaInicio,
            horaFin = horaFin,
            precioTotal = precioTotal,
            errorMensaje = errorMensaje,
            onFechaInicioChange = { cal ->
                fechaInicio = cal
                if (!esPorHoras && fechaFin.before(cal))
                    fechaFin = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 1) }
                errorMensaje = ""
            },
            onFechaFinChange = { fechaFin = it; errorMensaje = "" },
            onHoraInicioChange = { horaInicio = it; errorMensaje = "" },
            onHoraFinChange = { horaFin = it; errorMensaje = "" },
            onConfirmar = {
                if (esPorHoras && horaFin <= horaInicio) {
                    errorMensaje = "La hora de salida debe ser posterior a la de entrada."
                    return@ReservaDialog
                }
                val inicioStr = if (esPorHoras)
                    "${sdf.format(fechaInicio.time)} ${horaInicio.toString().padStart(2,'0')}:00"
                else sdf.format(fechaInicio.time)

                val finStr = if (esPorHoras)
                    "${sdf.format(fechaInicio.time)} ${horaFin.toString().padStart(2,'0')}:00"
                else sdf.format(fechaFin.time)

                if (dbHelper.verificarDisponibilidad(idLocal, inicioStr, finStr)) {
                    val res = dbHelper.insertarReserva(idUsuario, local.id_local, inicioStr, finStr, precioTotal)
                    if (res != -1L) { mostrarDialogoReserva = false; mostrarConfirmacion = true }
                    else errorMensaje = "Error al guardar la reserva. Inténtalo de nuevo."
                } else {
                    errorMensaje = "⚠️ El local no está disponible en las fechas seleccionadas."
                }
            },
            onDismiss = { mostrarDialogoReserva = false; errorMensaje = "" }
        )
    }

    // ── Confirmación exitosa ──────────────────────────────────────────────
    if (mostrarConfirmacion) {
        val resumen = if (esPorHoras)
            "${sdfDisplay.format(fechaInicio.time)}  ${horaInicio}:00 → ${horaFin}:00"
        else "${sdfDisplay.format(fechaInicio.time)} → ${sdfDisplay.format(fechaFin.time)}"

        AlertDialog(
            onDismissRequest = {},
            icon = { Text("✅", fontSize = 36.sp) },
            title = { Text("¡Reserva confirmada!", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Local: ${local.nombre}")
                    Text(resumen)
                    Spacer(Modifier.height(4.dp))
                    Text("${"%.2f".format(precioTotal)}€", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF1A4A7A))
                }
            },
            confirmButton = {
                Button(onClick = { mostrarConfirmacion = false; onReservaSuccess() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A4A7A))) { Text("Ver mis reservas") }
            },
            dismissButton = { TextButton(onClick = { mostrarConfirmacion = false }) { Text("Quedarme aquí") } }
        )
    }

    // ── Pantalla principal ────────────────────────────────────────────────
    Scaffold(
        bottomBar = {
            Surface(Modifier.fillMaxWidth(), shadowElevation = 8.dp) {
                Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth(),
                    Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Column {
                        Text("${local.precio_base}€", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("/ ${local.tipo_precio}", fontSize = 14.sp, color = Color.Gray)
                    }
                    Button(
                        onClick = { mostrarDialogoReserva = true },
                        enabled = !esPropietario,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A4A7A))
                    ) {
                        Icon(Icons.Default.DateRange, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (esPropietario) "Eres el dueño" else "Reservar")
                    }
                }
            }
        }
    ) { pv ->
        Box(Modifier.fillMaxSize().padding(pv)) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Box(Modifier.fillMaxWidth().height(300.dp)) {
                    Image(
                        painter = painterResource(if (local.id_categoria == 1) R.drawable.fiesta else R.drawable.logooriorent),
                        contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
                    )
                }
                Surface(Modifier.fillMaxSize().offset(y = (-20).dp),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    color = MaterialTheme.colorScheme.background) {
                    Column(Modifier.padding(24.dp)) {
                        Text(local.nombre, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                        Text(local.direccion, color = Color.Gray, fontSize = 16.sp)
                        HorizontalDivider(Modifier.padding(vertical = 16.dp))

                        // Tarjeta propietario
                        if (propietario != null && !esPropietario) {
                            Text("Propietario", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onOwnerClick(propietario.email) },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FF)),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF1A4A7A)),
                                        contentAlignment = Alignment.Center) {
                                        Text(propietario.nombre.take(1).uppercase(), color = Color.White,
                                            fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(propietario.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text("Ver perfil →", color = Color(0xFF1A4A7A), fontSize = 13.sp)
                                    }
                                }
                            }
                            HorizontalDivider(Modifier.padding(vertical = 16.dp))
                        }

                        Text("Información", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(Modifier.height(8.dp))
                        InfoRow(Icons.Default.Person, "${local.capacidad} personas")
                        Spacer(Modifier.height(16.dp))
                        Text("Descripción", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(local.descripcion, fontSize = 16.sp, lineHeight = 24.sp)
                        Spacer(Modifier.height(16.dp))
                        Text("Ubicación", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(Modifier.height(8.dp))
                        Box(Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray),
                            contentAlignment = Alignment.Center) { Text("Map placeholder", color = Color.DarkGray) }
                    }
                }
            }
            // Botones flotantes
            Row(Modifier.fillMaxWidth().padding(top = 16.dp, start = 16.dp, end = 16.dp), Arrangement.SpaceBetween) {
                IconButton(onClick = onBackClick, Modifier.background(Color.White.copy(alpha = 0.7f), CircleShape)) {
                    Icon(Icons.Default.ArrowBack, "Volver")
                }
                IconButton(onClick = { isFavorite = dbHelper.toggleFavorito(idUsuario, idLocal) },
                    Modifier.background(Color.White.copy(alpha = 0.7f), CircleShape)) {
                    Icon(if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        "Favorito", tint = if (isFavorite) Color.Red else Color.Black)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  ReservaDialog — nombre distinto de DatePickerDialog (Material3)
// ═══════════════════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaDialog(
    tipoPrecio: String,
    precioBase: Double,
    fechaInicio: Calendar,
    fechaFin: Calendar,
    horaInicio: Int,
    horaFin: Int,
    precioTotal: Double,
    errorMensaje: String,
    onFechaInicioChange: (Calendar) -> Unit,
    onFechaFinChange: (Calendar) -> Unit,
    onHoraInicioChange: (Int) -> Unit,
    onHoraFinChange: (Int) -> Unit,
    onConfirmar: () -> Unit,
    onDismiss: () -> Unit
) {
    val sdfDisplay = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val esPorHoras = tipoPrecio.lowercase() == "hora"

    var abrirPickerFechaInicio by remember { mutableStateOf(false) }
    var abrirPickerFechaFin by remember { mutableStateOf(false) }
    var abrirTimeInicio by remember { mutableStateOf(false) }
    var abrirTimeFin by remember { mutableStateOf(false) }

    val dateStateInicio = rememberDatePickerState(
        initialSelectedDateMillis = fechaInicio.timeInMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis >= System.currentTimeMillis() - 86_400_000L
        }
    )
    val dateStateFin = rememberDatePickerState(
        initialSelectedDateMillis = fechaFin.timeInMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis > fechaInicio.timeInMillis
        }
    )
    val timeStateInicio = rememberTimePickerState(initialHour = horaInicio, initialMinute = 0, is24Hour = true)
    val timeStateFin = rememberTimePickerState(initialHour = horaFin, initialMinute = 0, is24Hour = true)

    // DatePicker de Material3 para fecha inicio
    if (abrirPickerFechaInicio) {
        DatePickerDialog(
            onDismissRequest = { abrirPickerFechaInicio = false },
            confirmButton = {
                TextButton(onClick = {
                    dateStateInicio.selectedDateMillis?.let {
                        onFechaInicioChange(Calendar.getInstance().apply { timeInMillis = it })
                    }
                    abrirPickerFechaInicio = false
                }) { Text("Confirmar") }
            },
            dismissButton = { TextButton(onClick = { abrirPickerFechaInicio = false }) { Text("Cancelar") } }
        ) { DatePicker(state = dateStateInicio) }
        return
    }

    // DatePicker de Material3 para fecha fin
    if (abrirPickerFechaFin) {
        DatePickerDialog(
            onDismissRequest = { abrirPickerFechaFin = false },
            confirmButton = {
                TextButton(onClick = {
                    dateStateFin.selectedDateMillis?.let {
                        onFechaFinChange(Calendar.getInstance().apply { timeInMillis = it })
                    }
                    abrirPickerFechaFin = false
                }) { Text("Confirmar") }
            },
            dismissButton = { TextButton(onClick = { abrirPickerFechaFin = false }) { Text("Cancelar") } }
        ) { DatePicker(state = dateStateFin) }
        return
    }

    // TimePicker hora inicio
    if (abrirTimeInicio) {
        Dialog(onDismissRequest = { abrirTimeInicio = false }) {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Hora de entrada", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    TimePicker(state = timeStateInicio)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { abrirTimeInicio = false }) { Text("Cancelar") }
                        TextButton(onClick = { onHoraInicioChange(timeStateInicio.hour); abrirTimeInicio = false }) { Text("Confirmar") }
                    }
                }
            }
        }
        return
    }

    // TimePicker hora fin
    if (abrirTimeFin) {
        Dialog(onDismissRequest = { abrirTimeFin = false }) {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Hora de salida", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    TimePicker(state = timeStateFin)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { abrirTimeFin = false }) { Text("Cancelar") }
                        TextButton(onClick = { onHoraFinChange(timeStateFin.hour); abrirTimeFin = false }) { Text("Confirmar") }
                    }
                }
            }
        }
        return
    }

    // ── Diálogo principal ─────────────────────────────────────────────────
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(8.dp)) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Selecciona ${if (esPorHoras) "fecha y horas" else "las fechas"}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF1A4A7A))

                // Siempre: fecha de inicio
                SelectorButton(label = if (esPorHoras) "Fecha" else "Entrada",
                    value = sdfDisplay.format(fechaInicio.time),
                    onClick = { abrirPickerFechaInicio = true })

                if (esPorHoras) {
                    SelectorButton("Hora de entrada", "${horaInicio.toString().padStart(2,'0')}:00") { abrirTimeInicio = true }
                    SelectorButton("Hora de salida", "${horaFin.toString().padStart(2,'0')}:00") { abrirTimeFin = true }
                } else {
                    SelectorButton("Salida", sdfDisplay.format(fechaFin.time)) { abrirPickerFechaFin = true }
                }

                // Resumen
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FF)), shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        val duracion = if (esPorHoras) "${max(0, horaFin - horaInicio)} hora(s)"
                        else "${max(1L, ceil((fechaFin.timeInMillis - fechaInicio.timeInMillis) / 86_400_000.0).toLong())} día(s)"
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Text("Duración:", color = Color.Gray, fontSize = 14.sp); Text(duracion, fontWeight = FontWeight.Medium) }
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Text("Precio/$tipoPrecio:", color = Color.Gray, fontSize = 14.sp); Text("${"%.2f".format(precioBase)}€") }
                        HorizontalDivider()
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("Total:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("${"%.2f".format(precioTotal)}€", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A4A7A))
                        }
                    }
                }

                if (errorMensaje.isNotEmpty()) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)), shape = RoundedCornerShape(8.dp)) {
                        Text(errorMensaje, color = Color(0xFFD32F2F), fontSize = 14.sp, modifier = Modifier.padding(12.dp))
                    }
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) { Text("Cancelar") }
                    Button(onClick = onConfirmar, Modifier.weight(1f), shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A4A7A))) { Text("Confirmar") }
                }
            }
        }
    }
}

@Composable
private fun SelectorButton(label: String, value: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)) {
        Icon(Icons.Default.DateRange, null, Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontWeight = FontWeight.Medium, color = Color.Black, fontSize = 15.sp)
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