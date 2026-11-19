package com.duoc.ayanad.model

// Lo que se escribe en el registro
data class UsuarioForm(
    val nombre: String = "",
    val apellido: String = "",
    val rut: String = "",
    val email: String = "",
    val telefono: String = "",
    val region: String = "",
    val tipoUsuario: String = "",
    val especialidad: String = "",
    val password: String = ""
)
