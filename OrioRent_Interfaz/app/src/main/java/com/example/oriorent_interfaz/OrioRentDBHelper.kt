package com.example.oriorent_interfaz

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import java.text.SimpleDateFormat
import java.util.*

class OrioRentDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "orioRent.db"
        private const val DATABASE_VERSION = 1

        // Tablas
        const val TABLE_USUARIO = "USUARIO"
        const val TABLE_LOCAL = "LOCAL"
        const val TABLE_CATEGORIA = "CATEGORIA"
        const val TABLE_RESERVA = "RESERVA"
        const val TABLE_METODOPAGO = "METODOPAGO"
        const val TABLE_SERVICIO = "SERVICIO"
        const val TABLE_FAVORITO = "FAVORITO"
        const val TABLE_VALORACION = "VALORACION"
        const val TABLE_IMAGENLOCAL = "IMAGENLOCAL"
        const val TABLE_HORARIOLOCAL = "HORARIOLOCAL"

        // Sentencias CREATE TABLE adaptadas a SQLite
        private const val CREATE_TABLE_USUARIO = """
            CREATE TABLE USUARIO (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                email TEXT UNIQUE,
                contrasena TEXT,
                fecha_registro TEXT
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

        // ... (crea las demás tablas con la misma lógica)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear todas las tablas en orden correcto (primero las independientes)
        db.execSQL(CREATE_TABLE_USUARIO)
        db.execSQL(CREATE_TABLE_CATEGORIA)
        db.execSQL(CREATE_TABLE_LOCAL)
        db.execSQL(CREATE_TABLE_RESERVA)
        db.execSQL(CREATE_TABLE_METODOPAGO)
//        db.execSQL(CREATE_TABLE_SERVICIO)
//        db.execSQL(CREATE_TABLE_FAVORITO)
//        db.execSQL(CREATE_TABLE_VALORACION)
//        db.execSQL(CREATE_TABLE_IMAGENLOCAL)
//        db.execSQL(CREATE_TABLE_HORARIOLOCAL)

        // Insertar datos de prueba (opcional)
        insertarDatosIniciales(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Eliminar tablas en orden inverso (primero las dependientes)
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HORARIOLOCAL")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_IMAGENLOCAL")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_VALORACION")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SERVICIO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_METODOPAGO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RESERVA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LOCAL")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIO")

        onCreate(db)
    }

    private fun insertarDatosIniciales(db: SQLiteDatabase) {
        // Insertar categorías
        val categorias = listOf(
            "Salón de eventos" to "Espacios para celebraciones",
            "Oficina" to "Espacios de trabajo",
            "Almacén" to "Espacios de almacenamiento",
            "Local comercial" to "Para negocios",
            "Estudio" to "Para artistas y creadores"
        )

        categorias.forEachIndexed { index, (nombre, descripcion) ->
            val values = ContentValues().apply {
                put("id_categoria", index + 1)
                put("nombre", nombre)
                put("descripcion", descripcion)
            }
            db.insert(TABLE_CATEGORIA, null, values)
        }

        // Insertar usuario admin
        val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val usuarioAdmin = ContentValues().apply {
            put("nombre", "Administrador")
            put("email", "admin@oriorient.com")
            put("contrasena", "admin123")
            put("fecha_registro", fechaActual)
        }
        db.insert(TABLE_USUARIO, null, usuarioAdmin)
    }

    // ========== MÉTODOS CRUD PARA USUARIO ==========

    fun insertarUsuario(nombre: String, email: String, contrasena: String): Long {
        val db = writableDatabase
        val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val values = ContentValues().apply {
            put("nombre", nombre)
            put("email", email)
            put("contrasena", contrasena)
            put("fecha_registro", fechaActual)
        }

        return db.insert(TABLE_USUARIO, null, values)
    }

    fun verificarLogin(email: String, contrasena: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USUARIO WHERE email = ? AND contrasena = ?"
        val cursor = db.rawQuery(query, arrayOf(email, contrasena))
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    fun obtenerUsuarioPorEmail(email: String): Usuario? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USUARIO WHERE email = ?"
        val cursor = db.rawQuery(query, arrayOf(email))

        return cursor.use {
            if (it.moveToFirst()) {
                Usuario(
                    id_usuario = it.getInt(it.getColumnIndexOrThrow("id_usuario")),
                    nombre = it.getString(it.getColumnIndexOrThrow("nombre")),
                    email = it.getString(it.getColumnIndexOrThrow("email")),
                    contrasena = it.getString(it.getColumnIndexOrThrow("contrasena")),
                    fecha_registro = it.getString(it.getColumnIndexOrThrow("fecha_registro"))
                )
            } else {
                null
            }
        }
    }

    fun obtenerTodosUsuarios(): List<Usuario> {
        val usuarios = mutableListOf<Usuario>()
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
        return usuarios
    }

    // ========== MÉTODOS PARA OTRAS TABLAS ==========

    fun obtenerCategorias(): List<Categoria> {
        val categorias = mutableListOf<Categoria>()
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
        return categorias
    }

    // ... agregar métodos para las demás tablas según necesites
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

// ... crear las demás clases de modelo