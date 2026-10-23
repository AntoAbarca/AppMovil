package com.duoc.ayanad.model

data class LoginForm(
    val usuario: String = "",
    val password: String = ""
)

data class LoginFormError(
    val usuario: String? = null,
    val password: String? = null
) {
    fun isValid(): Boolean = usuario == null && password == null
}