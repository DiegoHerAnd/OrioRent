package com.example.oriorent_interfaz

import android.util.Log
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

// ═══════════════════════════════════════════════════ MODELOS SUPABASE ════
// Estos data classes se usan para serializar/deserializar con Supabase.
// Son equivalentes a los modelos del DBHelper pero anotados con @Serializable.

@Serializable
data class UsuarioSB(
    @SerialName("id_usuario")     val id_usuario: Int = 0,
    @SerialName("nombre")         val nombre: String,
    @SerialName("email")          val email: String,
    @SerialName("contrasena")     val contrasena: String,
    @SerialName("fecha_registro") val fecha_registro: String
)

@Serializable
data class CategoriaSB(
    @SerialName("id_categoria") val id_categoria: Int,
    @SerialName("nombre")       val nombre: String,
    @SerialName("descripcion")  val descripcion: String? = null
)

@Serializable
data class LocalSB(
    @SerialName("id_local")       val id_local: Int = 0,
    @SerialName("nombre")         val nombre: String,
    @SerialName("descripcion")    val descripcion: String? = null,
    @SerialName("direccion")      val direccion: String? = null,
    @SerialName("capacidad")      val capacidad: Int = 0,
    @SerialName("precio_base")    val precio_base: Double = 0.0,
    @SerialName("tipo_precio")    val tipo_precio: String? = null,
    @SerialName("id_propietario") val id_propietario: Int = 0,
    @SerialName("id_categoria")   val id_categoria: Int = 0
)

@Serializable
data class ReservaSB(
    @SerialName("id_reserva")    val id_reserva: Int = 0,
    @SerialName("fecha_inicio")  val fecha_inicio: String,
    @SerialName("fecha_fin")     val fecha_fin: String,
    @SerialName("estado")        val estado: String,
    @SerialName("precio_total")  val precio_total: Double,
    @SerialName("id_usuario")    val id_usuario: Int,
    @SerialName("id_local")      val id_local: Int
)

@Serializable
data class FavoritoSB(
    @SerialName("id_fav")     val id_fav: Int = 0,
    @SerialName("id_usuario") val id_usuario: Int,
    @SerialName("id_local")   val id_local: Int
)

@Serializable
data class ImagenLocalSB(
    @SerialName("id_imagen")    val id_imagen: Int = 0,
    @SerialName("url_imagen")   val url_imagen: String,
    @SerialName("descripcion")  val descripcion: String? = null,
    @SerialName("id_local")     val id_local: Int
)

@Serializable
data class ConversacionSB(
    @SerialName("id_conversacion")  val id_conversacion: Int = 0,
    @SerialName("id_usuario1")      val id_usuario1: Int,
    @SerialName("id_usuario2")      val id_usuario2: Int,
    @SerialName("id_local")         val id_local: Int,
    @SerialName("ultimo_mensaje")   val ultimo_mensaje: String = "",
    @SerialName("ultima_fecha")     val ultima_fecha: String = ""
)

@Serializable
data class MensajeSB(
    @SerialName("id_mensaje")      val id_mensaje: Int = 0,
    @SerialName("id_conversacion") val id_conversacion: Int,
    @SerialName("id_remitente")    val id_remitente: Int,
    @SerialName("contenido")       val contenido: String,
    @SerialName("fecha_hora")      val fecha_hora: String,
    @SerialName("leido")           val leido: Boolean = false
)

// ════════════════════════════════════════════════════════ REPOSITORIO ════

object SupabaseRepository {

    private val db get() = SupabaseManager.client.postgrest
    private val TAG = "SupabaseRepository"

    private fun ahora() =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

    private fun hoy() =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // ══════════════════════════════════════════════════════ USUARIOS ════

    /**
     * Inserta un nuevo usuario. Devuelve el id generado, o -1 si falla.
     * Equivale a: insertarUsuario()
     */
    suspend fun insertarUsuario(nombre: String, email: String, contrasena: String): Int {
        return try {
            val result = db["usuario"].insert(
                UsuarioSB(
                    nombre         = nombre,
                    email          = email.lowercase().trim(),
                    contrasena     = contrasena,
                    fecha_registro = hoy()
                )
            ) { select() }
                .decodeSingle<UsuarioSB>()
            result.id_usuario
        } catch (e: Exception) {
            Log.e(TAG, "insertarUsuario error", e)
            -1
        }
    }

