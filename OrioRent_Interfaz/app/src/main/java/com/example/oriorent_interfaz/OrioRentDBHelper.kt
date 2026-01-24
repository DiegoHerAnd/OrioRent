package com.example.oriorent_interfaz

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class OrioRentDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "orioRent.db"
        private const val DATABASE_VERSION = 2  // Incrementado para forzar recreación

        // Tablas
        const val TABLE_USUARIO = "USUARIO"
        const val TABLE_LOCAL = "LOCAL"
        const val TABLE_CATEGORIA = "CATEGORIA"
        const val TABLE_RESERVA = "RESERVA"
        const val TABLE_METODOPAGO = "METODOPAGO"

        private const val CREATE_TABLE_USUARIO = """
            CREATE TABLE USUARIO (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                contrasena TEXT NOT NULL,
                fecha_registro TEXT NOT NULL
            )
        """

        private const val CREATE_TABLE_CATEGORIA = """
            CREATE TABLE CATEGORIA (
                id_categoria INTEGER PRIMARY KEY,
                nombre TEXT NOT NULL,
                descripcion TEXT
            )
        """

        private const val CREATE_TABLE_LOCAL = """
            CREATE TABLE LOCAL (
                id_local INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                descripcion TEXT,
                direccion TEXT,
                capacidad INTEGER,
                precio_base REAL,
                id_propietario INTEGER,
                id_categoria INTEGER,
                FOREIGN KEY (id_propietario) REFERENCES USUARIO(id_usuario),
                FOREIGN KEY (id_categoria) REFERENCES CATEGORIA(id_categoria)
            )
        """

        private const val CREATE_TABLE_RESERVA = """
            CREATE TABLE RESERVA (
                id_reserva INTEGER PRIMARY KEY AUTOINCREMENT,
                fecha_inicio TEXT NOT NULL,
                fecha_fin TEXT NOT NULL,
                estado TEXT NOT NULL,
                precio_total REAL NOT NULL,
                id_usuario INTEGER,
                id_local INTEGER,
                FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario),
                FOREIGN KEY (id_local) REFERENCES LOCAL(id_local)
            )
        """

        private const val CREATE_TABLE_METODOPAGO = """
            CREATE TABLE METODOPAGO (
                id_pago INTEGER PRIMARY KEY AUTOINCREMENT,
                metodo_pago TEXT,
                hora_fecha TEXT,
                importe REAL,
                estado TEXT,
                id_usuario INTEGER,
                id_reserva INTEGER,
                FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario),
                FOREIGN KEY (id_reserva) REFERENCES RESERVA(id_reserva)
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("DB", "=== CREANDO BASE DE DATOS ===")
        try {
            db.execSQL(CREATE_TABLE_USUARIO)
            Log.d("DB", "Tabla USUARIO creada")

            db.execSQL(CREATE_TABLE_CATEGORIA)
            Log.d("DB", "Tabla CATEGORIA creada")

            db.execSQL(CREATE_TABLE_LOCAL)
            Log.d("DB", "Tabla LOCAL creada")

            db.execSQL(CREATE_TABLE_RESERVA)
            Log.d("DB", "Tabla RESERVA creada")

            db.execSQL(CREATE_TABLE_METODOPAGO)
            Log.d("DB", "Tabla METODOPAGO creada")

            insertarDatosIniciales(db)
            Log.d("DB", "Base de datos creada exitosamente")
        } catch (e: Exception) {
            Log.e("DB", "Error creando base de datos: ${e.message}", e)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("DB", "=== ACTUALIZANDO BASE DE DATOS de v$oldVersion a v$newVersion ===")

        db.execSQL("DROP TABLE IF EXISTS $TABLE_METODOPAGO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RESERVA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LOCAL")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIO")

        onCreate(db)
    }

    private fun insertarDatosIniciales(db: SQLiteDatabase) {
        Log.d("DB", "Insertando datos iniciales...")

        try {
            // Insertar categorías
            val categorias = listOf(
                1 to ("Salón de eventos" to "Espacios para celebraciones"),
                2 to ("Oficina" to "Espacios de trabajo"),
                3 to ("Almacén" to "Espacios de almacenamiento"),
                4 to ("Local comercial" to "Para negocios"),
                5 to ("Estudio" to "Para artistas y creadores")
            )

            categorias.forEach { (id, categoria) ->
                val (nombre, descripcion) = categoria
                val values = ContentValues().apply {
                    put("id_categoria", id)
                    put("nombre", nombre)
                    put("descripcion", descripcion)
                }
                val result = db.insert(TABLE_CATEGORIA, null, values)
                Log.d("DB", "Categoría '$nombre' insertada con ID: $result")
            }

            // Insertar usuario admin
            val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val usuarioAdmin = ContentValues().apply {
                put("nombre", "Administrador")
                put("email", "admin@oriorient.com")
                put("contrasena", "admin123")
                put("fecha_registro", fechaActual)
            }
            val adminId = db.insert(TABLE_USUARIO, null, usuarioAdmin)
            Log.d("DB", "Usuario admin insertado con ID: $adminId")

            // Verificar inserción
            val cursor = db.rawQuery("SELECT * FROM $TABLE_USUARIO WHERE id_usuario = ?", arrayOf(adminId.toString()))
            if (cursor.moveToFirst()) {
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val pass = cursor.getString(cursor.getColumnIndexOrThrow("contrasena"))
                Log.d("DB", "Verificación admin: email='$email', pass='$pass'")
            }
            cursor.close()

        } catch (e: Exception) {
            Log.e("DB", "Error insertando datos iniciales: ${e.message}", e)
        }
    }

    fun insertarUsuario(nombre: String, email: String, contrasena: String): Long {
        Log.d("DB", "insertarUsuario() - nombre='$nombre', email='$email'")

        return try {
            val db = writableDatabase
            val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            val values = ContentValues().apply {
                put("nombre", nombre)
                put("email", email)
                put("contrasena", contrasena)
                put("fecha_registro", fechaActual)
            }

            val resultado = db.insert(TABLE_USUARIO, null, values)
            Log.d("DB", "Usuario insertado con ID: $resultado")

            if (resultado != -1L) {
                // Verificar inserción
                val cursor = db.rawQuery("SELECT * FROM $TABLE_USUARIO WHERE id_usuario = ?", arrayOf(resultado.toString()))
                if (cursor.moveToFirst()) {
                    val emailVerificado = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                    Log.d("DB", "Usuario verificado en DB: $emailVerificado")
                }
                cursor.close()
            }

            resultado
        } catch (e: Exception) {
            Log.e("DB", "Error insertando usuario: ${e.message}", e)
            -1L
        }
    }

    fun verificarLogin(email: String, contrasena: String): Boolean {
        Log.d("DB", "verificarLogin() - email='$email', pass='${contrasena.take(3)}...'")

        return try {
            val db = readableDatabase
            val query = "SELECT * FROM $TABLE_USUARIO WHERE email = ? AND contrasena = ?"
            val cursor = db.rawQuery(query, arrayOf(email, contrasena))

            val existe = cursor.count > 0

            if (existe && cursor.moveToFirst()) {
                val nombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                Log.d("DB", "Login exitoso para: $nombreUsuario")
            } else {
                Log.d("DB", "Login fallido - usuario no encontrado")

                // Debug: mostrar todos los emails en la DB
                val cursorDebug = db.rawQuery("SELECT email, contrasena FROM $TABLE_USUARIO", null)
                Log.d("DB", "Emails en DB:")
                while (cursorDebug.moveToNext()) {
                    val emailDb = cursorDebug.getString(0)
                    val passDb = cursorDebug.getString(1)
                    Log.d("DB", "  - email='$emailDb', pass='$passDb'")
                }
                cursorDebug.close()
            }

            cursor.close()
            existe
        } catch (e: Exception) {
            Log.e("DB", "Error verificando login: ${e.message}", e)
            false
        }
    }

    fun obtenerUsuarioPorEmail(email: String): Usuario? {
        Log.d("DB", "obtenerUsuarioPorEmail() - email='$email'")

        return try {
            val db = readableDatabase
            val query = "SELECT * FROM $TABLE_USUARIO WHERE email = ?"
            val cursor = db.rawQuery(query, arrayOf(email))

            cursor.use {
                if (it.moveToFirst()) {
                    val usuario = Usuario(
                        id_usuario = it.getInt(it.getColumnIndexOrThrow("id_usuario")),
                        nombre = it.getString(it.getColumnIndexOrThrow("nombre")),
                        email = it.getString(it.getColumnIndexOrThrow("email")),
                        contrasena = it.getString(it.getColumnIndexOrThrow("contrasena")),
                        fecha_registro = it.getString(it.getColumnIndexOrThrow("fecha_registro"))
                    )
                    Log.d("DB", "Usuario encontrado: ${usuario.nombre}")
                    usuario
                } else {
                    Log.d("DB", "Usuario no encontrado")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("DB", "Error obteniendo usuario: ${e.message}", e)
            null
        }
    }

    fun obtenerTodosUsuarios(): List<Usuario> {
        Log.d("DB", "obtenerTodosUsuarios()")

        val usuarios = mutableListOf<Usuario>()

        return try {
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT * FROM $TABLE_USUARIO ORDER BY id_usuario", null)

            cursor.use {
                while (it.moveToNext()) {
                    val usuario = Usuario(
                        id_usuario = it.getInt(it.getColumnIndexOrThrow("id_usuario")),
                        nombre = it.getString(it.getColumnIndexOrThrow("nombre")),
                        email = it.getString(it.getColumnIndexOrThrow("email")),
                        contrasena = it.getString(it.getColumnIndexOrThrow("contrasena")),
                        fecha_registro = it.getString(it.getColumnIndexOrThrow("fecha_registro"))
                    )
                    usuarios.add(usuario)
                }
            }

            Log.d("DB", "Total usuarios obtenidos: ${usuarios.size}")
            usuarios
        } catch (e: Exception) {
            Log.e("DB", "Error obteniendo usuarios: ${e.message}", e)
            usuarios
        }
    }

    fun obtenerCategorias(): List<Categoria> {
        Log.d("DB", "obtenerCategorias()")

        val categorias = mutableListOf<Categoria>()

        return try {
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT * FROM $TABLE_CATEGORIA ORDER BY id_categoria", null)

            cursor.use {
                while (it.moveToNext()) {
                    val categoria = Categoria(
                        id_categoria = it.getInt(it.getColumnIndexOrThrow("id_categoria")),
                        nombre = it.getString(it.getColumnIndexOrThrow("nombre")),
                        descripcion = it.getString(it.getColumnIndexOrThrow("descripcion"))
                    )
                    categorias.add(categoria)
                }
            }

            Log.d("DB", "Total categorías obtenidas: ${categorias.size}")
            categorias
        } catch (e: Exception) {
            Log.e("DB", "Error obteniendo categorías: ${e.message}", e)
            categorias
        }
    }
}

// ========== MODELOS DE DATOS ==========

data class Usuario(
    val id_usuario: Int = 0,
    val nombre: String,
    val email: String,
    val contrasena: String,
    val fecha_registro: String
)

data class Categoria(
    val id_categoria: Int = 0,
    val nombre: String,
    val descripcion: String
)

data class Local(
    val id_local: Int = 0,
    val nombre: String,
    val descripcion: String? = null,
    val direccion: String? = null,
    val capacidad: Int = 0,
    val precio_base: Double = 0.0,
    val id_propietario: Int = 0,
    val id_categoria: Int = 0
)