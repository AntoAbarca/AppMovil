package com.duoc.ayanad.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.ayanad.model.AppDatabase
import com.duoc.ayanad.model.LoginForm
import com.duoc.ayanad.model.LoginFormError
import com.duoc.ayanad.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Solo guarda el estado de la pantalla; las reglas están en UsuarioRepository.
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UsuarioRepository(AppDatabase.getDatabase(application), application.filesDir)

    private val _form = MutableStateFlow(LoginForm())
    val form: StateFlow<LoginForm> = _form

    private val _errors = MutableStateFlow(LoginFormError())
    val errors: StateFlow<LoginFormError> = _errors

    private val _loginExitoso = MutableStateFlow(false)
    val loginExitoso: StateFlow<Boolean> = _loginExitoso

    // Error general (usuario o contraseña incorrectos)
    private val _errorLogin = MutableStateFlow<String?>(null)
    val errorLogin: StateFlow<String?> = _errorLogin

    fun onUsuarioChange(value: String) {
        _form.value = _form.value.copy(usuario = value)
        _errorLogin.value = null
        _errors.value = repository.validarLogin(_form.value)
    }

    fun onPasswordChange(value: String) {
        _form.value = _form.value.copy(password = value)
        _errorLogin.value = null
        _errors.value = repository.validarLogin(_form.value)
    }

    fun submit() {
        _errors.value = repository.validarLogin(_form.value)
        if (!_errors.value.isValid()) return

        viewModelScope.launch {
            if (repository.iniciarSesion(_form.value)) {
                _errorLogin.value = null
                _loginExitoso.value = true
            } else {
                _errorLogin.value = "Usuario o contraseña incorrectos"
            }
        }
    }

    /** Se llama cuando la navegación ya procesó el login: deja el formulario limpio. */
    fun consumirLogin() {
        _loginExitoso.value = false
        _form.value = LoginForm()
        _errors.value = LoginFormError()
        _errorLogin.value = null
    }
}
