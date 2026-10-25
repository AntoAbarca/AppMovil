package com.duoc.ayanad.model

data class Usuario (
    val nombre: String,
    val apellido: String,
    val rut: String,
    val email: String,
    val telefono: String,
    val region: String,
    val tipoUsuario: String,
    val especialidad: String
)