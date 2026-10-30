package com.duoc.ayanad.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.ayanad.model.AppDatabase
import com.duoc.ayanad.model.LoginForm
import com.duoc.ayanad.model.LoginFormError
import com.duoc.ayanad.repository.SesionManager
import com.duoc.ayanad.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    val TAG = "LoginViewModel"

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
        validate()
    }

    fun onPasswordChange(value: String) {
        _form.value = _form.value.copy(password = value)
        _errorLogin.value = null
        validate()
    }

    private fun validate() {
        val currentForm = _form.value

        val usuarioError = when {
            currentForm.usuario.isBlank() -> "El usuario es obligatorio"
            else -> null
        }

        val passwordError = when {
            currentForm.password.isBlank() -> "La contraseña es obligatoria"
            else -> null
        }

        _errors.value = LoginFormError(
            usuario = usuarioError,
            password = passwordError
        )
    }

    fun submit(context: Context) {
        validate()
        if (!_errors.value.isValid()) return

        val login = _form.value.usuario.trim()

        // Administrador: cuenta fija, no pasa por la base de datos
        if (login.equals(SesionManager.ADMIN_EMAIL, ignoreCase = true) &&
            _form.value.password == SesionManager.ADMIN_PASSWORD
        ) {
            SesionManager.iniciarAdmin()
            _errorLogin.value = null
            _loginExitoso.value = true
            return
        }

        val hash = UsuarioRepository.hashear(_form.value.password)

        viewModelScope.launch {
            val repo = UsuarioRepository(AppDatabase.getDatabase(context).usuarioDao())
            val usuario = repo.buscarPorLogin(login)
            if (usuario == null || usuario.password != hash) {
                _errorLogin.value = "Usuario o contraseña incorrectos"
                return@launch
            }
            SesionManager.iniciar(usuario)
            _errorLogin.value = null
            _loginExitoso.value = true
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
