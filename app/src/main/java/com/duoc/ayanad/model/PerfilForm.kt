package com.duoc.ayanad.model

data class PerfilForm(
    val nombre: String = "",
    val apellido: String = "",
    val rut: String = "",
    val email: String = "",
    val telefono: String = "",   // solo los 8 dígitos; el "+56 9 " se agrega al guardar
    val region: String = ""
)

data class PerfilFormError(
    val nombre: String? = null,
    val apellido: String? = null,
    val rut: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val region: String? = null
) {
    fun isValid(): Boolean = nombre == null && apellido == null && rut == null &&
            email == null && telefono == null && region == null
}

// Resultado de guardar el perfil: el usuario actualizado (null si hubo errores) y los errores por campo
data class ResultadoPerfil(
    val usuario: UsuarioEntity?,
    val errores: PerfilFormError
)