    /**
     * Comprueba email + contraseña. Devuelve true si son correctos.
     * Equivale a: verificarLogin()
     */
    suspend fun verificarLogin(email: String, contrasena: String): Boolean {
        return try {
            val result = db["usuario"]
                .select {
                    filter {
                        eq("email",     email.lowercase().trim())
                        eq("contrasena", contrasena)
                    }
                }
                .decodeList<UsuarioSB>()
            result.isNotEmpty()
        } catch (e: Exception) {
            Log.e(TAG, "verificarLogin error", e)
            false
        }
    }

    /**
     * Busca un usuario por su email. Devuelve Usuario (modelo local) o null.
     * Equivale a: obtenerUsuarioPorEmail()
     */
    suspend fun obtenerUsuarioPorEmail(email: String): Usuario? {
        return try {
            val result = db["usuario"]
                .select { filter { eq("email", email.lowercase().trim()) } }
                .decodeList<UsuarioSB>()
                .firstOrNull() ?: return null
            result.toLocal()
        } catch (e: Exception) {
            Log.e(TAG, "obtenerUsuarioPorEmail error", e)
            null
        }
    }

    /**
     * Busca un usuario por su id. Devuelve Usuario (modelo local) o null.
     * Equivale a: obtenerUsuarioPorId()
     */
    suspend fun obtenerUsuarioPorId(id: Int): Usuario? {
        return try {
            val result = db["usuario"]
                .select { filter { eq("id_usuario", id) } }
                .decodeList<UsuarioSB>()
                .firstOrNull() ?: return null
            result.toLocal()
        } catch (e: Exception) {
            Log.e(TAG, "obtenerUsuarioPorId error", e)
            null
        }
    }

    // ══════════════════════════════════════════════════ CATEGORÍAS ════

    /**
     * Devuelve todas las categorías.
     * Equivale a: obtenerCategorias()
     */
    suspend fun obtenerCategorias(): List<Categoria> {
        return try {
            val res = db["categoria"].select().decodeList<CategoriaSB>()
            Log.d(TAG, "obtenerCategorias: Éxito, encontradas ${res.size}")
            res.map { it.toLocal() }
        } catch (e: Exception) {
            Log.e(TAG, "obtenerCategorias error", e)
            emptyList()
        }
    }

    // ════════════════════════════════════════════════════ LOCALES ════

    /**
     * Inserta un nuevo local. Devuelve el id generado, o -1 si falla.
     * Equivale a: insertarLocal()
     */
    suspend fun insertarLocal(
        nombre: String, descripcion: String, direccion: String,
        capacidad: Int, precioBase: Double, tipoPrecio: String,
        idPropietario: Int, idCategoria: Int
    ): Int {
        return try {
            val result = db["local"].insert(
                LocalSB(
                    nombre         = nombre,
                    descripcion    = descripcion,
                    direccion      = direccion,
                    capacidad      = capacidad,
                    precio_base    = precioBase,
                    tipo_precio    = tipoPrecio,
                    id_propietario = idPropietario,
                    id_categoria   = idCategoria
                )
            ) { select() }
                .decodeSingle<LocalSB>()
            result.id_local
        } catch (e: Exception) {
            Log.e(TAG, "insertarLocal error", e)
            -1
        }
    }

    /**
     * Devuelve todos los locales.
     * Equivale a: obtenerLocales()
     */
    suspend fun obtenerLocales(): List<Local> {
        return try {
            db["local"].select().decodeList<LocalSB>().map { it.toLocal() }
        } catch (e: Exception) {
            Log.e(TAG, "obtenerLocales error", e)
            emptyList()
        }
    }

    /**
     * Devuelve un local por su id, o null si no existe.
     * Equivale a: obtenerLocalPorId()
     */
    suspend fun obtenerLocalPorId(idLocal: Int): Local? {
        return try {
            db["local"]
                .select { filter { eq("id_local", idLocal) } }
                .decodeList<LocalSB>()
                .firstOrNull()
                ?.toLocal()
        } catch (e: Exception) {
            Log.e(TAG, "obtenerLocalPorId error", e)
            null
        }
    }

