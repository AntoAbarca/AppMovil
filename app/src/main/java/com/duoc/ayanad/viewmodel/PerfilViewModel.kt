package com.duoc.ayanad.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.ayanad.model.AppDatabase
import com.duoc.ayanad.model.UsuarioEntity
import com.duoc.ayanad.repository.SesionManager
import com.duoc.ayanad.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

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

class PerfilViewModel : ViewModel() {

    private val prefijoTelefono = "+56 9 "
    private val soloLetras = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,}$".toRegex()
    private val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    private var usuarioActual: UsuarioEntity? = null

    private val _form = MutableStateFlow(PerfilForm())
    val form: StateFlow<PerfilForm> = _form

    private val _errors = MutableStateFlow(PerfilFormError())
    val errors: StateFlow<PerfilFormError> = _errors

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    // Solo los aplicadores tienen clasificación (promedio de las estrellas que les ponen los productores)
    private val _esAplicador = MutableStateFlow(false)
    val esAplicador: StateFlow<Boolean> = _esAplicador

    private val _clasificacion = MutableStateFlow("Sin calificaciones")
    val clasificacion: StateFlow<String> = _clasificacion

    /** Carga desde Room al usuario que inició sesión. */
    fun cargarPerfil(context: Context) {
        viewModelScope.launch {
            _errors.value = PerfilFormError()
            _mensaje.value = null
            val repo = UsuarioRepository(AppDatabase.getDatabase(context).usuarioDao())
            val u = repo.obtenerPorId(SesionManager.usuarioId)
            usuarioActual = u
            _esAplicador.value = u?.tipoUsuario == "Aplicador"
            if (u != null && u.tipoUsuario == "Aplicador") {
                val ofertaDao = AppDatabase.getDatabase(context).ofertaDao()
                val cantidad = ofertaDao.contarCalificaciones(u.id)
                val promedio = ofertaDao.promedioDeAplicador(u.id) ?: 0.0
                _clasificacion.value = if (cantidad == 0) "Sin calificaciones"
                else String.format(Locale.US, "%.1f / 5  (%d)", promedio, cantidad)
            }
            if (u == null) {
                _form.value = PerfilForm()
                _mensaje.value = "Inicia sesión para ver tu perfil."
            } else {
                _form.value = PerfilForm(
                    nombre = u.nombre,
                    apellido = u.apellido,
                    rut = u.rut,
                    email = u.email,
                    telefono = u.telefono.removePrefix(prefijoTelefono),
                    region = u.region
                )
            }
        }
    }

    fun onNombreChange(v: String) { _form.value = _form.value.copy(nombre = v); validate() }
    fun onApellidoChange(v: String) { _form.value = _form.value.copy(apellido = v); validate() }
    fun onRutChange(v: String) {
        if (v.length <= 12) { _form.value = _form.value.copy(rut = v); validate() }
    }
    fun onEmailChange(v: String) { _form.value = _form.value.copy(email = v); validate() }
    fun onRegionChange(v: String) { _form.value = _form.value.copy(region = v); validate() }
    fun onTelefonoChange(v: String) {
        if (v.all { it.isDigit() } && v.length <= 8) {
            _form.value = _form.value.copy(telefono = v); validate()
        }
    }

    private fun validate() {
        val f = _form.value
        _mensaje.value = null
        _errors.value = PerfilFormError(
            nombre = if (!f.nombre.matches(soloLetras)) "Mínimo 2 letras, sin números" else null,
            apellido = if (!f.apellido.matches(soloLetras)) "Mínimo 2 letras, sin números" else null,
            rut = if (f.rut.length !in 9..12) "El RUT debe tener entre 9 y 12 caracteres" else null,
            email = if (!f.email.matches(emailRegex)) "Formato válido: ejemplo@gmail.com" else null,
            telefono = if (f.telefono.length != 8) "Debe tener exactamente 8 dígitos" else null,
            region = if (f.region.isBlank()) "Selecciona una región" else null
        )
    }

    fun guardarCambios(context: Context) {
        validate()
        if (!_errors.value.isValid()) return
        val actual = usuarioActual
        if (actual == null) {
            _mensaje.value = "No hay un usuario para actualizar."
            return
        }
        val f = _form.value
        viewModelScope.launch {
            val repo = UsuarioRepository(AppDatabase.getDatabase(context).usuarioDao())

            // El correo y el RUT no pueden pertenecer a otra cuenta
            val otroCorreo = repo.buscarPorLogin(f.email.trim())
            if (otroCorreo != null && otroCorreo.id != actual.id) {
                _errors.value = _errors.value.copy(email = "Ese correo ya está en uso")
                return@launch
            }
            val otroRut = repo.buscarPorLogin(f.rut.trim())
            if (otroRut != null && otroRut.id != actual.id) {
                _errors.value = _errors.value.copy(rut = "Ese RUT ya está en uso")
                return@launch
            }

            val actualizado = actual.copy(
                nombre = f.nombre.trim(),
                apellido = f.apellido.trim(),
                rut = f.rut.trim(),
                email = f.email.trim(),
                telefono = prefijoTelefono + f.telefono,
                region = f.region
            )
            repo.actualizarUsuario(actualizado)
            usuarioActual = actualizado
            SesionManager.iniciar(actualizado)   // mantiene nombre/teléfono de la sesión al día
            _mensaje.value = "Cambios guardados"
        }
    }
}
