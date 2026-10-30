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
        if (value.length <= 8 && value.all { it.isDigit() || it == '.' } && value.count { it == '.' } <= 1) {
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
        if (value.length <= 300) _form.value = _form.value.copy(comentarios = value)
    }

    private fun validate() {
        val f = _form.value
        val sup = f.superficie.toDoubleOrNull()
        _errors.value = SolicitudFormError(
            superficie = when {
                f.superficie.isBlank() -> "Ingresa la superficie"
                sup == null || sup <= 0.0 -> "Debe ser un número mayor a 0"
                sup > 10000.0 -> "Máximo 10.000 ha"
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
                comentarios = f.comentarios.trim()
            ),
            SesionManager.usuarioId
        )
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

    // =====================================================
    // Pantalla 8: Solicitudes cercanas (aplicador)
    // =====================================================
    private val _filtroRegion = MutableStateFlow("")
    val filtroRegion: StateFlow<String> = _filtroRegion

    private val _solicitudesCercanas = MutableStateFlow<List<Solicitud>>(emptyList())
    val solicitudesCercanas: StateFlow<List<Solicitud>> = _solicitudesCercanas

    fun onFiltroRegionChange(region: String) {
        _filtroRegion.value = region
        refrescarCercanas()
    }

    fun limpiarFiltro() = onFiltroRegionChange("")

    fun cargarCercanas() = refrescarCercanas()

    private fun refrescarCercanas() {
        val region = _filtroRegion.value
        _solicitudesCercanas.value = repository.obtenerSolicitudes()
            .filter { it.estado != "Finalizada" && (region.isBlank() || it.region == region) }
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
        if (value.length <= 7 && value.all { it.isDigit() }) {
            _precioOferta.value = value
            validarOferta()
        }
    }

    private fun validarOferta(): Boolean {
        val precio = _precioOferta.value.toIntOrNull()
        _errorOferta.value = when {
            _precioOferta.value.isBlank() -> "Ingresa el precio por ha"
            precio == null || precio <= 0 -> "Debe ser mayor a 0"
            precio < 1000 -> "El mínimo es 1.000 CLP por ha"
            else -> null
        }
        return _errorOferta.value == null
    }

    fun enviarOferta() {
        val s = _solicitudActual.value ?: return
        if (!validarOferta()) return
        if (s.estado == "Finalizada" || _miOferta.value?.estado == "Aceptada") {
            _errorOferta.value = "Esta solicitud ya no admite ofertas"
            return
        }
        repository.guardarOferta(
            solicitudId = s.id,
            aplicadorId = SesionManager.usuarioId,
            aplicador = SesionManager.nombre,
            telefono = SesionManager.telefono,
            precioPorHa = _precioOferta.value.toInt()
        )
        _ofertaEnviada.value = true
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

    /** (solicitudes activas, ofertas nuevas) */
    fun cargarResumenProductor() {
        val id = SesionManager.usuarioId
        _resumen.value = Pair(repository.contarSolicitudesActivas(id), repository.contarOfertasNuevas(id))
    }

    /** (ofertas enviadas, ofertas aceptadas) */
    fun cargarResumenAplicador() {
        val id = SesionManager.usuarioId
        _resumen.value = Pair(repository.contarOfertasEnviadas(id), repository.contarOfertasAceptadas(id))
    }
}