    // ══════════════════════════════════════════════════ IMÁGENES ════

    /**
     * Guarda la URL de una imagen asociada a un local. Devuelve el id o -1 si falla.
     * Equivale a: insertarImagenLocal()
     */
    suspend fun insertarImagenLocal(idLocal: Int, urlImagen: String): Int {
        return try {
            val result = db["imagenlocal"].insert(
                ImagenLocalSB(url_imagen = urlImagen, id_local = idLocal)
            ) { select() }
                .decodeSingle<ImagenLocalSB>()
            result.id_imagen
        } catch (e: Exception) {
            Log.e(TAG, "insertarImagenLocal error", e)
            -1
        }
    }

    /**
     * Devuelve la lista de URLs de imágenes de un local.
     * Equivale a: obtenerImagenesLocal()
     */
    suspend fun obtenerImagenesLocal(idLocal: Int): List<String> {
        return try {
            db["imagenlocal"]
                .select { filter { eq("id_local", idLocal) } }
                .decodeList<ImagenLocalSB>()
                .map { it.url_imagen }
        } catch (e: Exception) {
            Log.e(TAG, "obtenerImagenesLocal error", e)
            emptyList()
        }
    }

    // ══════════════════════════════════════════════════ RESERVAS ════

    /**
     * Comprueba si un local está disponible en el rango dado.
     * Equivale a: verificarDisponibilidad()
     *
     * Nota: Supabase no soporta filtros de solapamiento directamente en el cliente,
     * así que traemos las reservas activas del local y comprobamos en memoria.
     */
    suspend fun verificarDisponibilidad(idLocal: Int, fechaInicio: String, fechaFin: String): Boolean {
        return try {
            val reservas = db["reserva"]
                .select {
                    filter {
                        eq("id_local", idLocal)
                        neq("estado", "Cancelada")
                    }
                }
                .decodeList<ReservaSB>()

            // Solapamiento: inicio < fechaFin AND fin > fechaInicio
            reservas.none { r ->
                r.fecha_inicio < fechaFin && r.fecha_fin > fechaInicio
            }
        } catch (e: Exception) {
            Log.e(TAG, "verificarDisponibilidad error", e)
            false
        }
    }

    /**
     * Inserta una reserva confirmada. Devuelve el id generado o -1 si falla.
     * Equivale a: insertarReserva()
     */
    suspend fun insertarReserva(
        idUsuario: Int, idLocal: Int,
        fechaInicio: String, fechaFin: String, precioTotal: Double
    ): Int {
        return try {
            val result = db["reserva"].insert(
                ReservaSB(
                    fecha_inicio  = fechaInicio,
                    fecha_fin     = fechaFin,
                    estado        = "Confirmada",
                    precio_total  = precioTotal,
                    id_usuario    = idUsuario,
                    id_local      = idLocal
                )
            ) { select() }
                .decodeSingle<ReservaSB>()
            result.id_reserva
        } catch (e: Exception) {
            Log.e(TAG, "insertarReserva error", e)
            -1
        }
    }

    /**
     * Devuelve las reservas de un usuario ordenadas por fecha descendente.
     * Equivale a: obtenerReservasUsuario()
     */
    suspend fun obtenerReservasUsuario(idUsuario: Int): List<Reserva> {
        return try {
            db["reserva"]
                .select {
                    filter { eq("id_usuario", idUsuario) }
                    order("fecha_inicio", Order.DESCENDING)
                }
                .decodeList<ReservaSB>()
                .map { it.toLocal() }
        } catch (e: Exception) {
            Log.e(TAG, "obtenerReservasUsuario error", e)
            emptyList()
        }
    }

    /**
     * Cancela una reserva (cambia estado a "Cancelada"). Devuelve true si tuvo éxito.
     * Equivale a: cancelarReserva()
     */
    suspend fun cancelarReserva(idReserva: Int): Boolean {
        return try {
            db["reserva"].update(
                mapOf("estado" to "Cancelada")
            ) {
                filter { eq("id_reserva", idReserva) }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "cancelarReserva error", e)
            false
        }
    }

