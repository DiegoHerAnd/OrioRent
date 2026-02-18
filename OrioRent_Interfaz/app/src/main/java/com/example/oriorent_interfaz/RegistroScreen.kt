package com.example.oriorent_interfaz

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onRegistroSuccess: () -> Unit,
    onBackClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var aceptoTerminos by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón Volver
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
        }

        // Logo
        Image(
            painter = painterResource(id = R.drawable.logooriorent),
            contentDescription = "Logo OrioRent",
            modifier = Modifier
                .size(160.dp)
                .padding(top = 0.dp)
        )

        Text(
            text = "Registro",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = "¡Te damos la bienvenida!",
            fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Contenedor Azul del Formulario
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A4A7A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Campo Email
                RegistroLabel("Correo Electrónico")
                RegistroTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Introduce Correo"
                )

                // Campo Contraseña
                RegistroLabel("Contraseña")
                RegistroTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    placeholder = "Introduce Contraseña",
                    isPassword = true
                )

                // Campo Nombre de Usuario
                RegistroLabel("Nombre de usuario")
                RegistroTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    placeholder = "Introduce el nombre de usuario"
                )

                // Campo Teléfono
                RegistroLabel("Número de Teléfono")
                RegistroTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    placeholder = "Introduce el numero"
                )

                // Términos y Condiciones
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Checkbox(
                        checked = aceptoTerminos,
                        onCheckedChange = { aceptoTerminos = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color.White,
                            uncheckedColor = Color.White,
                            checkmarkColor = Color(0xFF1A4A7A)
                        )
                    )
                    Column {
                        Text(
                            text = "Aceptar Terminos y Condiciones",
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Ver Terminos y Condiciones",
                            color = Color.White,
                            fontSize = 13.sp,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { onTermsClick() }
                        )
                    }
                }

                // Botón Registrarse
                Button(
                    onClick = {
                        if (email.isBlank() || contrasena.isBlank() || nombre.isBlank()) {
                            mensaje = "Por favor completa los campos obligatorios"
                            return@Button
                        }
                        if (!aceptoTerminos) {
                            mensaje = "Debes aceptar los términos"
                            return@Button
                        }

                        isLoading = true
                        scope.launch {
                            try {
                                val dbHelper = OrioRentDBHelper(context)
                                val resultado = dbHelper.insertarUsuario(nombre, email, contrasena)
                                dbHelper.close()
                                isLoading = false

                                if (resultado != -1L) {
                                    onRegistroSuccess()
                                } else {
                                    mensaje = "Error: El email ya está registrado"
                                }
                            } catch (e: Exception) {
                                mensaje = "Error: ${e.message}"
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D)),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Registrarse", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        if (mensaje.isNotEmpty()) {
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun RegistroLabel(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.Black
        ),
        singleLine = true
    )
}
