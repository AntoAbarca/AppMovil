package com.duoc.ayanad.model
data class UsuarioForm(
    val nombre: String = "",
    val apellido: String = "",
    val rut: String = "",
    val email: String = "",
    val telefono: String = "",
    val region: String = "",
    val tipoUsuario: String = "Cliente",
    val especialidad: String = "Drones"
)
data class UsuarioFormError(
    val nombre: String? = null,
    val apellido: String? = null,
    val rut: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val region: String? = null
) {
    fun isValid(): Boolean {
        return nombre == null && apellido == null && rut == null &&
                email == null && telefono == null && region == null
    }
}