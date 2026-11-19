package com.duoc.ayanad.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.ayanad.model.AppDatabase
import com.duoc.ayanad.model.UsuarioForm
import com.duoc.ayanad.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Solo guarda el estado del registro; las validaciones y el guardado están en UsuarioRepository.
class UsuarioFormViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UsuarioRepository(AppDatabase.getDatabase(application), application.filesDir)

    private val _form = MutableStateFlow(UsuarioForm())
    val form: StateFlow<UsuarioForm> = _form

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError

    private val _registroExitoso = MutableStateFlow(false)
    val registroExitoso: StateFlow<Boolean> = _registroExitoso

    fun actualizarCampo(
        nombre: String = _form.value.nombre,
        apellido: String = _form.value.apellido,
        rut: String = _form.value.rut,
        email: String = _form.value.email,
        telefono: String = _form.value.telefono,
        region: String = _form.value.region,
        tipoUsuario: String = _form.value.tipoUsuario,
        especialidad: String = _form.value.especialidad,
        password: String = _form.value.password
    ) {
        _form.value = UsuarioForm(
            nombre, apellido, rut, email, telefono, region, tipoUsuario, especialidad, password
        )
    }

    fun submitForm() {
        viewModelScope.launch {
            val error = repository.registrar(_form.value)   // valida y guarda; null = todo bien
            _mensajeError.value = error
            if (error == null) _registroExitoso.value = true
        }
    }

    /** Limpia el formulario después de un registro exitoso. */
    fun reiniciar() {
        _form.value = UsuarioForm()
        _mensajeError.value = null
        _registroExitoso.value = false
    }
}
