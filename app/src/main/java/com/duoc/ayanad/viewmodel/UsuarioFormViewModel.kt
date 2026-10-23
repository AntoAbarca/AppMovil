package com.duoc.ayanad.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.duoc.ayanad.model.UsuarioForm
import com.duoc.ayanad.model.UsuarioFormError
import com.duoc.ayanad.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UsuarioFormViewModel : ViewModel() {

    val TAG = "UsuarioFormViewModel"
    private val repository = UsuarioRepository()

    private val _form = MutableStateFlow(UsuarioForm())
    val form: StateFlow<UsuarioForm> = _form

    private val _errors = MutableStateFlow(UsuarioFormError())
    val errors: StateFlow<UsuarioFormError> = _errors

    private val _registroExitoso = MutableStateFlow(false)
    val registroExitoso: StateFlow<Boolean> = _registroExitoso

    // Funciones para actualizar el estado del formulario cuando cambia el texto
    fun onNombreChange(value: String) {
        _form.value = _form.value.copy(nombre = value)
        validate()
    }

    fun onApellidoChange(value: String) {
        _form.value = _form.value.copy(apellido = value)
        validate()
    }

    fun onRutChange(value: String) {
        _form.value = _form.value.copy(rut = value)
        validate()
    }

    fun onEmailChange(value: String) {
        _form.value = _form.value.copy(email = value)
        validate()
    }

    fun onTelefonoChange(value: String) {
        _form.value = _form.value.copy(telefono = value)
        validate()
    }

    fun onRegionChange(value: String) {
        _form.value = _form.value.copy(region = value)
        validate()
    }

    fun onTipoUsuarioChange(value: String) {
        _form.value = _form.value.copy(tipoUsuario = value)
    }

    fun onEspecialidadChange(value: String){
        _form.value = _form.value.copy(especialidad = value)
    }

    private fun validate() {
        val currentForm = _form.value


        val nombreError = when {
            currentForm.nombre.isBlank() -> "El nombre es obligatorio"
            currentForm.nombre.length < 3 -> "El nombre debe tener al menos 3 caracteres"
            !currentForm.nombre.all { it.isLetter() || it.isWhitespace() } -> "El nombre solo debe contener letras"
            else -> null
        }

        // 2. Apellido: Obligatorio, mínimo 2 letras y NO debe contener números
        val apellidoError = when {
            currentForm.apellido.isBlank() -> "El apellido es obligatorio"
            currentForm.apellido.length < 2 -> "El apellido debe tener al menos 2 caracteres"
            !currentForm.apellido.all { it.isLetter() || it.isWhitespace() } -> "El apellido solo debe contener letras"
            else -> null
        }

        // 3. RUT: Máximo 12 caracteres (ej: 12.345.678-K), no letras al azar y mínimo 8
        val rutClean = currentForm.rut.replace(".", "").replace("-", "")
        val esRutValido = currentForm.rut.matches(Regex("^[0-9kK.-]+$")) // Solo números, K, puntos y guión

        val rutError = when {
            currentForm.rut.isBlank() -> "El RUT es obligatorio"
            currentForm.rut.length > 12 -> "El RUT no puede tener más de 12 caracteres"
            !esRutValido -> "El RUT solo puede contener números, puntos, guión o 'K'"
            rutClean.length < 8 -> "Ingresa un RUT válido (ej: 12.345.678-K)"
            else -> null
        }

        // 4. Email
        val emailError = when {
            currentForm.email.isBlank() -> "El email es obligatorio"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(currentForm.email).matches() -> "Formato de email inválido"
            else -> null
        }

        // 5. Teléfono: Solo números, entre 8 y 9 dígitos
        val telefonoError = when {
            currentForm.telefono.isBlank() -> "El teléfono es obligatorio"
            !currentForm.telefono.all { it.isDigit() } -> "El teléfono solo debe contener números"
            currentForm.telefono.length < 9 || currentForm.telefono.length > 11 -> "El teléfono debe tener entre 8 y 9 dígitos"
            else -> null
        }

        // 6. Región
        val regionError = if (currentForm.region.isBlank()) "Debes seleccionar una región" else null

        _errors.value = UsuarioFormError(
            nombre = nombreError,
            apellido = apellidoError,
            rut = rutError,
            email = emailError,
            telefono = telefonoError,
            region = regionError
        )

    }

    fun submit() {
        validate()
        if (_errors.value.isValid()) {
            val guardado = repository.guardarUsuario(_form.value)
            _registroExitoso.value = guardado
        }
    }
}