    // ══════════════════════════════════════════════════ FAVORITOS ════

    /**
     * Añade o elimina un favorito (toggle). Devuelve true si ahora ES favorito.
     * Equivale a: toggleFavorito()
     */
    suspend fun toggleFavorito(idUsuario: Int, idLocal: Int): Boolean {
        return try {
            val existente = db["favorito"]
                .select {
                    filter {
                        eq("id_usuario", idUsuario)
                        eq("id_local", idLocal)
                    }
                }
                .decodeList<FavoritoSB>()
                .firstOrNull()

            if (existente != null) {
                db["favorito"].delete {
                    filter {
                        eq("id_usuario", idUsuario)
                        eq("id_local", idLocal)
                    }
                }
                false   // Ya no es favorito
            } else {
                db["favorito"].insert(FavoritoSB(id_usuario = idUsuario, id_local = idLocal))
                true    // Ahora sí es favorito
            }
        } catch (e: Exception) {
            Log.e(TAG, "toggleFavorito error", e)
            false
        }
    }

    /**
     * Comprueba si un local es favorito del usuario.
     * Equivale a: esFavorito()
     */
    suspend fun esFavorito(idUsuario: Int, idLocal: Int): Boolean {
        return try {
            db["favorito"]
                .select {
                    filter {
                        eq("id_usuario", idUsuario)
                        eq("id_local", idLocal)
                    }
                }
                .decodeList<FavoritoSB>()
                .isNotEmpty()
        } catch (e: Exception) {
            Log.e(TAG, "esFavorito error", e)
            false
        }
    }

    // ══════════════════════════════════════════════════ MENSAJERÍA ════

    /**
     * Obtiene o crea una conversación entre dos usuarios sobre un local.
     * Equivale a: obtenerOCrearConversacion()
     */
    suspend fun obtenerOCrearConversacion(idUsuario1: Int, idUsuario2: Int, idLocal: Int): Int {
        return try {
            // Buscar en ambos órdenes de usuarios
            val existente = db["conversacion"]
                .select {
                    filter {
                        eq("id_local", idLocal)
                        or {
                            and {
                                eq("id_usuario1", idUsuario1)
                                eq("id_usuario2", idUsuario2)
                            }
                            and {
                                eq("id_usuario1", idUsuario2)
                                eq("id_usuario2", idUsuario1)
                            }
                        }
                    }
                }
                .decodeList<ConversacionSB>()
                .firstOrNull()

            if (existente != null) return existente.id_conversacion

            // Crear nueva conversación
            val nueva = db["conversacion"].insert(
                ConversacionSB(
                    id_usuario1   = idUsuario1,
                    id_usuario2   = idUsuario2,
                    id_local      = idLocal,
                    ultimo_mensaje = "",
                    ultima_fecha  = ahora()
                )
            ) { select() }
                .decodeSingle<ConversacionSB>()
            nueva.id_conversacion
        } catch (e: Exception) {
            Log.e(TAG, "obtenerOCrearConversacion error", e)
            -1
        }
    }

    /**
     * Inserta un mensaje y actualiza el último mensaje de la conversación.
     * Equivale a: insertarMensaje()
     */
    suspend fun insertarMensaje(
        idConversacion: Int, idRemitente: Int,
        contenido: String, fechaHora: String
    ): Int {
        return try {
            val msg = db["mensaje"].insert(
                MensajeSB(
                    id_conversacion = idConversacion,
                    id_remitente    = idRemitente,
                    contenido       = contenido,
                    fecha_hora      = fechaHora,
                    leido           = false
                )
            ) { select() }
                .decodeSingle<MensajeSB>()

            // Actualizar último mensaje en la conversación
            db["conversacion"].update(
                mapOf("ultimo_mensaje" to contenido, "ultima_fecha" to fechaHora)
            ) {
                filter { eq("id_conversacion", idConversacion) }
            }

            msg.id_mensaje
        } catch (e: Exception) {
            Log.e(TAG, "insertarMensaje error", e)
            -1
        }
    }

