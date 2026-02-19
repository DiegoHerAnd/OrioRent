@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.oriorent_interfaz

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Conversation(
    val sender: String,
    val propertyName: String,
    val lastMessage: String,
    val date: String,
    @DrawableRes val propertyImage: Int
)

fun parseFechaSimple(fecha: String): Int {
    val meses = listOf("ene", "feb", "mar", "abr", "may", "jun",
        "jul", "ago", "sep", "oct", "nov", "dic")
    val partes = fecha.trim().split(" ")
    val dia = partes[0].toIntOrNull() ?: 0
    val mes = meses.indexOf(partes.getOrNull(1)?.lowercase() ?: "")
    return mes * 100 + dia
}

val sampleConversations = listOf(
    Conversation(
        sender = "Juan García",
        propertyName = "Sala de reuniones",
        lastMessage = "Hola, ¿qué tal?",
        date = "08 ene",
        propertyImage = R.drawable.ic_launcher_background
    ),
    Conversation(
        sender = "Luis Pérez",
        propertyName = "Sala de fiestas privada",
        lastMessage = "Hola no tengo ni idea...",
        date = "11 nov",
        propertyImage = R.drawable.ic_launcher_background
    ),
    Conversation(
        sender = "Marta Santos",
        propertyName = "Sala de reuniones",
        lastMessage = "Buenas mira te comento......",
        date = "03 nov",
        propertyImage = R.drawable.ic_launcher_background
    )
)

@Composable
fun PostalService(onBack: () -> Unit) {
    var ordenAscendente by remember { mutableStateOf(false) }

    val conversacionesOrdenadas = remember(ordenAscendente) {
        if (ordenAscendente)
            sampleConversations.sortedBy { parseFechaSimple(it.date) }
        else
            sampleConversations.sortedByDescending { parseFechaSimple(it.date) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mensajes",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { ordenAscendente = !ordenAscendente}) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Filtrar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            items(conversacionesOrdenadas) { conversation ->
                ConversationItem(conversation = conversation)
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun ConversationItem(conversation: Conversation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = conversation.propertyImage),
                contentDescription = "Imagen del local",
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = conversation.sender,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = conversation.date,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = conversation.propertyName,
                    color = Color(0xFF1976D2),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = conversation.lastMessage,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun PostalServicePreview() {
    MaterialTheme {
        PostalService(onBack = {})
    }
}
