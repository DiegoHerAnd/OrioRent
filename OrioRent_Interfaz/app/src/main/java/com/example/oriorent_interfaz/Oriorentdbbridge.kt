package com.example.oriorent_interfaz

import kotlinx.coroutines.runBlocking

/**
 * PUENTE DE MIGRACIÓN
 * ═══════════════════
 * Este objeto expone exactamente la misma API que OrioRentDBHelper
 * pero por dentro llama a SupabaseRepository (que usa Supabase en la nube).
 *
 * USO: En todos tus Activities/Fragments donde tengas:
 *
 *   val dbHelper = OrioRentDBHelper(context)
 *
 * Cámbialo por:
 *
 *   val dbHelper = OrioRentDB   // sin paréntesis, es un object
 *
 * Y eso es TODO. Ninguna llamada más cambia.
 *
 * NOTA: Usa runBlocking internamente para mantener compatibilidad con código
 * síncrono. Una vez migrado el proyecto a ViewModel/coroutines correctamente,
 * se puede eliminar este puente y llamar a SupabaseRepository directamente.
 */
object OrioRentDB {

    // ══════════════════════════════════════════════ USUARIOS ════

    fun insertarUsuario(nombre: String, email: String, contrasena: String): Long =
        runBlocking { SupabaseRepository.insertarUsuario(nombre, email, contrasena).toLong() }

    fun verificarLogin(email: String, contrasena: String): Boolean =
        runBlocking { SupabaseRepository.verificarLogin(email, contrasena) }

    fun obtenerUsuarioPorEmail(email: String): Usuario? =
        runBlocking { SupabaseRepository.obtenerUsuarioPorEmail(email) }

    fun obtenerUsuarioPorId(id: Int): Usuario? =
        runBlocking { SupabaseRepository.obtenerUsuarioPorId(id) }

    // ══════════════════════════════════════════════ CATEGORÍAS ════

    fun obtenerCategorias(): List<Categoria> =
        runBlocking { SupabaseRepository.obtenerCategorias() }

    // ══════════════════════════════════════════════ LOCALES ════

    fun insertarLocal(
        nombre: String, descripcion: String, direccion: String,
        capacidad: Int, precioBase: Double, tipoPrecio: String,
        idPropietario: Int, idCategoria: Int
    ): Long = runBlocking {
        SupabaseRepository.insertarLocal(
            nombre, descripcion, direccion,
            capacidad, precioBase, tipoPrecio,
            idPropietario, idCategoria
        ).toLong()
    }

    fun obtenerLocales(): List<Local> =
        runBlocking { SupabaseRepository.obtenerLocales() }

    fun obtenerLocalPorId(idLocal: Int): Local? =
        runBlocking { SupabaseRepository.obtenerLocalPorId(idLocal) }

    // ══════════════════════════════════════════════ IMÁGENES ════

    fun insertarImagenLocal(idLocal: Int, urlImagen: String): Long =
        runBlocking { SupabaseRepository.insertarImagenLocal(idLocal, urlImagen).toLong() }

    fun obtenerImagenesLocal(idLocal: Int): List<String> =
        runBlocking { SupabaseRepository.obtenerImagenesLocal(idLocal) }

    // ══════════════════════════════════════════════ RESERVAS ════

    fun verificarDisponibilidad(idLocal: Int, fechaInicio: String, fechaFin: String): Boolean =
        runBlocking { SupabaseRepository.verificarDisponibilidad(idLocal, fechaInicio, fechaFin) }

    fun insertarReserva(
        idUsuario: Int, idLocal: Int,
        fechaInicio: String, fechaFin: String, precioTotal: Double
    ): Long = runBlocking {
        SupabaseRepository.insertarReserva(idUsuario, idLocal, fechaInicio, fechaFin, precioTotal).toLong()
    }

    fun obtenerReservasUsuario(idUsuario: Int): List<Reserva> =
        runBlocking { SupabaseRepository.obtenerReservasUsuario(idUsuario) }

    fun cancelarReserva(idReserva: Int): Boolean =
        runBlocking { SupabaseRepository.cancelarReserva(idReserva) }

    // ══════════════════════════════════════════════ FAVORITOS ════

    fun toggleFavorito(idUsuario: Int, idLocal: Int): Boolean =
        runBlocking { SupabaseRepository.toggleFavorito(idUsuario, idLocal) }

    fun esFavorito(idUsuario: Int, idLocal: Int): Boolean =
        runBlocking { SupabaseRepository.esFavorito(idUsuario, idLocal) }

    // ══════════════════════════════════════════════ MENSAJERÍA ════

    fun obtenerOCrearConversacion(idUsuario1: Int, idUsuario2: Int, idLocal: Int): Int =
        runBlocking { SupabaseRepository.obtenerOCrearConversacion(idUsuario1, idUsuario2, idLocal) }

    fun insertarMensaje(
        idConversacion: Int, idRemitente: Int,
        contenido: String, fechaHora: String
    ): Long = runBlocking {
        SupabaseRepository.insertarMensaje(idConversacion, idRemitente, contenido, fechaHora).toLong()
    }

    fun obtenerMensajesConversacion(idConversacion: Int): List<Mensaje> =
        runBlocking { SupabaseRepository.obtenerMensajesConversacion(idConversacion) }

    fun obtenerConversacionesUsuario(idUsuario: Int): List<Conversacion> =
        runBlocking { SupabaseRepository.obtenerConversacionesUsuario(idUsuario) }

    fun obtenerConversacionPorId(idConversacion: Int): Conversacion? =
        runBlocking { SupabaseRepository.obtenerConversacionPorId(idConversacion) }

    fun marcarMensajesLeidos(idConversacion: Int, idUsuarioLector: Int) =
        runBlocking { SupabaseRepository.marcarMensajesLeidos(idConversacion, idUsuarioLector) }
}