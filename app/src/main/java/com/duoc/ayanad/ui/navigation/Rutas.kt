package com.duoc.ayanad.ui.navigation

// Nombres de todas las pantallas. Se usan siempre desde aquí para no escribir textos sueltos.
object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"

    const val INICIO_PRODUCTOR = "inicio_productor"
    const val CREAR_SOLICITUD = "crear_solicitud"
    const val MIS_SOLICITUDES = "mis_solicitudes"
    const val DETALLE_PRODUCTOR = "detalle_productor"   // se usa como detalle_productor/{id}

    const val INICIO_APLICADOR = "inicio_aplicador"
    const val SOLICITUDES_CERCA = "solicitudes_cerca"
    const val DETALLE_APLICADOR = "detalle_aplicador"   // se usa como detalle_aplicador/{id}
    const val MIS_OFERTAS = "mis_ofertas"

    const val PERFIL = "perfil"
    const val ADMIN = "admin"

    fun detalleProductor(id: Int) = "$DETALLE_PRODUCTOR/$id"
    fun detalleAplicador(id: Int) = "$DETALLE_APLICADOR/$id"
}
