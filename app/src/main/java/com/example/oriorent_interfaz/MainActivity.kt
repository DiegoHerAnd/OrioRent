package com.example.oriorent_interfaz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                SimpleDatabaseScreen()
            }
        }
    }
}

// Helper simplificado
class SimpleDBHelper(context: Context) : SQLiteOpenHelper(context, "simpleDB.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE USUARIO (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                email TEXT
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS USUARIO")
        onCreate(db)
    }

    fun agregarUsuario(nombre: String, email: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("email", email)
        }
        db.insert("USUARIO", null, values)
        db.close()
    }

    fun contarUsuarios(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM USUARIO", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        db.close()
        return count
    }
}

@Composable
fun SimpleDatabaseScreen() {
    val context = LocalContext.current
    var contador by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Usuarios en DB: $contador",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val dbHelper = SimpleDBHelper(context)
                dbHelper.agregarUsuario("Usuario ${contador + 1}", "email${contador + 1}@test.com")
                contador = dbHelper.contarUsuarios()
            }
        ) {
            Text("Agregar Usuario")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val dbHelper = SimpleDBHelper(context)
                contador = dbHelper.contarUsuarios()
            }
        ) {
            Text("Actualizar Contador")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaLoginPreview() {
    MaterialTheme {
        // Necesitamos un contexto para el preview
        // Usamos remember para simular un contexto en preview
        val context = androidx.compose.ui.platform.LocalContext.current
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SimpleDatabaseScreen()
        }
    }
}