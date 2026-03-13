package com.example.oriorent_interfaz

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class OrioRentDBHelper(context: Context) :
    SQLiteOpenHelper(context, "orioRent.db", null, 4) {

    companion object {
        const val TABLE_USUARIO      = "USUARIO"
        const val TABLE_CATEGORIA    = "CATEGORIA"
        const val TABLE_LOCAL        = "LOCAL"
        const val TABLE_RESERVA      = "RESERVA"
        const val TABLE_METODOPAGO   = "METODOPAGO"
        const val TABLE_SERVICIO     = "SERVICIO"
        const val TABLE_FAVORITO     = "FAVORITO"
        const val TABLE_VALORACION   = "VALORACION"
        const val TABLE_IMAGENLOCAL  = "IMAGENLOCAL"
        const val TABLE_HORARIOLOCAL = "HORARIOLOCAL"
        const val TABLE_CONVERSACION = "CONVERSACION"
        const val TABLE_MENSAJE      = "MENSAJE"

        private const val CREATE_TABLE_USUARIO = """
        CREATE TABLE USUARIO (
            id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT,
            email TEXT UNIQUE,
            contrasena TEXT,
            fecha_registro TEXT NOT NULL
        )"""

        private const val CREATE_TABLE_CATEGORIA = """
        CREATE TABLE CATEGORIA (
            id_categoria INTEGER PRIMARY KEY,
            nombre TEXT NOT NULL,
            descripcion TEXT
        )"""

        private const val CREATE_TABLE_LOCAL = """
        CREATE TABLE LOCAL (
            id_local INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL,
            descripcion TEXT,
            direccion TEXT,
            capacidad INTEGER,
            precio_base REAL,
            tipo_precio TEXT,
            id_propietario INTEGER,
            id_categoria INTEGER,
            FOREIGN KEY (id_propietario) REFERENCES USUARIO(id_usuario),
            FOREIGN KEY (id_categoria)   REFERENCES CATEGORIA(id_categoria)
        )"""

        private const val CREATE_TABLE_RESERVA = """
        CREATE TABLE RESERVA (
            id_reserva  INTEGER PRIMARY KEY AUTOINCREMENT,
            fecha_inicio TEXT NOT NULL,
            fecha_fin    TEXT NOT NULL,
            estado       TEXT NOT NULL,
            precio_total REAL NOT NULL,
            id_usuario   INTEGER,
            id_local     INTEGER,
            FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario),
            FOREIGN KEY (id_local)   REFERENCES LOCAL(id_local)
        )"""

        private const val CREATE_TABLE_METODOPAGO = """
        CREATE TABLE METODOPAGO (
            id_pago     INTEGER PRIMARY KEY AUTOINCREMENT,
            metodo_pago TEXT,
            hora_fecha  TEXT,
            importe     REAL,
            estado      TEXT,
            id_usuario  INTEGER,
            id_reserva  INTEGER,
            FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario),
            FOREIGN KEY (id_reserva) REFERENCES RESERVA(id_reserva)
        )"""

        private const val CREATE_TABLE_SERVICIO = """
        CREATE TABLE SERVICIO (
            id_servicio INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre      TEXT NOT NULL,
            descripcion TEXT,
            precio      REAL,
            id_local    INTEGER,
            fecha       TEXT,
            FOREIGN KEY (id_local) REFERENCES LOCAL(id_local)
        )"""

        private const val CREATE_TABLE_FAVORITO = """
        CREATE TABLE FAVORITO (
            id_fav     INTEGER PRIMARY KEY AUTOINCREMENT,
            id_usuario INTEGER,
            id_local   INTEGER,
            FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario),
            FOREIGN KEY (id_local)   REFERENCES LOCAL(id_local)
        )"""

        private const val CREATE_TABLE_VALORACION = """
        CREATE TABLE VALORACION (
            id_valoracion INTEGER PRIMARY KEY AUTOINCREMENT,
            puntuacion    INTEGER NOT NULL,
            comentario    TEXT NOT NULL,
            fecha         TEXT NOT NULL,
            id_usuario    INTEGER,
            id_local      INTEGER,
            FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario),
            FOREIGN KEY (id_local)   REFERENCES LOCAL(id_local)
        )"""

        private const val CREATE_TABLE_IMAGENLOCAL = """
        CREATE TABLE IMAGENLOCAL (
            id_imagen   INTEGER PRIMARY KEY AUTOINCREMENT,
            url_imagen  TEXT NOT NULL,
            descripcion TEXT,
            id_local    INTEGER,
            FOREIGN KEY (id_local) REFERENCES LOCAL(id_local)
        )"""

        private const val CREATE_TABLE_HORARIOLOCAL = """
        CREATE TABLE HORARIOLOCAL (
            id_horario    INTEGER PRIMARY KEY AUTOINCREMENT,
            dia_semana    TEXT NOT NULL,
            hora_apertura TEXT NOT NULL,
            hora_cierre   TEXT NOT NULL,
            id_local      INTEGER,
            FOREIGN KEY (id_local) REFERENCES LOCAL(id_local)
        )"""

        private const val CREATE_TABLE_CONVERSACION = """
        CREATE TABLE CONVERSACION (
            id_conversacion      INTEGER PRIMARY KEY AUTOINCREMENT,
            id_usuario1          INTEGER NOT NULL,
            id_usuario2          INTEGER NOT NULL,
            id_local             INTEGER NOT NULL,
            ultimo_mensaje       TEXT DEFAULT '',
            ultima_fecha         TEXT DEFAULT '',
            FOREIGN KEY (id_usuario1) REFERENCES USUARIO(id_usuario),
            FOREIGN KEY (id_usuario2) REFERENCES USUARIO(id_usuario),
            FOREIGN KEY (id_local)    REFERENCES LOCAL(id_local)
        )"""

        private const val CREATE_TABLE_MENSAJE = """
        CREATE TABLE MENSAJE (
            id_mensaje       INTEGER PRIMARY KEY AUTOINCREMENT,
            id_conversacion  INTEGER NOT NULL,
            id_remitente     INTEGER NOT NULL,
            contenido        TEXT NOT NULL,
            fecha_hora       TEXT NOT NULL,
            leido            INTEGER DEFAULT 0,
            FOREIGN KEY (id_conversacion) REFERENCES CONVERSACION(id_conversacion),
            FOREIGN KEY (id_remitente)    REFERENCES USUARIO(id_usuario)
        )"""
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
        db.execSQL(CREATE_TABLE_CONVERSACION)
        db.execSQL(CREATE_TABLE_MENSAJE)
        insertarDatosIniciales(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        listOf(TABLE_MENSAJE, TABLE_CONVERSACION,
            TABLE_HORARIOLOCAL, TABLE_IMAGENLOCAL, TABLE_VALORACION,
            TABLE_FAVORITO, TABLE_SERVICIO, TABLE_METODOPAGO,
            TABLE_RESERVA, TABLE_LOCAL, TABLE_CATEGORIA, TABLE_USUARIO)
            .forEach { db.execSQL("DROP TABLE IF EXISTS $it") }
        onCreate(db)
    }

    private fun insertarDatosIniciales(db: SQLiteDatabase) {
        val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        db.insert(TABLE_USUARIO, null, ContentValues().apply {
            put("nombre", "Administrador"); put("email", "admin@oriorent.com")
            put("contrasena", "admin123");  put("fecha_registro", fecha)
        })
        listOf(1 to ("Salón de eventos" to "Espacios para celebraciones"),
            2 to ("Oficina"          to "Espacios de trabajo"),
            3 to ("Almacén"          to "Espacios de almacenamiento"),
            4 to ("Local comercial"  to "Para negocios"),
            5 to ("Estudio"          to "Para artistas"))
            .forEach { (id, par) ->
                db.insert(TABLE_CATEGORIA, null, ContentValues().apply {
                    put("id_categoria", id); put("nombre", par.first); put("descripcion", par.second)
                })
            }
    }

    // ═══════════════════════════════════════════════════════ USUARIOS ════

    fun insertarUsuario(nombre: String, email: String, contrasena: String): Long {
        return try {
            val values = ContentValues().apply {
                put("nombre", nombre)
                put("email", email.lowercase().trim())
                put("contrasena", contrasena)
                put("fecha_registro", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
            }
            writableDatabase.insert(TABLE_USUARIO, null, values)
        } catch (e: Exception) { -1L }
    }

    fun verificarLogin(email: String, contrasena: String): Boolean {
        val c = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_USUARIO WHERE email=? AND contrasena=?",
            arrayOf(email.lowercase().trim(), contrasena)
        )
        return c.use { it.count > 0 }
    }

    fun obtenerUsuarioPorEmail(email: String): Usuario? {
        return try {
            val c = readableDatabase.rawQuery(
                "SELECT * FROM $TABLE_USUARIO WHERE email = ?",
                arrayOf(email.lowercase().trim())
            )
            c.use {
                if (it.moveToFirst()) Usuario(it.getInt(0), it.getString(1), it.getString(2), it.getString(3), it.getString(4))
                else null
            }
        } catch (_: Exception) { null }
    }

    /** NUEVO: busca usuario por id (para mostrar el propietario de un local) */
    fun obtenerUsuarioPorId(id: Int): Usuario? {
        return try {
            val c = readableDatabase.rawQuery(
                "SELECT * FROM $TABLE_USUARIO WHERE id_usuario = ?",
                arrayOf(id.toString())
            )
            c.use {
                if (it.moveToFirst()) Usuario(it.getInt(0), it.getString(1), it.getString(2), it.getString(3), it.getString(4))
                else null
            }
        } catch (_: Exception) { null }
    }

    // ════════════════════════════════════════════════════ CATEGORÍAS ════

    fun obtenerCategorias(): List<Categoria> {
        val lista = mutableListOf<Categoria>()
        readableDatabase.rawQuery("SELECT * FROM $TABLE_CATEGORIA", null).use { c ->
            while (c.moveToNext()) lista.add(Categoria(c.getInt(0), c.getString(1), c.getString(2)))
        }
        return lista
    }

    // ═══════════════════════════════════════════════════════ LOCALES ════

    fun insertarLocal(nombre: String, descripcion: String, direccion: String, capacidad: Int,
                      precioBase: Double, tipoPrecio: String, idPropietario: Int, idCategoria: Int): Long {
        return try {
            writableDatabase.insert(TABLE_LOCAL, null, ContentValues().apply {
                put("nombre", nombre); put("descripcion", descripcion); put("direccion", direccion)
                put("capacidad", capacidad); put("precio_base", precioBase); put("tipo_precio", tipoPrecio)
                put("id_propietario", idPropietario); put("id_categoria", idCategoria)
            })
        } catch (e: Exception) { Log.e("DB", "insertarLocal: ${e.message}"); -1L }
    }

    fun obtenerLocales(): List<Local> {
        val lista = mutableListOf<Local>()
        readableDatabase.rawQuery("SELECT * FROM $TABLE_LOCAL", null).use { c ->
            while (c.moveToNext()) lista.add(
                Local(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                    c.getInt(4), c.getDouble(5), c.getString(6), c.getInt(7), c.getInt(8))
            )
        }
        return lista
    }

    fun obtenerLocalPorId(idLocal: Int): Local? {
        readableDatabase.rawQuery("SELECT * FROM $TABLE_LOCAL WHERE id_local = ?", arrayOf(idLocal.toString())).use { c ->
            if (c.moveToFirst()) return Local(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                c.getInt(4), c.getDouble(5), c.getString(6), c.getInt(7), c.getInt(8))
        }
        return null
    }

    // ══════════════════════════════════════════════════════ IMÁGENES ════

    /** NUEVO: guarda la URI de una imagen asociada a un local */
    fun insertarImagenLocal(idLocal: Int, urlImagen: String): Long {
        return try {
            writableDatabase.insert(TABLE_IMAGENLOCAL, null, ContentValues().apply {
                put("url_imagen", urlImagen)
                put("id_local", idLocal)
            })
        } catch (e: Exception) { Log.e("DB", "insertarImagenLocal: ${e.message}"); -1L }
    }

    /** Devuelve la lista de URIs de imágenes de un local */
    fun obtenerImagenesLocal(idLocal: Int): List<String> {
        val lista = mutableListOf<String>()
        readableDatabase.rawQuery(
            "SELECT url_imagen FROM $TABLE_IMAGENLOCAL WHERE id_local = ?",
            arrayOf(idLocal.toString())
        ).use { c ->
            while (c.moveToNext()) lista.add(c.getString(0))
        }
        return lista
    }

    // ═══════════════════════════════════════════════════════ RESERVAS ════

    /**
     * Comprueba si un local está disponible en el rango dado.
     * Detecta solapamiento ignorando reservas canceladas.
     * Funciona tanto para "yyyy-MM-dd" como para "yyyy-MM-dd HH:mm".
     */
    fun verificarDisponibilidad(idLocal: Int, fechaInicio: String, fechaFin: String): Boolean {
        return try {
            readableDatabase.rawQuery(
                """SELECT COUNT(*) FROM $TABLE_RESERVA
                   WHERE id_local = ?
                   AND estado != 'Cancelada'
                   AND fecha_inicio < ?
                   AND fecha_fin    > ?""",
                arrayOf(idLocal.toString(), fechaFin, fechaInicio)
            ).use { c ->
                c.moveToFirst()
                c.getInt(0) == 0
            }
        } catch (e: Exception) {
            Log.e("DB", "verificarDisponibilidad: ${e.message}"); false
        }
    }

    fun insertarReserva(idUsuario: Int, idLocal: Int, fechaInicio: String, fechaFin: String, precioTotal: Double): Long {
        return try {
            writableDatabase.insert(TABLE_RESERVA, null, ContentValues().apply {
                put("fecha_inicio", fechaInicio); put("fecha_fin", fechaFin)
                put("estado", "Confirmada");      put("precio_total", precioTotal)
                put("id_usuario", idUsuario);     put("id_local", idLocal)
            })
        } catch (e: Exception) { Log.e("DB", "insertarReserva: ${e.message}"); -1L }
    }

    fun obtenerReservasUsuario(idUsuario: Int): List<Reserva> {
        val lista = mutableListOf<Reserva>()
        readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_RESERVA WHERE id_usuario = ? ORDER BY fecha_inicio DESC",
            arrayOf(idUsuario.toString())
        ).use { c ->
            while (c.moveToNext()) lista.add(
                Reserva(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                    c.getDouble(4), c.getInt(5), c.getInt(6))
            )
        }
        return lista
    }

    /** Cancela una reserva cambiando su estado (no la borra, queda en historial) */
    fun cancelarReserva(idReserva: Int): Boolean {
        return try {
            val filas = writableDatabase.update(
                TABLE_RESERVA,
                ContentValues().apply { put("estado", "Cancelada") },
                "id_reserva = ?",
                arrayOf(idReserva.toString())
            )
            filas > 0
        } catch (e: Exception) { Log.e("DB", "cancelarReserva: ${e.message}"); false }
    }

    // ══════════════════════════════════════════════════════ FAVORITOS ════

    fun toggleFavorito(idUsuario: Int, idLocal: Int): Boolean {
        val db = writableDatabase
        val existe = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_FAVORITO WHERE id_usuario = ? AND id_local = ?",
            arrayOf(idUsuario.toString(), idLocal.toString())
        ).use { it.count > 0 }

        if (existe) db.delete(TABLE_FAVORITO, "id_usuario = ? AND id_local = ?",
            arrayOf(idUsuario.toString(), idLocal.toString()))
        else db.insert(TABLE_FAVORITO, null, ContentValues().apply {
            put("id_usuario", idUsuario); put("id_local", idLocal)
        })
        return !existe
    }

    fun esFavorito(idUsuario: Int, idLocal: Int): Boolean =
        readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_FAVORITO WHERE id_usuario = ? AND id_local = ?",
            arrayOf(idUsuario.toString(), idLocal.toString())
        ).use { it.count > 0 }

    // ═══════════════════════════════════════════════ MENSAJERÍA ════

    /**
     * Obtiene o crea una conversación entre dos usuarios sobre un local.
     * Evita duplicados: si ya existe devuelve la existente.
     */
    fun obtenerOCrearConversacion(idUsuario1: Int, idUsuario2: Int, idLocal: Int): Int {
        val db = writableDatabase
        // Buscar conversación existente (en cualquier orden de usuarios)
        val cursor = db.rawQuery(
            """SELECT id_conversacion FROM $TABLE_CONVERSACION
               WHERE id_local = ?
               AND ((id_usuario1 = ? AND id_usuario2 = ?)
                 OR (id_usuario1 = ? AND id_usuario2 = ?))""",
            arrayOf(idLocal.toString(),
                idUsuario1.toString(), idUsuario2.toString(),
                idUsuario2.toString(), idUsuario1.toString())
        )
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(0)
            cursor.close()
            return id
        }
        cursor.close()
        // Crear nueva conversación
        val values = ContentValues().apply {
            put("id_usuario1", idUsuario1)
            put("id_usuario2", idUsuario2)
            put("id_local", idLocal)
            put("ultimo_mensaje", "")
            put("ultima_fecha", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
        }
        return db.insert(TABLE_CONVERSACION, null, values).toInt()
    }

    fun insertarMensaje(idConversacion: Int, idRemitente: Int, contenido: String, fechaHora: String): Long {
        return try {
            val db = writableDatabase
            // Insertar mensaje
            val id = db.insert(TABLE_MENSAJE, null, ContentValues().apply {
                put("id_conversacion", idConversacion)
                put("id_remitente", idRemitente)
                put("contenido", contenido)
                put("fecha_hora", fechaHora)
                put("leido", 0)
            })
            // Actualizar último mensaje en la conversación
            db.update(TABLE_CONVERSACION, ContentValues().apply {
                put("ultimo_mensaje", contenido)
                put("ultima_fecha", fechaHora)
            }, "id_conversacion = ?", arrayOf(idConversacion.toString()))
            id
        } catch (e: Exception) {
            Log.e("DB", "insertarMensaje: ${e.message}"); -1L
        }
    }

    fun obtenerMensajesConversacion(idConversacion: Int): List<Mensaje> {
        val lista = mutableListOf<Mensaje>()
        readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_MENSAJE WHERE id_conversacion = ? ORDER BY fecha_hora ASC",
            arrayOf(idConversacion.toString())
        ).use { c ->
            while (c.moveToNext()) {
                lista.add(Mensaje(
                    id_mensaje       = c.getInt(0),
                    id_conversacion  = c.getInt(1),
                    id_remitente     = c.getInt(2),
                    contenido        = c.getString(3),
                    fecha_hora       = c.getString(4),
                    leido            = c.getInt(5) == 1
                ))
            }
        }
        return lista
    }

    fun obtenerConversacionesUsuario(idUsuario: Int): List<Conversacion> {
        val lista = mutableListOf<Conversacion>()
        readableDatabase.rawQuery(
            """SELECT c.*,
                      (SELECT COUNT(*) FROM $TABLE_MENSAJE m
                       WHERE m.id_conversacion = c.id_conversacion
                       AND m.id_remitente != ? AND m.leido = 0) AS no_leidos
               FROM $TABLE_CONVERSACION c
               WHERE c.id_usuario1 = ? OR c.id_usuario2 = ?
               ORDER BY c.ultima_fecha DESC""",
            arrayOf(idUsuario.toString(), idUsuario.toString(), idUsuario.toString())
        ).use { c ->
            while (c.moveToNext()) {
                lista.add(Conversacion(
                    id_conversacion     = c.getInt(0),
                    id_usuario1         = c.getInt(1),
                    id_usuario2         = c.getInt(2),
                    id_local            = c.getInt(3),
                    ultimo_mensaje      = c.getString(4),
                    ultima_fecha        = c.getString(5),
                    mensajes_no_leidos  = c.getInt(6)
                ))
            }
        }
        return lista
    }

    fun obtenerConversacionPorId(idConversacion: Int): Conversacion? {
        readableDatabase.rawQuery(
            "SELECT *, 0 as no_leidos FROM $TABLE_CONVERSACION WHERE id_conversacion = ?",
            arrayOf(idConversacion.toString())
        ).use { c ->
            if (c.moveToFirst()) return Conversacion(
                id_conversacion    = c.getInt(0),
                id_usuario1        = c.getInt(1),
                id_usuario2        = c.getInt(2),
                id_local           = c.getInt(3),
                ultimo_mensaje     = c.getString(4),
                ultima_fecha       = c.getString(5),
                mensajes_no_leidos = 0
            )
        }
        return null
    }

    fun marcarMensajesLeidos(idConversacion: Int, idUsuarioLector: Int) {
        try {
            writableDatabase.execSQL(
                """UPDATE $TABLE_MENSAJE SET leido = 1
                   WHERE id_conversacion = ? AND id_remitente != ?""",
                arrayOf(idConversacion.toString(), idUsuarioLector.toString())
            )
        } catch (e: Exception) { Log.e("DB", "marcarLeidos: ${e.message}") }
    }

}

// ═════════════════════════════════════════════════════════ MODELOS ════

data class Usuario(
    val id_usuario: Int, val nombre: String, val email: String,
    val contrasena: String, val fecha_registro: String
)

data class Categoria(
    val id_categoria: Int, val nombre: String, val descripcion: String
)

data class Local(
    val id_local: Int, val nombre: String, val descripcion: String,
    val direccion: String, val capacidad: Int, val precio_base: Double,
    val tipo_precio: String, val id_propietario: Int, val id_categoria: Int
)

data class Reserva(
    val id_reserva: Int, val fecha_inicio: String, val fecha_fin: String,
    val estado: String, val precio_total: Double,
    val id_usuario: Int, val id_local: Int
)

data class Conversacion(
    val id_conversacion: Int,
    val id_usuario1: Int,
    val id_usuario2: Int,
    val id_local: Int,
    val ultimo_mensaje: String,
    val ultima_fecha: String,
    val mensajes_no_leidos: Int = 0
)

data class Mensaje(
    val id_mensaje: Int,
    val id_conversacion: Int,
    val id_remitente: Int,
    val contenido: String,
    val fecha_hora: String,
    val leido: Boolean
)