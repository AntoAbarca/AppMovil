package com.duoc.ayanad.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solicitudes")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productorId: Long,
    val tipoProducto: String,
    val superficie: Double,
    val aplicacion: String,
    val region: String,
    val tieneAgua: Boolean,
    val obstaculos: String,      // nombres separados por "|"
    val comentarios: String,
    val distanciaKm: Int = 0,
    val estado: String = "Pendiente"
)

@Entity(tableName = "ofertas")
data class OfertaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val solicitudId: Int,
    val aplicadorId: Long,
    val aplicador: String,
    val telefono: String,
    val precioPorHa: Int,
    val calificacion: Int = 0,   // estrellas (1-5) que puso el productor; 0 = sin calificar
    val estado: String = "Enviada"   // Enviada / Aceptada / Rechazada
)
