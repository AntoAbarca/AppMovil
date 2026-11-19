package com.duoc.ayanad.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.ayanad.model.AppDatabase
import com.duoc.ayanad.model.PerfilForm
import com.duoc.ayanad.model.PerfilFormError
import com.duoc.ayanad.model.UsuarioEntity
import com.duoc.ayanad.repository.SesionManager
import com.duoc.ayanad.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Solo guarda el estado del perfil; las validaciones y el guardado están en UsuarioRepository.
class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UsuarioRepository(AppDatabase.getDatabase(application), application.filesDir)

    private var usuarioActual: UsuarioEntity? = null

    private val _form = MutableStateFlow(PerfilForm())
    val form: StateFlow<PerfilForm> = _form

    private val _errors = MutableStateFlow(PerfilFormError())
    val errors: StateFlow<PerfilFormError> = _errors

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    // Solo los aplicadores tienen clasificación
    private val _esAplicador = MutableStateFlow(false)
    val esAplicador: StateFlow<Boolean> = _esAplicador

    private val _clasificacion = MutableStateFlow("Sin calificaciones")
    val clasificacion: StateFlow<String> = _clasificacion

    fun cargarPerfil() {
        viewModelScope.launch {
            _errors.value = PerfilFormError()
            _mensaje.value = null
            val u = repository.obtenerPorId(SesionManager.usuarioId)
            usuarioActual = u
            _esAplicador.value = repository.esAplicador(u)
            _clasificacion.value = repository.clasificacionDe(u)
            if (u == null) {
                _form.value = PerfilForm()
                _mensaje.value = "Inicia sesión para ver tu perfil."
            } else {
                _form.value = repository.perfilFormDe(u)
            }
        }
    }

    fun onNombreChange(v: String) = actualizar(_form.value.copy(nombre = v))
    fun onApellidoChange(v: String) = actualizar(_form.value.copy(apellido = v))
    fun onEmailChange(v: String) = actualizar(_form.value.copy(email = v))
    fun onRegionChange(v: String) = actualizar(_form.value.copy(region = v))
    fun onRutChange(v: String) {
        if (repository.aceptaRut(v)) actualizar(_form.value.copy(rut = v))
    }
    fun onTelefonoChange(v: String) {
        if (repository.aceptaTelefono(v)) actualizar(_form.value.copy(telefono = v))
    }

    // Guarda el nuevo valor y pide al repositorio que lo valide
    private fun actualizar(nuevo: PerfilForm) {
        _form.value = nuevo
        _mensaje.value = null
        _errors.value = repository.validarPerfil(nuevo)
    }

    fun guardarCambios() {
        _errors.value = repository.validarPerfil(_form.value)
        if (!_errors.value.isValid()) return
        val actual = usuarioActual
        if (actual == null) {
            _mensaje.value = "No hay un usuario para actualizar."
            return
        }
        viewModelScope.launch {
            val resultado = repository.guardarPerfil(actual, _form.value)
            _errors.value = resultado.errores
            if (resultado.usuario != null) {
                usuarioActual = resultado.usuario
                _mensaje.value = "Cambios guardados"
            }
        }
    }
}
