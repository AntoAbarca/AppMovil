package com.duoc.ayanad.repository

import com.duoc.ayanad.model.AppDatabase
import com.duoc.ayanad.model.Oferta
import com.duoc.ayanad.model.OfertaDetalle
import com.duoc.ayanad.model.OfertaEntity
import com.duoc.ayanad.model.Solicitud
import com.duoc.ayanad.model.SolicitudEntity
import com.duoc.ayanad.model.SolicitudForm
import com.duoc.ayanad.model.SolicitudFormError

class SolicitudRepository(db: AppDatabase) {

    private val solicitudDao = db.solicitudDao()
    private val ofertaDao = db.ofertaDao()

    // ---------- Solicitudes ----------
    fun obtenerSolicitudes(): List<Solicitud> = solicitudDao.getAll().map { it.toDomain() }

    fun obtenerSolicitudesDeProductor(productorId: Long): List<Solicitud> =
        solicitudDao.getDeProductor(productorId).map { it.toDomain() }

    fun obtenerSolicitud(id: Int): Solicitud? = solicitudDao.getPorId(id)?.toDomain()

    fun agregarSolicitud(s: Solicitud, productorId: Long): Solicitud {
        val id = solicitudDao.insertar(
            SolicitudEntity(
                productorId = productorId,
                tipoProducto = s.tipoProducto,
                superficie = s.superficie,
                aplicacion = s.aplicacion,
                region = s.region,
                tieneAgua = s.tieneAgua,
                obstaculos = s.obstaculos.joinToString("|"),
                comentarios = s.comentarios,
                distanciaKm = s.distanciaKm,
                estado = "Pendiente"
            )
        )
        return s.copy(id = id.toInt(), productorId = productorId, estado = "Pendiente")
    }

    // ---------- Ofertas ----------
    fun obtenerOfertas(solicitudId: Int): List<Oferta> =
        ofertaDao.getDeSolicitud(solicitudId).map { aDominio(it) }

    fun obtenerOfertaDe(solicitudId: Int, aplicadorId: Long): Oferta? =
        ofertaDao.getPorSolicitudYAplicador(solicitudId, aplicadorId)?.let { aDominio(it) }

    fun obtenerOfertasDeAplicador(aplicadorId: Long): List<OfertaDetalle> =
        ofertaDao.getDeAplicador(aplicadorId).map { o ->
            OfertaDetalle(aDominio(o), obtenerSolicitud(o.solicitudId))
        }

    /** Crea la oferta, o actualiza el precio si el aplicador ya había ofertado. */
    fun guardarOferta(
        solicitudId: Int,
        aplicadorId: Long,
        aplicador: String,
        telefono: String,
        precioPorHa: Int
    ) {
        val existente = ofertaDao.getPorSolicitudYAplicador(solicitudId, aplicadorId)
        if (existente != null) {
            ofertaDao.actualizarPrecio(existente.id, precioPorHa)
        } else {
            ofertaDao.insertar(
                OfertaEntity(
                    solicitudId = solicitudId,
                    aplicadorId = aplicadorId,
                    aplicador = aplicador,
                    telefono = telefono,
                    precioPorHa = precioPorHa
                )
            )
        }
        val s = solicitudDao.getPorId(solicitudId)
        if (s != null && s.estado == "Pendiente") {
            solicitudDao.actualizarEstado(solicitudId, "Con ofertas")
        }
    }

    fun aceptarOferta(ofertaId: Int) {
        val oferta = ofertaDao.getPorId(ofertaId) ?: return
        ofertaDao.actualizarEstado(ofertaId, "Aceptada")
        ofertaDao.rechazarOtras(oferta.solicitudId, ofertaId)
        solicitudDao.actualizarEstado(oferta.solicitudId, "Finalizada")
    }

    /** El productor califica (1 a 5) el trabajo de una oferta que ya aceptó. */
    fun calificarOferta(ofertaId: Int, estrellas: Int) {
        val oferta = ofertaDao.getPorId(ofertaId) ?: return
        if (oferta.estado != "Aceptada" || estrellas !in 1..5) return
        ofertaDao.calificar(ofertaId, estrellas)
    }

    /** Promedio y cantidad de calificaciones que ha recibido un aplicador. */
    fun resumenCalificacion(aplicadorId: Long): Pair<Double, Int> {
        val promedio = ofertaDao.promedioDeAplicador(aplicadorId) ?: 0.0
        return Pair(Math.round(promedio * 10) / 10.0, ofertaDao.contarCalificaciones(aplicadorId))
    }

