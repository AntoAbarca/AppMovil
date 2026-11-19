package com.duoc.ayanad.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.duoc.ayanad.model.AppDatabase
import com.duoc.ayanad.model.Oferta
import com.duoc.ayanad.model.OfertaDetalle
import com.duoc.ayanad.model.Solicitud
import com.duoc.ayanad.model.SolicitudForm
import com.duoc.ayanad.model.SolicitudFormError
import com.duoc.ayanad.repository.SesionManager
import com.duoc.ayanad.repository.SolicitudRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Solo guarda el estado de las pantallas; validaciones, estados y consultas están en SolicitudRepository.
class SolicitudViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SolicitudRepository(AppDatabase.getDatabase(application))

    // =====================================================
    // Pantalla 5: Nueva solicitud
    // =====================================================
    private val _form = MutableStateFlow(SolicitudForm())
    val form: StateFlow<SolicitudForm> = _form

    private val _errors = MutableStateFlow(SolicitudFormError())
    val errors: StateFlow<SolicitudFormError> = _errors

    private val _publicada = MutableStateFlow<Solicitud?>(null)
    val publicada: StateFlow<Solicitud?> = _publicada

    fun onSuperficieChange(value: String) {
        if (repository.aceptaSuperficie(value)) actualizarForm(_form.value.copy(superficie = value))
    }

    fun onTipoProductoChange(value: String) = actualizarForm(_form.value.copy(tipoProducto = value))

    fun onRegionChange(value: String) = actualizarForm(_form.value.copy(region = value))

    fun onTieneAguaChange(value: Boolean) {
        _form.value = _form.value.copy(tieneAgua = value)
    }

    fun onAplicacionChange(value: String) {
        _form.value = _form.value.copy(aplicacion = value)
    }

    fun onObstaculoToggle(nombre: String) {
        _form.value = _form.value.copy(obstaculos = repository.alternarObstaculo(_form.value.obstaculos, nombre))
    }

    fun onComentariosChange(value: String) {
        if (repository.aceptaComentarios(value)) _form.value = _form.value.copy(comentarios = value)
    }

    private fun actualizarForm(nuevo: SolicitudForm) {
        _form.value = nuevo
        _errors.value = repository.validarSolicitud(nuevo)
    }

    fun publicarSolicitud() {
        _errors.value = repository.validarSolicitud(_form.value)
        if (!_errors.value.isValid()) return
        _publicada.value = repository.publicarSolicitud(_form.value, SesionManager.usuarioId)
        _form.value = SolicitudForm()
        _errors.value = SolicitudFormError()
    }

    fun consumirPublicada() { _publicada.value = null }

    // =====================================================
    // Pantalla 4: Mis solicitudes (productor)
    // =====================================================
    private val _misSolicitudes = MutableStateFlow<List<Solicitud>>(emptyList())
    val misSolicitudes: StateFlow<List<Solicitud>> = _misSolicitudes

    fun cargarMisSolicitudes() {
        _misSolicitudes.value = repository.obtenerSolicitudesDeProductor(SesionManager.usuarioId)
    }

    // =====================================================
    // Pantalla 6: Detalle de solicitud con ofertas (productor)
    // =====================================================
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

    fun calificarOferta(ofertaId: Int, estrellas: Int) {
        repository.calificarOferta(ofertaId, estrellas)
        _solicitudActual.value?.let { cargarDetalle(it.id) }
    }

    // =====================================================
    // Pantalla 8: Solicitudes cercanas (aplicador)
    // =====================================================
    private val _filtroRegion = MutableStateFlow("")
    val filtroRegion: StateFlow<String> = _filtroRegion

    private val _solicitudesCercanas = MutableStateFlow<List<Solicitud>>(emptyList())
    val solicitudesCercanas: StateFlow<List<Solicitud>> = _solicitudesCercanas

    fun onFiltroRegionChange(region: String) {
        _filtroRegion.value = region
        cargarCercanas()
    }

    fun limpiarFiltro() = onFiltroRegionChange("")

    fun cargarCercanas() {
        _solicitudesCercanas.value = repository.solicitudesAbiertas(_filtroRegion.value)
    }

    // =====================================================
    // Pantalla 9: Detalle de solicitud + enviar oferta (aplicador)
    // =====================================================
    private val _precioOferta = MutableStateFlow("")
    val precioOferta: StateFlow<String> = _precioOferta

    private val _errorOferta = MutableStateFlow<String?>(null)
    val errorOferta: StateFlow<String?> = _errorOferta

    private val _ofertaEnviada = MutableStateFlow(false)
    val ofertaEnviada: StateFlow<Boolean> = _ofertaEnviada

    private val _miOferta = MutableStateFlow<Oferta?>(null)
    val miOferta: StateFlow<Oferta?> = _miOferta

    fun cargarDetalleAplicador(solicitudId: Int) {
        _solicitudActual.value = repository.obtenerSolicitud(solicitudId)
        val mia = repository.obtenerOfertaDe(solicitudId, SesionManager.usuarioId)
        _miOferta.value = mia
        _precioOferta.value = mia?.precioPorHa?.toString() ?: ""
        _errorOferta.value = null
        _ofertaEnviada.value = false
    }

    fun onPrecioOfertaChange(value: String) {
        if (repository.aceptaPrecio(value)) {
            _precioOferta.value = value
            _errorOferta.value = repository.validarPrecioOferta(value)
        }
    }

    fun enviarOferta() {
        val solicitud = _solicitudActual.value ?: return
        val error = repository.enviarOferta(
            solicitud = solicitud,
            miOferta = _miOferta.value,
            precio = _precioOferta.value,
            aplicadorId = SesionManager.usuarioId,
            aplicador = SesionManager.nombre,
            telefono = SesionManager.telefono
        )
        _errorOferta.value = error
        if (error == null) _ofertaEnviada.value = true
    }

    fun consumirOfertaEnviada() { _ofertaEnviada.value = false }

    // =====================================================
    // Pantalla 10: Mis ofertas (aplicador)
    // =====================================================
    private val _misOfertas = MutableStateFlow<List<OfertaDetalle>>(emptyList())
    val misOfertas: StateFlow<List<OfertaDetalle>> = _misOfertas

    fun cargarMisOfertas() {
        _misOfertas.value = repository.obtenerOfertasDeAplicador(SesionManager.usuarioId)
    }

    // =====================================================
    // Pantallas 3 y 7: contadores de inicio (primero, segundo)
    // =====================================================
    private val _resumen = MutableStateFlow(Pair(0, 0))
    val resumen: StateFlow<Pair<Int, Int>> = _resumen

    fun cargarResumenProductor() {
        _resumen.value = repository.resumenProductor(SesionManager.usuarioId)
    }

    fun cargarResumenAplicador() {
        _resumen.value = repository.resumenAplicador(SesionManager.usuarioId)
    }
}
