package com.example.oriorent_interfaz

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
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
        "main" -> MainScreenNew(
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

                        val dbHelper = OrioRentDBHelper(context)
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

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = mensaje,
                color = if (mensaje.contains("incorrectos") || mensaje.contains("error", ignoreCase = true))
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
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
                        val dbHelper = OrioRentDBHelper(context)
                        val resultado = dbHelper.insertarUsuario(nombre, email, contrasena)

                        dbHelper.close()
                        isLoading = false

                        if (resultado != -1L) {
                            mensaje = "¡Registro exitoso! Por favor inicia sesión"
                            nombre = ""
                            email = ""
                            contrasena = ""
                            confirmarContrasena = ""

                            kotlinx.coroutines.delay(1500)
                            onRegistroSuccess()
                        } else {
                            mensaje = "Error: El email ya está registrado"
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

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
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

// ===== NUEVA PANTALLA PRINCIPAL =====
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenNew(onLogout: () -> Unit) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    var searchText by remember { mutableStateOf("") }
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            val dbHelper = OrioRentDBHelper(context)
            categorias = dbHelper.obtenerCategorias()
            dbHelper.close()
        } catch (e: Exception) {
            Log.e("MAIN", "Error cargando datos: ${e.message}", e)
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                // Barra de búsqueda
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = { Text("Search") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar"
                        )
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = "Voz"
                        )
                    },
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF1976D2)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de Favoritos y Categorías
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("FAVORITOS")
                    }

                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Default.AccountBox,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CATEGORÍAS")
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Email, contentDescription = "Mensajes") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Share, contentDescription = "Compartir") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Sección "¡Lo mas buscado!"
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "¡Lo mas buscado!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            CategoryCard(
                                title = "Fiesta",
                                color = Color(0xFF1976D2)
                            )
                        }
                        item {
                            CategoryCard(
                                title = "Reunion",
                                color = Color(0xFF1976D2)
                            )
                        }
                    }
                }
            }

            // Sección "Destacados"
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Destacados",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Lista de locales destacados
            items(2) { index ->
                LocalCard(
                    precio = if (index == 0) "85€/hora" else "350€/D",
                    descripcion = if (index == 0) "Sala de fiestas privadas" else "Sala de reuniones",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Sección "Lo mas reciente..."
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Lo mas reciente...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Lista de locales recientes
            items(2) { index ->
                LocalCard(
                    precio = "85€/hora",
                    descripcion = "Sala de fiestas privadas",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Espaciado final
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(140.dp)
            .height(80.dp)
            .clickable { /* TODO */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomStart
        ) {
            // Aquí podrías agregar una imagen de fondo
            // Para este ejemplo, solo mostramos el texto
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun LocalCard(
    precio: String,
    descripcion: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clickable { /* TODO */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Imagen del local (placeholder con color)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFF1976D2))
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Aquí iría la imagen real del local
                Icon(
                    Icons.Default.Home,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }

            // Información del local
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = precio,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        MainScreenNew(onLogout = {})
    }
}

