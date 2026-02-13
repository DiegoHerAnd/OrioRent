package com.example.oriorent_interfaz

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
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
                        dbHelper.close()
                        isLoading = false

                        if (loginExitoso) {
                            mensaje = "¡Login exitoso!"
                            Log.d("LOGIN", "Login EXITOSO - Navegando a main")
                            onLoginSuccess(email)
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

        TextButton(onClick = onRegistroClick) {
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
    }
}