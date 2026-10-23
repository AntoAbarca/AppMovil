package com.duoc.ayanad.repository

import com.duoc.ayanad.model.Oferta
import com.duoc.ayanad.model.Solicitud

class SolicitudRepository {

    private val solicitudes = mutableListOf(
        Solicitud(1, "granulada", 10.0, "Diurna", "Región Metropolitana", true,
            listOf("Cables", "Torres de alta tensión"), "", 20, "Pendiente"),
        Solicitud(2, "líquida", 10.0, "Diurna", "Región Metropolitana", true,
            listOf("Antenas"), "", 20, "Pendiente"),
        Solicitud(3, "granulada", 10.0, "Diurna", "Región Metropolitana", false,
            emptyList(), "", 20, "Pendiente"),
        Solicitud(4, "líquida", 50.0, "Diurna", "Valparaíso", true,
            emptyList(), "", 85, "Con ofertas")
    )

    private val ofertas = mutableListOf(
        Oferta(1, 1, "John Dutton", 25000, 4.6),
        Oferta(2, 1, "John Dutton", 27000, 4.6),
        Oferta(3, 1, "John Dutton", 23500, 4.6)
    )

    private var nextId = 5

    fun obtenerSolicitudes(): List<Solicitud> = solicitudes.toList()

    fun obtenerSolicitud(id: Int): Solicitud? = solicitudes.firstOrNull { it.id == id }

    fun agregarSolicitud(s: Solicitud): Solicitud {
        val nueva = s.copy(id = nextId++)
        solicitudes.add(nueva)
        return nueva
    }

    fun obtenerOfertas(solicitudId: Int): List<Oferta> =
        ofertas.filter { it.solicitudId == solicitudId }

    fun aceptarOferta(ofertaId: Int) {
        val oferta = ofertas.firstOrNull { it.id == ofertaId } ?: return
        ofertas.replaceAll {
            if (it.solicitudId == oferta.solicitudId) it.copy(aceptada = it.id == ofertaId) else it
        }
    }
}
