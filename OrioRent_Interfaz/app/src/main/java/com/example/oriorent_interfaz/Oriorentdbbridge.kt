package com.example.oriorent_interfaz

object OrioRentDB {

    // ══════════════════════════════════════════════ USUARIOS ════

    suspend fun insertarUsuario(nombre: String, email: String, contrasena: String): Long =
        SupabaseRepository.insertarUsuario(nombre, email, contrasena).toLong()

    suspend fun verificarLogin(email: String, contrasena: String): Boolean =
        SupabaseRepository.verificarLogin(email, contrasena)

    suspend fun obtenerUsuarioPorEmail(email: String): Usuario? =
        SupabaseRepository.obtenerUsuarioPorEmail(email)

    suspend fun obtenerUsuarioPorId(id: Int): Usuario? =
        SupabaseRepository.obtenerUsuarioPorId(id)

    // ══════════════════════════════════════════════ CATEGORÍAS ════

    suspend fun obtenerCategorias(): List<Categoria> =
        SupabaseRepository.obtenerCategorias()

    // ══════════════════════════════════════════════ LOCALES ════

    suspend fun insertarLocal(
        nombre: String, descripcion: String, direccion: String,
        capacidad: Int, precioBase: Double, tipoPrecio: String,
        idPropietario: Int, idCategoria: Int
    ): Long = SupabaseRepository.insertarLocal(
        nombre, descripcion, direccion,
        capacidad, precioBase, tipoPrecio,
        idPropietario, idCategoria
    ).toLong()

    suspend fun obtenerLocales(): List<Local> =
        SupabaseRepository.obtenerLocales()

    suspend fun obtenerLocalPorId(idLocal: Int): Local? =
        SupabaseRepository.obtenerLocalPorId(idLocal)

    // ══════════════════════════════════════════════ IMÁGENES ════

    suspend fun insertarImagenLocal(idLocal: Int, urlImagen: String): Long =
        SupabaseRepository.insertarImagenLocal(idLocal, urlImagen).toLong()

    suspend fun obtenerImagenesLocal(idLocal: Int): List<String> =
        SupabaseRepository.obtenerImagenesLocal(idLocal)

    // ══════════════════════════════════════════════ RESERVAS ════

    suspend fun verificarDisponibilidad(idLocal: Int, fechaInicio: String, fechaFin: String): Boolean =
        SupabaseRepository.verificarDisponibilidad(idLocal, fechaInicio, fechaFin)

    suspend fun insertarReserva(
        idUsuario: Int, idLocal: Int,
        fechaInicio: String, fechaFin: String, precioTotal: Double
    ): Long = SupabaseRepository.insertarReserva(idUsuario, idLocal, fechaInicio, fechaFin, precioTotal).toLong()

    suspend fun obtenerReservasUsuario(idUsuario: Int): List<Reserva> =
        SupabaseRepository.obtenerReservasUsuario(idUsuario)

    suspend fun cancelarReserva(idReserva: Int): Boolean =
        SupabaseRepository.cancelarReserva(idReserva)

    // ══════════════════════════════════════════════ FAVORITOS ════

    suspend fun toggleFavorito(idUsuario: Int, idLocal: Int): Boolean =
        SupabaseRepository.toggleFavorito(idUsuario, idLocal)

    suspend fun esFavorito(idUsuario: Int, idLocal: Int): Boolean =
        SupabaseRepository.esFavorito(idUsuario, idLocal)

    // ══════════════════════════════════════════════ MENSAJERÍA ════

    suspend fun obtenerOCrearConversacion(idUsuario1: Int, idUsuario2: Int, idLocal: Int): Int =
        SupabaseRepository.obtenerOCrearConversacion(idUsuario1, idUsuario2, idLocal)

    suspend fun insertarMensaje(
        idConversacion: Int, idRemitente: Int,
        contenido: String, fechaHora: String
    ): Long = SupabaseRepository.insertarMensaje(idConversacion, idRemitente, contenido, fechaHora).toLong()

    suspend fun obtenerMensajesConversacion(idConversacion: Int): List<Mensaje> =
        SupabaseRepository.obtenerMensajesConversacion(idConversacion)

    suspend fun obtenerConversacionesUsuario(idUsuario: Int): List<Conversacion> =
        SupabaseRepository.obtenerConversacionesUsuario(idUsuario)

    suspend fun obtenerConversacionPorId(idConversacion: Int): Conversacion? =
        SupabaseRepository.obtenerConversacionPorId(idConversacion)

    suspend fun marcarMensajesLeidos(idConversacion: Int, idUsuarioLector: Int) =
        SupabaseRepository.marcarMensajesLeidos(idConversacion, idUsuarioLector)
}