    private fun aDominio(o: OfertaEntity): Oferta = Oferta(
        id = o.id,
        solicitudId = o.solicitudId,
        aplicador = o.aplicador,
        precioPorHa = o.precioPorHa,
        calificacion = resumenCalificacion(o.aplicadorId).first,
        estado = o.estado,
        aplicadorId = o.aplicadorId,
        telefono = o.telefono,
        calificacionDada = o.calificacion
    )

    // ---------- Reglas de la pantalla "Nueva solicitud" ----------
    fun aceptaSuperficie(v: String): Boolean =
        v.length <= 8 && v.all { it.isDigit() || it == '.' } && v.count { it == '.' } <= 1

    fun aceptaComentarios(v: String): Boolean = v.length <= 300

    fun alternarObstaculo(actual: Set<String>, nombre: String): Set<String> =
        if (nombre in actual) actual - nombre else actual + nombre

    fun validarSolicitud(f: SolicitudForm): SolicitudFormError {
        val sup = f.superficie.toDoubleOrNull()
        return SolicitudFormError(
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

    /** Valida y publica la solicitud. Devuelve null si el formulario tiene errores. */
    fun publicarSolicitud(f: SolicitudForm, productorId: Long): Solicitud? {
        if (!validarSolicitud(f).isValid()) return null
        return agregarSolicitud(
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
            productorId
        )
    }

    /** Solicitudes abiertas, opcionalmente filtradas por región (vacío = todas). */
    fun solicitudesAbiertas(region: String): List<Solicitud> =
        obtenerSolicitudes().filter { it.estado != "Finalizada" && (region.isBlank() || it.region == region) }

    // ---------- Reglas de la pantalla "Enviar oferta" ----------
    fun aceptaPrecio(v: String): Boolean = v.length <= 7 && v.all { it.isDigit() }

    /** Devuelve el mensaje de error del precio, o null si es válido. */
    fun validarPrecioOferta(precio: String): String? {
        val valor = precio.toIntOrNull()
        return when {
            precio.isBlank() -> "Ingresa el precio por ha"
            valor == null || valor <= 0 -> "Debe ser mayor a 0"
            valor < 1000 -> "El mínimo es 1.000 CLP por ha"
            else -> null
        }
    }

    /** Valida y guarda la oferta. Devuelve el mensaje de error, o null si se envió. */
    fun enviarOferta(
        solicitud: Solicitud,
        miOferta: Oferta?,
        precio: String,
        aplicadorId: Long,
        aplicador: String,
        telefono: String
    ): String? {
        validarPrecioOferta(precio)?.let { return it }
        if (solicitud.estado == "Finalizada" || miOferta?.estado == "Aceptada") {
            return "Esta solicitud ya no admite ofertas"
        }
        guardarOferta(solicitud.id, aplicadorId, aplicador, telefono, precio.toInt())
        return null
    }

    // ---------- Contadores: (solicitudes activas, ofertas nuevas) y (enviadas, aceptadas) ----------
    fun resumenProductor(productorId: Long): Pair<Int, Int> =
        Pair(contarSolicitudesActivas(productorId), contarOfertasNuevas(productorId))

    fun resumenAplicador(aplicadorId: Long): Pair<Int, Int> =
        Pair(contarOfertasEnviadas(aplicadorId), contarOfertasAceptadas(aplicadorId))

    // ---------- Contadores de las pantallas de inicio ----------
    fun contarSolicitudesActivas(productorId: Long): Int = solicitudDao.contarActivas(productorId)
    fun contarOfertasNuevas(productorId: Long): Int = ofertaDao.contarNuevasDeProductor(productorId)
    fun contarOfertasEnviadas(aplicadorId: Long): Int = ofertaDao.contarDeAplicador(aplicadorId)
    fun contarOfertasAceptadas(aplicadorId: Long): Int = ofertaDao.contarAceptadasDeAplicador(aplicadorId)
}

private fun SolicitudEntity.toDomain() = Solicitud(
    id = id,
    tipoProducto = tipoProducto,
    superficie = superficie,
    aplicacion = aplicacion,
    region = region,
    tieneAgua = tieneAgua,
    obstaculos = if (obstaculos.isBlank()) emptyList() else obstaculos.split("|"),
    comentarios = comentarios,
    distanciaKm = distanciaKm,
    estado = estado,
    productorId = productorId
)
