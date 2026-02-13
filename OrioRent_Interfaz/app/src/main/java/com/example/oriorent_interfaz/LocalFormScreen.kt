package com.example.oriorent_interfaz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalFormScreen(
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { OrioRentDBHelper(context) }

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var capacidad by remember { mutableStateOf("") }
    var precioBase by remember { mutableStateOf("") }
    var tipoPrecio by remember { mutableStateOf("Hora") }
    var idCategoria by remember { mutableStateOf(1) }

    val categorias = remember { dbHelper.obtenerCategorias() }
    val tiposPrecio = listOf("Hora", "Día", "Semana")
    
    var expandedCat by remember { mutableStateOf(false) }
    var expandedTipo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Nuevo Local") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del Local") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = capacidad,
                onValueChange = { capacidad = it },
                label = { Text("Capacidad") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = precioBase,
                    onValueChange = { precioBase = it },
                    label = { Text("Precio") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                ExposedDropdownMenuBox(
                    expanded = expandedTipo,
                    onExpandedChange = { expandedTipo = !expandedTipo },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = tipoPrecio,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Por") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTipo,
                        onDismissRequest = { expandedTipo = false }
                    ) {
                        tiposPrecio.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    tipoPrecio = tipo
                                    expandedTipo = false
                                }
                            )
                        }
                    }
                }
            }

            // Selector de Categoría
            ExposedDropdownMenuBox(
                expanded = expandedCat,
                onExpandedChange = { expandedCat = !expandedCat }
            ) {
                OutlinedTextField(
                    value = categorias.find { it.id_categoria == idCategoria }?.nombre ?: "Seleccionar Categoría",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedCat,
                    onDismissRequest = { expandedCat = false }
                ) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria.nombre) },
                            onClick = {
                                idCategoria = categoria.id_categoria
                                expandedCat = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    val cap = capacidad.toIntOrNull() ?: 0
                    val precio = precioBase.toDoubleOrNull() ?: 0.0
                    
                    // Nota: id_propietario hardcodeado a 1 para simplificar o podrías obtenerlo del usuario logueado
                    // En este punto, como no tenemos un sistema de sesión persistente global complejo,
                    // el DBHelper maneja la inserción.
                    val result = dbHelper.insertarLocal(
                        nombre = nombre,
                        descripcion = descripcion,
                        direccion = direccion,
                        capacidad = cap,
                        precioBase = precio,
                        tipoPrecio = tipoPrecio,
                        idPropietario = 1, 
                        idCategoria = idCategoria
                    )
                    
                    if (result != -1L) {
                        onSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nombre.isNotBlank() && direccion.isNotBlank() && precioBase.isNotBlank()
            ) {
                Text("Registrar Local")
            }
        }
    }
}
