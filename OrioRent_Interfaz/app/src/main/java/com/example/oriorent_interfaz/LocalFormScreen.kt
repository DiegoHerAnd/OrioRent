package com.example.oriorent_interfaz

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalFormScreen(
    usuarioEmail: String,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = OrioRentDB

    var nombre      by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var direccion   by remember { mutableStateOf("") }
    var precioBase  by remember { mutableStateOf("") }
    var tipoPrecio  by remember { mutableStateOf("Día") }
    var idCategoria by remember { mutableIntStateOf(1) }

    val categorias  = remember { dbHelper.obtenerCategorias() }
    val tiposPrecio = listOf("Hora", "Día", "Semana")

    var expandedTipo by remember { mutableStateOf(false) }
    var expandedCat  by remember { mutableStateOf(false) }
    var errorMsg     by remember { mutableStateOf("") }

    // ── Imágenes ──────────────────────────────────────────────────────────
    // Lista de URIs seleccionadas (máximo 3)
    val imagenes = remember { mutableStateListOf<Uri>() }

    // Launcher para el selector de imágenes del sistema
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && imagenes.size < 3) {
            // Intentamos guardar permiso persistente para poder leer la imagen
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) { /* algunos URIs no lo admiten */ }
            imagenes.add(uri)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subir local", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // ── Sección de fotos ──────────────────────────────────────────
            Text("Fotos del local", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Text("Añade hasta 3 fotos", fontSize = 13.sp, color = Color.Gray)
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Botón de añadir (visible si hay menos de 3 imágenes)
                if (imagenes.size < 3) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE0E0E0))
                            .clickable { imageLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Add, null, Modifier.size(28.dp), tint = Color.Gray)
                            Text("Foto", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }

                // Miniaturas de imágenes seleccionadas
                imagenes.forEachIndexed { index, uri ->
                    Box(modifier = Modifier.size(90.dp)) {
                        val bitmap = remember(uri) {
                            try {
                                context.contentResolver.openInputStream(uri)?.use { stream ->
                                    BitmapFactory.decodeStream(stream)
                                }
                            } catch (_: Exception) { null }
                        }

                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Foto $index",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        } else {
                            Box(
                                Modifier.fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF1A4A7A)),
                                contentAlignment = Alignment.Center
                            ) { Text("✓", color = Color.White, fontSize = 24.sp) }
                        }

                        // Botón eliminar foto
                        IconButton(
                            onClick = { imagenes.removeAt(index) },
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.TopEnd)
                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Default.Close, null, Modifier.size(14.dp), tint = Color.White)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Formulario ────────────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A4A7A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {

                    FormLabel("Título")
                    FormTextField(
                        value = nombre,
                        onValueChange = { if (it.length <= 50) nombre = it },
                        placeholder = "Ej: Casa de fiesta...",
                        counterText = "${nombre.length}/50"
                    )

                    FormLabel("Descripción")
                    FormTextField(
                        value = descripcion,
                        onValueChange = { if (it.length <= 500) descripcion = it },
                        placeholder = "Ej: Lugar muy animado y preparado para una gran multitud",
                        singleLine = false,
                        modifier = Modifier.height(120.dp),
                        counterText = "${descripcion.length}/500"
                    )

                    FormLabel("Precio")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                        FormTextField(
                            value = precioBase,
                            onValueChange = { precioBase = it },
                            placeholder = "€ 0.00",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                        ExposedDropdownMenuBox(expanded = expandedTipo, onExpandedChange = { expandedTipo = !expandedTipo }, modifier = Modifier.weight(0.6f)) {
                            TextField(
                                value = tipoPrecio, onValueChange = {}, readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedTipo) },
                                shape = RoundedCornerShape(8.dp),
                                colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                                modifier = Modifier.menuAnchor().fillMaxWidth().height(50.dp)
                            )
                            ExposedDropdownMenu(expandedTipo, { expandedTipo = false }) {
                                tiposPrecio.forEach { tipo ->
                                    DropdownMenuItem(text = { Text(tipo) }, onClick = { tipoPrecio = tipo; expandedTipo = false })
                                }
                            }
                        }
                    }

                    FormLabel("Categoría")
                    ExposedDropdownMenuBox(expanded = expandedCat, onExpandedChange = { expandedCat = !expandedCat }, modifier = Modifier.fillMaxWidth()) {
                        TextField(
                            value = categorias.find { it.id_categoria == idCategoria }?.nombre ?: "",
                            onValueChange = {}, readOnly = true,
                            placeholder = { Text("Selecciona la categoría", fontSize = 14.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCat) },
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                            modifier = Modifier.menuAnchor().fillMaxWidth().height(50.dp)
                        )
                        ExposedDropdownMenu(expandedCat, { expandedCat = false }) {
                            categorias.forEach { cat ->
                                DropdownMenuItem(text = { Text(cat.nombre) }, onClick = { idCategoria = cat.id_categoria; expandedCat = false })
                            }
                        }
                    }

                    FormLabel("Ubicación")
                    FormTextField(value = direccion, onValueChange = { direccion = it }, placeholder = "Ej: Calle los Rosales, 03300...")

                    // Error
                    if (errorMsg.isNotEmpty()) {
                        Text(errorMsg, color = Color(0xFFFFCDD2), fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            // Validaciones
                            when {
                                nombre.isBlank() -> { errorMsg = "El título es obligatorio."; return@Button }
                                descripcion.isBlank() -> { errorMsg = "La descripción es obligatoria."; return@Button }
                                direccion.isBlank() -> { errorMsg = "La ubicación es obligatoria."; return@Button }
                                precioBase.toDoubleOrNull() == null -> { errorMsg = "Introduce un precio válido."; return@Button }
                            }
                            val usuario = dbHelper.obtenerUsuarioPorEmail(usuarioEmail)
                            val idLocal = dbHelper.insertarLocal(
                                nombre = nombre,
                                descripcion = descripcion,
                                direccion = direccion,
                                capacidad = 50,
                                precioBase = precioBase.toDouble(),
                                tipoPrecio = tipoPrecio,
                                idPropietario = usuario?.id_usuario ?: 1,
                                idCategoria = idCategoria
                            )
                            if (idLocal != -1L) {
                                // Guardamos las imágenes en la BD
                                imagenes.forEach { uri ->
                                    dbHelper.insertarImagenLocal(idLocal.toInt(), uri.toString())
                                }
                                onSuccess()
                            } else {
                                errorMsg = "Error al guardar el local. Inténtalo de nuevo."
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Subir local", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FormLabel(text: String) {
    Text(text, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    counterText: String? = null
) {
    Column(modifier = modifier) {
        TextField(
            value = value, onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
            shape = RoundedCornerShape(8.dp), singleLine = singleLine,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, cursorColor = Color.Black
            )
        )
        counterText?.let {
            Text(it, Modifier.fillMaxWidth().padding(top = 4.dp, end = 8.dp), textAlign = TextAlign.End, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
        }
    }
}