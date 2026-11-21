package com.duoc.ayanad.model

// Texto de una notificación de ofertas. El id distingue un tipo de aviso de otro,
// así "ofertas nuevas" y "ofertas disponibles" no se reemplazan entre sí.
data class AvisoNotificacion(
    val titulo: String,
    val mensaje: String,
    val id: Int = ID_OFERTAS
) {
    companion object {
        const val ID_OFERTAS = 1001        // ofertas nuevas / ofertas aceptadas
        const val ID_DISPONIBLES = 1002    // ofertas disponibles en el área del aplicador
    }
}
