package com.duoc.ayanad.viewmodel

import androidx.lifecycle.ViewModel
import com.duoc.ayanad.model.Oferta
import com.duoc.ayanad.model.Solicitud
import com.duoc.ayanad.model.SolicitudForm
import com.duoc.ayanad.model.SolicitudFormError
import com.duoc.ayanad.repository.SolicitudRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SolicitudViewModel : ViewModel() {

    private val repository = SolicitudRepository()

    private val _form = MutableStateFlow(SolicitudForm())
    val form: StateFlow<SolicitudForm> = _form

    private val _errors = MutableStateFlow(SolicitudFormError())
    val errors: StateFlow<SolicitudFormError> = _errors

    private val _publicada = MutableStateFlow<Solicitud?>(null)
    val publicada: StateFlow<Solicitud?> = _publicada

    fun onSuperficieChange(value: String) {

        if (value.all { it.isDigit() || it == '.' } && value.count { it == '.' } <= 1) {
            _form.value = _form.value.copy(superficie = value)
            validate()
        }
    }

    fun onTipoProductoChange(value: String) {
        _form.value = _form.value.copy(tipoProducto = value); validate()
    }

    fun onRegionChange(value: String) {
        _form.value = _form.value.copy(region = value); validate()
    }

    fun onTieneAguaChange(value: Boolean) {
        _form.value = _form.value.copy(tieneAgua = value)
    }

    fun onAplicacionChange(value: String) {
        _form.value = _form.value.copy(aplicacion = value)
    }

    fun onObstaculoToggle(nombre: String) {
        val actual = _form.value.obstaculos
        _form.value = _form.value.copy(
            obstaculos = if (nombre in actual) actual - nombre else actual + nombre
        )
    }

    fun onComentariosChange(value: String) {
        _form.value = _form.value.copy(comentarios = value)
    }

    private fun validate() {
        val f = _form.value
        _errors.value = SolicitudFormError(
            superficie = when {
                f.superficie.isBlank() -> "Ingresa la superficie"
                (f.superficie.toDoubleOrNull() ?: 0.0) <= 0.0 -> "Debe ser mayor a 0"
                else -> null
            },
            tipoProducto = if (f.tipoProducto.isBlank()) "Selecciona un tipo de producto" else null,
            region = if (f.region.isBlank()) "Selecciona una región" else null
        )
    }

    fun publicarSolicitud() {
        validate()
        if (!_errors.value.isValid()) return
        val f = _form.value
        _publicada.value = repository.agregarSolicitud(
            Solicitud(
                id = 0,
                tipoProducto = f.tipoProducto.lowercase(),
                superficie = f.superficie.toDouble(),
                aplicacion = f.aplicacion,
                region = f.region,
                tieneAgua = f.tieneAgua,
                obstaculos = f.obstaculos.toList(),
                comentarios = f.comentarios
            )
        )
        _form.value = SolicitudForm()
        _errors.value = SolicitudFormError()
    }

    fun consumirPublicada() { _publicada.value = null }


    private val _solicitudActual = MutableStateFlow<Solicitud?>(null)
    val solicitudActual: StateFlow<Solicitud?> = _solicitudActual

    private val _ofertas = MutableStateFlow<List<Oferta>>(emptyList())
    val ofertas: StateFlow<List<Oferta>> = _ofertas

    fun cargarDetalle(solicitudId: Int) {
        _solicitudActual.value = repository.obtenerSolicitud(solicitudId)
        _ofertas.value = repository.obtenerOfertas(solicitudId)
    }

    fun aceptarOferta(ofertaId: Int) {
        repository.aceptarOferta(ofertaId)
        _solicitudActual.value?.let { cargarDetalle(it.id) }
    }


    private val _filtroRegion = MutableStateFlow("")
    val filtroRegion: StateFlow<String> = _filtroRegion

    private val _solicitudesCercanas = MutableStateFlow(repository.obtenerSolicitudes())
    val solicitudesCercanas: StateFlow<List<Solicitud>> = _solicitudesCercanas

    fun onFiltroRegionChange(region: String) {
        _filtroRegion.value = region
        refrescarCercanas()
    }

    fun limpiarFiltro() = onFiltroRegionChange("")

    private fun refrescarCercanas() {
        val region = _filtroRegion.value
        _solicitudesCercanas.value = repository.obtenerSolicitudes()
            .filter { it.estado == "Pendiente" && (region.isBlank() || it.region == region) }
    }

    init { refrescarCercanas() }
}
