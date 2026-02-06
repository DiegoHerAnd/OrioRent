package com.example.oriorent_interfaz

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "App iniciada")

        setContent {
            MaterialTheme {
                OrioRentApp()
            }
        }
    }
}

@Composable
fun OrioRentApp() {
    var pantallaActual by remember { mutableStateOf("login") }

    when (pantallaActual) {
        "login" -> LoginScreen(
            onLoginSuccess = { pantallaActual = "main" },
            onRegistroClick = { pantallaActual = "registro" }
        )
        "registro" -> RegistroScreen(
            onRegistroSuccess = { pantallaActual = "login" },
            onBackClick = { pantallaActual = "login" }
        )
        "main" -> MainScreen(
            onLogout = { pantallaActual = "login" }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegistroClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(Color(0xFFE0E0E0)),
    horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        Image(
            painter = painterResource(id = R.drawable.logooriorent),
            contentDescription = "Logo OrioRent",
            modifier = Modifier
                .size(220.dp)
                .padding(bottom = 12.dp)
        )

        Text(
            text = "Alquiler de Locales",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trim() },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isBlank() || contrasena.isBlank()) {
                    mensaje = "Por favor completa todos los campos"
                    return@Button
                }

                isLoading = true
                scope.launch {
                    try {
                        Log.d("LOGIN", "=== INICIO LOGIN ===")
                        Log.d("LOGIN", "Email ingresado: '$email'")
                        Log.d("LOGIN", "Contraseña ingresada: '${contrasena.take(3)}...'")

                        val dbHelper = OrioRentDBHelper(context)

                        // Ver todos los usuarios en la DB
                        val todosUsuarios = dbHelper.obtenerTodosUsuarios()
                        Log.d("LOGIN", "Total usuarios en DB: ${todosUsuarios.size}")
                        todosUsuarios.forEach { usuario ->
                            Log.d("LOGIN", "Usuario DB: email='${usuario.email}', pass='${usuario.contrasena}'")
                        }

                        val loginExitoso = dbHelper.verificarLogin(email, contrasena)
                        Log.d("LOGIN", "Resultado login: $loginExitoso")

                        dbHelper.close()
                        isLoading = false

                        if (loginExitoso) {
                            mensaje = "¡Login exitoso!"
                            Log.d("LOGIN", "Login EXITOSO - Navegando a main")
                            onLoginSuccess()
                        } else {
                            mensaje = "Email o contraseña incorrectos"
                            Log.d("LOGIN", "Login FALLIDO")
                        }
                    } catch (e: Exception) {
                        Log.e("LOGIN", "ERROR en login: ${e.message}", e)
                        mensaje = "Error: ${e.message}"
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Iniciar Sesión")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onRegistroClick
        ) {
            Text("¿No tienes cuenta? Regístrate")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (mensaje.isNotEmpty()) {
            Text(
                text = mensaje,
                color = if (mensaje.contains("incorrectos") || mensaje.contains("error", ignoreCase = true))
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Botón de debug
        Button(
            onClick = {
                scope.launch {
                    try {
                        Log.d("DEBUG", "=== BOTÓN DEBUG ===")
                        val dbHelper = OrioRentDBHelper(context)
                        val usuarios = dbHelper.obtenerTodosUsuarios()
                        val categorias = dbHelper.obtenerCategorias()

                        Log.d("DEBUG", "Usuarios: ${usuarios.size}")
                        usuarios.forEach {
                            Log.d("DEBUG", "  - ${it.nombre} | ${it.email} | ${it.contrasena}")
                        }

                        Log.d("DEBUG", "Categorías: ${categorias.size}")
                        categorias.forEach {
                            Log.d("DEBUG", "  - ${it.nombre}")
                        }

                        val detalleUsuarios = usuarios.joinToString("\n") {
                            "${it.nombre}: ${it.email}"
                        }

                        mensaje = """
                            Usuarios: ${usuarios.size}
                            Categorías: ${categorias.size}
                            
                            $detalleUsuarios
                        """.trimIndent()

                        dbHelper.close()
                    } catch (e: Exception) {
                        Log.e("DEBUG", "Error en debug: ${e.message}", e)
                        mensaje = "Error: ${e.message}"
                    }
                }
            },
            modifier = Modifier.padding(top = 32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("DEBUG: Ver datos")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onRegistroSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("← Volver")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Crear Cuenta",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trim() },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmarContrasena,
            onValueChange = { confirmarContrasena = it },
            label = { Text("Confirmar contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // Validaciones
                if (nombre.isBlank() || email.isBlank() || contrasena.isBlank()) {
                    mensaje = "Por favor completa todos los campos"
                    return@Button
                }

                if (contrasena != confirmarContrasena) {
                    mensaje = "Las contraseñas no coinciden"
                    return@Button
                }

                if (contrasena.length < 6) {
                    mensaje = "La contraseña debe tener al menos 6 caracteres"
                    return@Button
                }

                isLoading = true
                scope.launch {
                    try {
                        Log.d("REGISTRO", "=== INICIO REGISTRO ===")
                        Log.d("REGISTRO", "Nombre: '$nombre'")
                        Log.d("REGISTRO", "Email: '$email'")
                        Log.d("REGISTRO", "Contraseña: '${contrasena.take(3)}...'")

                        val dbHelper = OrioRentDBHelper(context)
                        val resultado = dbHelper.insertarUsuario(nombre, email, contrasena)

                        Log.d("REGISTRO", "Resultado inserción: $resultado")

                        // Verificar que se insertó
                        if (resultado != -1L) {
                            val usuarioInsertado = dbHelper.obtenerUsuarioPorEmail(email)
                            Log.d("REGISTRO", "Usuario insertado verificado: $usuarioInsertado")
                        }

                        dbHelper.close()
                        isLoading = false

                        if (resultado != -1L) {
                            mensaje = "¡Registro exitoso! Por favor inicia sesión"
                            Log.d("REGISTRO", "Registro EXITOSO")
                            // Limpiar campos
                            nombre = ""
                            email = ""
                            contrasena = ""
                            confirmarContrasena = ""

                            // Esperar un poco antes de volver
                            kotlinx.coroutines.delay(1500)
                            onRegistroSuccess()
                        } else {
                            mensaje = "Error: El email ya está registrado"
                            Log.d("REGISTRO", "Registro FALLIDO - Email duplicado")
                        }
                    } catch (e: Exception) {
                        Log.e("REGISTRO", "ERROR en registro: ${e.message}", e)
                        mensaje = "Error: ${e.message}"
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Registrarse")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (mensaje.isNotEmpty()) {
            Text(
                text = mensaje,
                color = if (mensaje.contains("éxito", ignoreCase = true))
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var usuarios by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            Log.d("MAIN", "Cargando datos del dashboard")
            val dbHelper = OrioRentDBHelper(context)
            usuarios = dbHelper.obtenerTodosUsuarios()
            categorias = dbHelper.obtenerCategorias()
            Log.d("MAIN", "Usuarios: ${usuarios.size}, Categorías: ${categorias.size}")
            dbHelper.close()
        } catch (e: Exception) {
            Log.e("MAIN", "Error cargando datos: ${e.message}", e)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineMedium
            )

            Button(onClick = {
                Log.d("MAIN", "Cerrando sesión")
                onLogout()
            }) {
                Text("Cerrar Sesión")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tarjetas de estadísticas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = usuarios.size.toString(),
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text("Usuarios registrados")
                }
            }

            Card(
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = categorias.size.toString(),
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text("Categorías")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Lista de usuarios
        Text(
            text = "Usuarios registrados:",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(usuarios) { usuario ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = usuario.nombre,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = usuario.email,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Contraseña: ${usuario.contrasena}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "Registrado: ${usuario.fecha_registro}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    MaterialTheme {
        LoginScreen(
            onLoginSuccess = {},
            onRegistroClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegistroPreview() {
    MaterialTheme {
        RegistroScreen(
            onRegistroSuccess = {},
            onBackClick = {}
        )
    }
}