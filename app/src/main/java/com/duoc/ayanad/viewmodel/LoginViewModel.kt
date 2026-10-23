package com.duoc.ayanad.viewmodel

import androidx.lifecycle.ViewModel
import com.duoc.ayanad.model.LoginForm
import com.duoc.ayanad.model.LoginFormError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {

    val TAG = "LoginViewModel"

    private val _form = MutableStateFlow(LoginForm())
    val form: StateFlow<LoginForm> = _form

    private val _errors = MutableStateFlow(LoginFormError())
    val errors: StateFlow<LoginFormError> = _errors

    private val _loginExitoso = MutableStateFlow(false)
    val loginExitoso: StateFlow<Boolean> = _loginExitoso

    // Funciones para actualizar el estado del formulario cuando cambia el texto
    fun onUsuarioChange(value: String) {
        _form.value = _form.value.copy(usuario = value)
        validate()
    }

    fun onPasswordChange(value: String) {
        _form.value = _form.value.copy(password = value)
        validate()
    }

    private fun validate() {
        val currentForm = _form.value

        // 1. Usuario: obligatorio
        val usuarioError = when {
            currentForm.usuario.isBlank() -> "El usuario es obligatorio"
            else -> null
        }

        // 2. Password: obligatoria
        val passwordError = when {
            currentForm.password.isBlank() -> "La contraseña es obligatoria"
            else -> null
        }

        _errors.value = LoginFormError(
            usuario = usuarioError,
            password = passwordError
        )
    }

    fun submit() {
        validate()
        if (_errors.value.isValid()) {
            // TODO: comprobar con UsuarioRepository que el usuario y la contraseña existan
            _loginExitoso.value = true
        }
    }
}