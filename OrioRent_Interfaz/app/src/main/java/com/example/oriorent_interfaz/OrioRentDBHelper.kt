package com.example.oriorent_interfaz

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class OrioRentDBHelper(context: Context) :
    SQLiteOpenHelper(context, "orioRent.db", null, 3) { // Versión subida a 3

    companion object {
        const val TABLE_USUARIO = "USUARIO"
        const val TABLE_CATEGORIA = "CATEGORIA"
        const val TABLE_LOCAL = "LOCAL"
        const val TABLE_RESERVA = "RESERVA"
        const val TABLE_METODOPAGO = "METODOPAGO"
        const val TABLE_SERVICIO = "SERVICIO"
        const val TABLE_FAVORITO = "FAVORITO"
        const val TABLE_VALORACION = "VALORACION"
        const val TABLE_IMAGENLOCAL = "IMAGENLOCAL"
        const val TABLE_HORARIOLOCAL = "HORARIOLOCAL"

        private const val CREATE_TABLE_USUARIO = """
        CREATE TABLE USUARIO (
            id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT,
            email TEXT UNIQUE,
            contrasena TEXT,
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
            tipo_precio TEXT, -- Nuevo campo
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

        private const val CREATE_TABLE_SERVICIO = """
        CREATE TABLE SERVICIO (
            id_servicio INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL,
            descripcion TEXT,
            precio REAL,
            id_local INTEGER,
            fecha TEXT,
            FOREIGN KEY (id_local) REFERENCES LOCAL(id_local)
        )
        """

        private const val CREATE_TABLE_FAVORITO = """
        CREATE TABLE FAVORITO (
            id_fav INTEGER PRIMARY KEY AUTOINCREMENT,
            id_usuario INTEGER,
            id_local INTEGER,
            FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario),
            FOREIGN KEY (id_local) REFERENCES LOCAL(id_local)
        )
        """

        private const val CREATE_TABLE_VALORACION = """
        CREATE TABLE VALORACION (
            id_valoracion INTEGER PRIMARY KEY AUTOINCREMENT,
            puntuacion INTEGER NOT NULL,
            comentario TEXT NOT NULL,
            fecha TEXT NOT NULL,
            id_usuario INTEGER,
            id_local INTEGER,
            FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario),
            FOREIGN KEY (id_local) REFERENCES LOCAL(id_local)
        )
        """

        private const val CREATE_TABLE_IMAGENLOCAL = """
        CREATE TABLE IMAGENLOCAL (
            id_imagen INTEGER PRIMARY KEY AUTOINCREMENT,
            url_imagen TEXT NOT NULL,
            descripcion TEXT,
            id_local INTEGER,
            FOREIGN KEY (id_local) REFERENCES LOCAL(id_local)
        )
        """

        private const val CREATE_TABLE_HORARIOLOCAL = """
        CREATE TABLE HORARIOLOCAL (
            id_horario INTEGER PRIMARY KEY AUTOINCREMENT,
            dia_semana TEXT NOT NULL,
            hora_apertura TEXT NOT NULL,
            hora_cierre TEXT NOT NULL,
            id_local INTEGER,
            FOREIGN KEY (id_local) REFERENCES LOCAL(id_local)
        )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_USUARIO)
        db.execSQL(CREATE_TABLE_CATEGORIA)
        db.execSQL(CREATE_TABLE_LOCAL)
        db.execSQL(CREATE_TABLE_RESERVA)
        db.execSQL(CREATE_TABLE_METODOPAGO)
        db.execSQL(CREATE_TABLE_SERVICIO)
        db.execSQL(CREATE_TABLE_FAVORITO)
        db.execSQL(CREATE_TABLE_VALORACION)
        db.execSQL(CREATE_TABLE_IMAGENLOCAL)
        db.execSQL(CREATE_TABLE_HORARIOLOCAL)

        insertarDatosIniciales(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val tablas = listOf(
            TABLE_HORARIOLOCAL, TABLE_IMAGENLOCAL, TABLE_VALORACION,
            TABLE_FAVORITO, TABLE_SERVICIO, TABLE_METODOPAGO,
            TABLE_RESERVA, TABLE_LOCAL, TABLE_CATEGORIA, TABLE_USUARIO
        )

        tablas.forEach { db.execSQL("DROP TABLE IF EXISTS $it") }
        onCreate(db)
    }

    private fun insertarDatosIniciales(db: SQLiteDatabase) {
        val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val admin = ContentValues().apply {
            put("nombre", "Administrador")
            put("email", "admin@oriorent.com")
            put("contrasena", "admin123")
            put("fecha_registro", fecha)
        }
        db.insert(TABLE_USUARIO, null, admin)

        val categorias = listOf(
            1 to ("Salón de eventos" to "Espacios para celebraciones"),
            2 to ("Oficina" to "Espacios de trabajo"),
            3 to ("Almacén" to "Espacios de almacenamiento"),
            4 to ("Local comercial" to "Para negocios"),
            5 to ("Estudio" to "Para artistas")
        )

        categorias.forEach { (id, par) ->
            val values = ContentValues().apply {
                put("id_categoria", id)
                put("nombre", par.first)
                put("descripcion", par.second)
            }
            db.insert(TABLE_CATEGORIA, null, values)
        }
    }

    fun insertarUsuario(nombre: String, email: String, contrasena: String): Long {
        return try {
            val db = writableDatabase
            val fecha =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val values = ContentValues().apply {
                put("nombre", nombre)
                put("email", email.lowercase().trim())
                put("contrasena", contrasena)
                put("fecha_registro", fecha)
            }

            db.insert(TABLE_USUARIO, null, values)
        } catch (e: Exception) {
            -1L
        }
    }

    fun verificarLogin(email: String, contrasena: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USUARIO WHERE email=? AND contrasena=?",
            arrayOf(email.lowercase().trim(), contrasena)
        )

        val existe = cursor.count > 0
        cursor.close()
        return existe
    }
    fun obtenerUsuarioPorEmail(email: String): Usuario? {
        return try {
            val db = readableDatabase
            val emailNormalizado = email.lowercase().trim()

            val cursor = db.rawQuery(
                "SELECT * FROM $TABLE_USUARIO WHERE email = ?",
                arrayOf(emailNormalizado)
            )

            var usuario: Usuario? = null

            if (cursor.moveToFirst()) {
                usuario = Usuario(
                    id_usuario = cursor.getInt(0),
                    nombre = cursor.getString(1),
                    email = cursor.getString(2),
                    contrasena = cursor.getString(3),
                    fecha_registro = cursor.getString(4)
                )
                Log.d("DB", "Usuario encontrado: ${usuario.email}")
            } else {
                Log.d("DB", "Usuario NO encontrado con email: $emailNormalizado")
            }

            cursor.close()
            usuario

        } catch (e: Exception) {
            Log.e("DB", "Error obteniendo usuario por email: ${e.message}", e)
            null
        }
    }


    fun obtenerTodosUsuarios(): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        val db = readableDatabase
        val c = db.rawQuery("SELECT * FROM $TABLE_USUARIO", null)

        while (c.moveToNext()) {
            lista.add(
                Usuario(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4)
                )
            )
        }
        c.close()
        return lista
    }

    fun obtenerCategorias(): List<Categoria> {
        val lista = mutableListOf<Categoria>()
        val db = readableDatabase
        val c = db.rawQuery("SELECT * FROM $TABLE_CATEGORIA", null)

        while (c.moveToNext()) {
            lista.add(
                Categoria(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2)
                )
            )
        }
        c.close()
        return lista
    }

    fun insertarLocal(
        nombre: String,
        descripcion: String,
        direccion: String,
        capacidad: Int,
        precioBase: Double,
        tipoPrecio: String, // Nuevo campo
        idPropietario: Int,
        idCategoria: Int
    ): Long {
        return try {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("nombre", nombre)
                put("descripcion", descripcion)
                put("direccion", direccion)
                put("capacidad", capacidad)
                put("precio_base", precioBase)
                put("tipo_precio", tipoPrecio)
                put("id_propietario", idPropietario)
                put("id_categoria", idCategoria)
            }
            db.insert(TABLE_LOCAL, null, values)
        } catch (e: Exception) {
            Log.e("DB", "Error insertando local: ${e.message}")
            -1L
        }
    }

    fun obtenerLocales(): List<Local> {
        val lista = mutableListOf<Local>()
        val db = readableDatabase
        val c = db.rawQuery("SELECT * FROM $TABLE_LOCAL", null)

        while (c.moveToNext()) {
            lista.add(
                Local(
                    id_local = c.getInt(0),
                    nombre = c.getString(1),
                    descripcion = c.getString(2),
                    direccion = c.getString(3),
                    capacidad = c.getInt(4),
                    precio_base = c.getDouble(5),
                    tipo_precio = c.getString(6), // Nuevo campo
                    id_propietario = c.getInt(7),
                    id_categoria = c.getInt(8)
                )
            )
        }
        c.close()
        return lista
    }

    fun obtenerLocalPorId(idLocal: Int): Local? {
        val db = readableDatabase
        val c = db.rawQuery("SELECT * FROM $TABLE_LOCAL WHERE id_local = ?", arrayOf(idLocal.toString()))
        var local: Local? = null
        if (c.moveToFirst()) {
            local = Local(
                id_local = c.getInt(0),
                nombre = c.getString(1),
                descripcion = c.getString(2),
                direccion = c.getString(3),
                capacidad = c.getInt(4),
                precio_base = c.getDouble(5),
                tipo_precio = c.getString(6), // Nuevo campo
                id_propietario = c.getInt(7),
                id_categoria = c.getInt(8)
            )
        }
        c.close()
        return local
    }

    fun insertarReserva(idUsuario: Int, idLocal: Int, fechaInicio: String, fechaFin: String, precioTotal: Double): Long {
        return try {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("fecha_inicio", fechaInicio)
                put("fecha_fin", fechaFin)
                put("estado", "Confirmada")
                put("precio_total", precioTotal)
                put("id_usuario", idUsuario)
                put("id_local", idLocal)
            }
            db.insert(TABLE_RESERVA, null, values)
        } catch (e: Exception) {
            Log.e("DB", "Error insertando reserva: ${e.message}")
            -1L
        }
    }
}

// ===== MODELOS =====
data class Usuario(
    val id_usuario: Int,
    val nombre: String,
    val email: String,
    val contrasena: String,
    val fecha_registro: String
)

data class Categoria(
    val id_categoria: Int,
    val nombre: String,
    val descripcion: String
)

data class Local(
    val id_local: Int,
    val nombre: String,
    val descripcion: String,
    val direccion: String,
    val capacidad: Int,
    val precio_base: Double,
    val tipo_precio: String, // Nuevo campo
    val id_propietario: Int,
    val id_categoria: Int
)
