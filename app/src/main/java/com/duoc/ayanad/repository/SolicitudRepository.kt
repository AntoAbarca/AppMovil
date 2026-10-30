package com.duoc.ayanad.repository

import com.duoc.ayanad.model.AppDatabase
import com.duoc.ayanad.model.Oferta
import com.duoc.ayanad.model.OfertaDetalle
import com.duoc.ayanad.model.OfertaEntity
import com.duoc.ayanad.model.Solicitud
import com.duoc.ayanad.model.SolicitudEntity

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
        ofertaDao.getDeSolicitud(solicitudId).map { it.toDomain() }

    fun obtenerOfertaDe(solicitudId: Int, aplicadorId: Long): Oferta? =
        ofertaDao.getPorSolicitudYAplicador(solicitudId, aplicadorId)?.toDomain()

    fun obtenerOfertasDeAplicador(aplicadorId: Long): List<OfertaDetalle> =
        ofertaDao.getDeAplicador(aplicadorId).map { o ->
            OfertaDetalle(o.toDomain(), obtenerSolicitud(o.solicitudId))
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

private fun OfertaEntity.toDomain() = Oferta(
    id = id,
    solicitudId = solicitudId,
    aplicador = aplicador,
    precioPorHa = precioPorHa,
    calificacion = calificacion,
    estado = estado,
    aplicadorId = aplicadorId,
    telefono = telefono
)
