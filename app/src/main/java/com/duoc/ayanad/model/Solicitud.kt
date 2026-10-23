package com.duoc.ayanad.model

data class SolicitudForm(
    val superficie: String = "",
    val tipoProducto: String = "",
    val region: String = "",
    val tieneAgua: Boolean = true,
    val aplicacion: String = "Diurna",
    val obstaculos: Set<String> = emptySet(),
    val comentarios: String = ""
)

data class SolicitudFormError(
    val superficie: String? = null,
    val tipoProducto: String? = null,
    val region: String? = null
) {
    fun isValid(): Boolean = superficie == null && tipoProducto == null && region == null
}

data class Solicitud(
    val id: Int,
    val tipoProducto: String,
    val superficie: Double,
    val aplicacion: String,
    val region: String,
    val tieneAgua: Boolean,
    val obstaculos: List<String>,
    val comentarios: String,
    val distanciaKm: Int = 0,
    val estado: String = "Pendiente"
)

data class Oferta(
    val id: Int,
    val solicitudId: Int,
    val aplicador: String,
    val precioPorHa: Int,
    val calificacion: Double,
    val aceptada: Boolean = false
)
