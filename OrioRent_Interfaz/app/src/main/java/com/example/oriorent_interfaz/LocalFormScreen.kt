package com.example.oriorent_interfaz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    val dbHelper = remember { OrioRentDBHelper(context) }

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var precioBase by remember { mutableStateOf("") }
    var tipoPrecio by remember { mutableStateOf("Día") }
    var idCategoria by remember { mutableStateOf(1) }

    val categorias = remember { dbHelper.obtenerCategorias() }
    val tiposPrecio = listOf("Hora", "Día", "Semana")

    var expandedTipo by remember { mutableStateOf(false) }
    var expandedCat by remember { mutableStateOf(false) }

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
            // Sección Fotos
            Text("Fotos del producto", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(32.dp), tint = Color.Gray)
                }
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(2.dp, Color.Gray, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(32.dp), tint = Color.Gray)
                }
            }

            Spacer(Modifier.height(20.dp))

            // Formulario
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A4A7A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FormLabel("Título")
                    FormTextField(
                        value = nombre,
                        onValueChange = { if (it.length <= 50) nombre = it },
                        placeholder = "Ej: Casa de fiesta....",
                        counterText = "${nombre.length}/50"
                    )

                    FormLabel("Descripcion")
                    FormTextField(
                        value = descripcion,
                        onValueChange = { if (it.length <= 500) descripcion = it },
                        placeholder = "Ej: Lugar muy animado y preparado para una gran multitud y tipos de fiestas",
                        singleLine = false,
                        modifier = Modifier.height(120.dp),
                        counterText = "${descripcion.length}/500"
                    )

                    FormLabel("Precio")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FormTextField(
                            value = precioBase,
                            onValueChange = { precioBase = it },
                            placeholder = "€ 0.00",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                        ExposedDropdownMenuBox(
                            expanded = expandedTipo,
                            onExpandedChange = { expandedTipo = !expandedTipo },
                            modifier = Modifier.weight(0.6f)
                        ) {
                            OutlinedTextField(
                                value = tipoPrecio,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) },
                                shape = RoundedCornerShape(8.dp),
                                colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White),
                                modifier = Modifier.menuAnchor().height(50.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedTipo,
                                onDismissRequest = { expandedTipo = false }
                            ) {
                                tiposPrecio.forEach { tipo ->
                                    DropdownMenuItem(text = { Text(tipo) }, onClick = {
                                        tipoPrecio = tipo
                                        expandedTipo = false
                                    })
                                }
                            }
                        }
                    }

                    FormLabel("Categoria")
                    ExposedDropdownMenuBox(
                        expanded = expandedCat,
                        onExpandedChange = { expandedCat = !expandedCat }
                    ) {
                        OutlinedTextField(
                            value = categorias.find { it.id_categoria == idCategoria }?.nombre ?: "",
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Selecciona la categoria") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White),
                            modifier = Modifier.menuAnchor().fillMaxWidth().height(50.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCat,
                            onDismissRequest = { expandedCat = false }
                        ) {
                            categorias.forEach { cat ->
                                DropdownMenuItem(text = { Text(cat.nombre) }, onClick = {
                                    idCategoria = cat.id_categoria
                                    expandedCat = false
                                })
                            }
                        }
                    }

                    FormLabel("Ubicacion")
                    FormTextField(
                        value = direccion,
                        onValueChange = { direccion = it },
                        placeholder = "Ej: Calle los rosales, 03300....."
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val usuario = dbHelper.obtenerUsuarioPorEmail(usuarioEmail)
                            val result = dbHelper.insertarLocal(
                                nombre = nombre,
                                descripcion = descripcion,
                                direccion = direccion,
                                capacidad = 50,
                                precioBase = precioBase.toDoubleOrNull() ?: 0.0,
                                tipoPrecio = tipoPrecio,
                                idPropietario = usuario?.id_usuario ?: 1,
                                idCategoria = idCategoria
                            )
                            if (result != -1L) onSuccess()
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
    Column {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
            modifier = modifier.fillMaxWidth().heightIn(min = 50.dp),
            shape = RoundedCornerShape(8.dp),
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black
            )
        )
        counterText?.let {
            Text(
                text = it,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, end = 8.dp),
                textAlign = TextAlign.End,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
        }
    }
}
