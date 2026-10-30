package com.duoc.ayanad.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.ayanad.model.AppDatabase
import com.duoc.ayanad.model.UsuarioEntity
import com.duoc.ayanad.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

class UsuarioFormViewModel : ViewModel() {

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

    fun submitForm(context: Context) {
        val currentForm = _form.value

        if (currentForm.tipoUsuario.isBlank()) {
            _mensajeError.value = "Selecciona si eres productor o aplicador."
            return
        }

        val soloLetrasRegex = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,}$".toRegex()
        if (!currentForm.nombre.matches(soloLetrasRegex)) {
            _mensajeError.value = "El nombre debe tener al menos 2 letras y sin números."
            return
        }

        if (!currentForm.apellido.matches(soloLetrasRegex)) {
            _mensajeError.value = "El apellido debe tener al menos 2 letras y sin números."
            return
        }

        if (currentForm.rut.length !in 9..12) {
            _mensajeError.value = "El RUT debe tener entre 9 y 12 caracteres."
            return
        }

        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        if (!currentForm.email.matches(emailRegex)) {
            _mensajeError.value = "El correo debe tener un formato válido (ej: ejemplo@gmail.com)."
            return
        }

        if (currentForm.telefono.length != 8 || !currentForm.telefono.all { it.isDigit() }) {
            _mensajeError.value = "El teléfono debe contener exactamente 8 dígitos."
            return
        }

        if (currentForm.region.isBlank()) {
            _mensajeError.value = "Selecciona una región."
            return
        }

        if (currentForm.tipoUsuario == "Aplicador" && currentForm.especialidad.isBlank()) {
            _mensajeError.value = "Selecciona una especialidad."
            return
        }

        if (currentForm.password.length < 6) {
            _mensajeError.value = "La contraseña debe tener al menos 6 caracteres."
            return
        }

        _mensajeError.value = null

        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            val repository = UsuarioRepository(db.usuarioDao())

            if (repository.buscarPorLogin(currentForm.email.trim()) != null) {
                _mensajeError.value = "Ya existe una cuenta con ese correo."
                return@launch
            }
            if (repository.buscarPorLogin(currentForm.rut.trim()) != null) {
                _mensajeError.value = "Ya existe una cuenta con ese RUT."
                return@launch
            }

            val nuevoUsuario = UsuarioEntity(
                nombre = currentForm.nombre.trim(),
                apellido = currentForm.apellido.trim(),
                rut = currentForm.rut.trim(),
                email = currentForm.email.trim(),
                telefono = "+56 9 ${currentForm.telefono}",
                region = currentForm.region,
                tipoUsuario = currentForm.tipoUsuario,
                especialidad = currentForm.especialidad,
                password = UsuarioRepository.hashear(currentForm.password)
            )
            repository.guardarUsuario(nuevoUsuario)
            _registroExitoso.value = true
        }
    }

    /** Limpia el formulario después de un registro exitoso. */
    fun reiniciar() {
        _form.value = UsuarioForm()
        _mensajeError.value = null
        _registroExitoso.value = false
    }
}