    /**
     * Devuelve los mensajes de una conversación ordenados por fecha ascendente.
     * Equivale a: obtenerMensajesConversacion()
     */
    suspend fun obtenerMensajesConversacion(idConversacion: Int): List<Mensaje> {
        return try {
            db["mensaje"]
                .select {
                    filter { eq("id_conversacion", idConversacion) }
                    order("fecha_hora", Order.ASCENDING)
                }
                .decodeList<MensajeSB>()
                .map { it.toLocal() }
        } catch (e: Exception) {
            Log.e(TAG, "obtenerMensajesConversacion error", e)
            emptyList()
        }
    }

    /**
     * Devuelve todas las conversaciones de un usuario con conteo de no leídos.
     * Equivale a: obtenerConversacionesUsuario()
     *
     * Nota: El conteo de no leídos se calcula en memoria porque Supabase
     * no permite subconsultas de agregado directas desde el cliente Kotlin.
     */
    suspend fun obtenerConversacionesUsuario(idUsuario: Int): List<Conversacion> {
        return try {
            val conversaciones = db["conversacion"]
                .select {
                    filter {
                        or {
                            eq("id_usuario1", idUsuario)
                            eq("id_usuario2", idUsuario)
                        }
                    }
                    order("ultima_fecha", Order.DESCENDING)
                }
                .decodeList<ConversacionSB>()

            conversaciones.map { conv ->
                // Contar mensajes no leídos de otros en esta conversación
                val noLeidos = try {
                    db["mensaje"]
                        .select {
                            filter {
                                eq("id_conversacion", conv.id_conversacion)
                                eq("leido", false)
                                neq("id_remitente", idUsuario)
                            }
                        }
                        .decodeList<MensajeSB>()
                        .size
                } catch (_: Exception) { 0 }

                conv.toLocal(noLeidos)
            }
        } catch (e: Exception) {
            Log.e(TAG, "obtenerConversacionesUsuario error", e)
            emptyList()
        }
    }

    /**
     * Devuelve una conversación por su id, o null si no existe.
     * Equivale a: obtenerConversacionPorId()
     */
    suspend fun obtenerConversacionPorId(idConversacion: Int): Conversacion? {
        return try {
            db["conversacion"]
                .select { filter { eq("id_conversacion", idConversacion) } }
                .decodeList<ConversacionSB>()
                .firstOrNull()
                ?.toLocal(0)
        } catch (e: Exception) {
            Log.e(TAG, "obtenerConversacionPorId error", e)
            null
        }
    }

    /**
     * Marca como leídos todos los mensajes de una conversación que no sean del lector.
     * Equivale a: marcarMensajesLeidos()
     */
    suspend fun marcarMensajesLeidos(idConversacion: Int, idUsuarioLector: Int) {
        try {
            db["mensaje"].update(
                mapOf("leido" to true)
            ) {
                filter {
                    eq("id_conversacion", idConversacion)
                    neq("id_remitente", idUsuarioLector)
                    eq("leido", false)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "marcarMensajesLeidos error", e)
        }
    }
}

// ═══════════════════════════════════════════ EXTENSIONES DE MAPEO ════
// Convierten los modelos Supabase (SB) a los modelos locales existentes,
// para que el resto de tu app no necesite cambiar.

fun UsuarioSB.toLocal()     = Usuario(id_usuario, nombre, email, contrasena, fecha_registro)
fun CategoriaSB.toLocal()   = Categoria(id_categoria, nombre, descripcion ?: "")
fun LocalSB.toLocal()       = Local(id_local, nombre, descripcion ?: "", direccion ?: "",
    capacidad, precio_base, tipo_precio ?: "", id_propietario, id_categoria)
fun ReservaSB.toLocal()     = Reserva(id_reserva, fecha_inicio, fecha_fin, estado, precio_total, id_usuario, id_local)
fun MensajeSB.toLocal()     = Mensaje(id_mensaje, id_conversacion, id_remitente, contenido, fecha_hora, leido)
fun ConversacionSB.toLocal(noLeidos: Int = 0) = Conversacion(
    id_conversacion, id_usuario1, id_usuario2, id_local,
    ultimo_mensaje, ultima_fecha, noLeidos
)
