package com.example.oriorent_interfaz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Términos y Condiciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1A4A7A)
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F7FA))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Bienvenido a OrioRent",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A4A7A),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Por favor, lea atentamente estos términos antes de usar nuestra plataforma.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                TermsSection(
                    title = "1. Aceptación de los Términos",
                    content = "Al crear una cuenta en OrioRent, usted acepta cumplir con estos Términos y Condiciones de uso, todas las leyes y regulaciones aplicables, y acepta que es responsable del cumplimiento de cualquier ley local aplicable."
                )
            }

            item {
                TermsSection(
                    title = "2. Uso de la Plataforma",
                    content = "OrioRent es un mercado que permite a los usuarios publicar y alquilar locales. No somos dueños, ni gestionamos, ni controlamos los locales publicados. Nuestra responsabilidad se limita a facilitar la conexión entre arrendadores y arrendatarios."
                )
            }

            item {
                TermsSection(
                    title = "3. Registro y Seguridad",
                    content = "Usted es responsable de mantener la confidencialidad de su cuenta y contraseña. Debe proporcionarnos información verídica y actualizada en todo momento. El uso de identidades falsas está estrictamente prohibido."
                )
            }

            item {
                TermsSection(
                    title = "4. Pagos y Cancelaciones",
                    content = "Los precios son establecidos por los propietarios. OrioRent puede aplicar una comisión por servicio. Las políticas de cancelación varían según el local y deben ser respetadas una vez confirmada la reserva."
                )
            }

            item {
                TermsSection(
                    title = "5. Privacidad",
                    content = "Su privacidad es importante para nosotros. Consulte nuestra Política de Privacidad para entender cómo recopilamos, usamos y compartimos su información personal."
                )
            }

            item {
                TermsSection(
                    title = "6. Conducta del Usuario",
                    content = "Se prohíbe cualquier uso ilícito de los locales alquilados. Los daños causados a la propiedad serán responsabilidad exclusiva del arrendatario."
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Última actualización: Mayo 2024",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun TermsSection(title: String, content: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A4A7A)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            color = Color(0xFF444444),
            textAlign = TextAlign.Justify
        )
    }
}
