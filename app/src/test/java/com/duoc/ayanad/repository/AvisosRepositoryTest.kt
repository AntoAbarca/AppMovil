package com.duoc.ayanad.repository

import com.duoc.ayanad.model.AppDatabase
import com.duoc.ayanad.model.AvisoNotificacion
import com.duoc.ayanad.model.OfertaDao
import com.duoc.ayanad.model.OfertaEntity
import com.duoc.ayanad.model.SolicitudDao
import com.duoc.ayanad.model.SolicitudEntity
import com.duoc.ayanad.model.UsuarioDao
import com.duoc.ayanad.model.UsuarioEntity
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

// Avisos de las notificaciones: ofertas nuevas, oferta aceptada y "hay N ofertas disponibles para tu área"
class AvisosRepositoryTest {

    private val solicitudDao = mockk<SolicitudDao>(relaxed = true)
    private val ofertaDao = mockk<OfertaDao>(relaxed = true)
    private val usuarioDao = mockk<UsuarioDao>(relaxed = true)
    private lateinit var repository: SolicitudRepository

    private val region = "Región Metropolitana"

    @Before
    fun preparar() {
        val db = mockk<AppDatabase>()
        every { db.solicitudDao() } returns solicitudDao
        every { db.ofertaDao() } returns ofertaDao
        every { db.usuarioDao() } returns usuarioDao
        every { ofertaDao.getPorSolicitudYAplicador(any(), any()) } returns null   // nadie ha ofertado todavía
        every { usuarioDao.getPorId(5L) } returns aplicador()
        repository = SolicitudRepository(db)
    }

    private fun aplicador() = UsuarioEntity(
        id = 5, nombre = "Luis", apellido = "Soto", rut = "12345678-9", email = "luis@correo.cl",
        telefono = "+56 9 11112222", region = region, tipoUsuario = "Aplicador", especialidad = "Drones"
    )

    private fun solicitud(id: Int, estado: String = "Pendiente", region: String = this.region) = SolicitudEntity(
        id = id, productorId = 3, tipoProducto = "granulada", superficie = 10.0, aplicacion = "Diurna",
        region = region, tieneAgua = true, obstaculos = "", comentarios = "", estado = estado
    )

    // ---------- Ofertas disponibles en el área (aplicador) ----------
    @Test
    fun `avisa cuantas ofertas hay disponibles en la region del aplicador`() {
        every { solicitudDao.getAll() } returns listOf(solicitud(1), solicitud(2), solicitud(3))

        val aviso = repository.avisoDisponibles("Aplicador", 5L)

        assertNotNull(aviso)
        assertEquals("Hay 3 ofertas disponibles para tu área ($region)", aviso!!.mensaje)
        assertEquals(AvisoNotificacion.ID_DISPONIBLES, aviso.id)
    }

    @Test
    fun `usa singular cuando hay una sola oferta disponible`() {
        every { solicitudDao.getAll() } returns listOf(solicitud(1))

        assertEquals("Hay 1 oferta disponible para tu área ($region)", repository.avisoDisponibles("Aplicador", 5L)!!.mensaje)
    }

    @Test
    fun `no cuenta las finalizadas ni las de otras regiones`() {
        every { solicitudDao.getAll() } returns listOf(
            solicitud(1),
            solicitud(2, estado = "Finalizada"),
            solicitud(3, region = "Región de Valparaíso")
        )

        assertEquals("Hay 1 oferta disponible para tu área ($region)", repository.avisoDisponibles("Aplicador", 5L)!!.mensaje)
    }

    @Test
    fun `no cuenta las solicitudes en las que el aplicador ya oferto`() {
        every { solicitudDao.getAll() } returns listOf(solicitud(1), solicitud(2))
        every { ofertaDao.getPorSolicitudYAplicador(2, 5L) } returns OfertaEntity(
            id = 9, solicitudId = 2, aplicadorId = 5, aplicador = "Luis Soto", telefono = "+56 9 11112222", precioPorHa = 15000
        )

        assertEquals("Hay 1 oferta disponible para tu área ($region)", repository.avisoDisponibles("Aplicador", 5L)!!.mensaje)
    }

    @Test
    fun `no hay aviso si no hay ofertas disponibles`() {
        every { solicitudDao.getAll() } returns emptyList()

        assertNull(repository.avisoDisponibles("Aplicador", 5L))
    }

    @Test
    fun `el productor y el administrador no reciben el aviso de disponibles`() {
        every { solicitudDao.getAll() } returns listOf(solicitud(1))

        assertNull(repository.avisoDisponibles("Productor", 3L))
        assertNull(repository.avisoDisponibles("Administrador", 0L))
    }

    // ---------- Ofertas nuevas (productor) ----------
    @Test
    fun `el productor recibe un aviso con la cantidad de ofertas nuevas`() {
        every { ofertaDao.contarOfertasDesde(3L, 0) } returns 3

        val aviso = repository.avisoPendiente("Productor", 3L)

        assertEquals("Nuevas ofertas", aviso!!.titulo)
        assertEquals("Recibiste 3 ofertas nuevas para tus solicitudes", aviso.mensaje)
        assertEquals(AvisoNotificacion.ID_OFERTAS, aviso.id)
    }

    @Test
    fun `el aviso del productor usa singular cuando es una sola oferta`() {
        every { ofertaDao.contarOfertasDesde(3L, 0) } returns 1

        assertEquals("Recibiste 1 oferta nueva para tus solicitudes", repository.avisoPendiente("Productor", 3L)!!.mensaje)
    }

    @Test
    fun `no hay aviso si el productor no tiene ofertas nuevas`() {
        every { ofertaDao.contarOfertasDesde(3L, 0) } returns 0

        assertNull(repository.avisoPendiente("Productor", 3L))
    }

    // ---------- Oferta aceptada (aplicador) ----------
    @Test
    fun `el aplicador recibe un aviso cuando le aceptan una oferta`() {
        every { ofertaDao.contarAceptadasDeAplicador(5L) } returns 1

        val aviso = repository.avisoPendiente("Aplicador", 5L)

        assertEquals("Oferta aceptada", aviso!!.titulo)
        assertEquals("¡Aceptaron una de tus ofertas!", aviso.mensaje)
    }

    @Test
    fun `no hay aviso si al aplicador no le han aceptado ofertas`() {
        every { ofertaDao.contarAceptadasDeAplicador(5L) } returns 0

        assertNull(repository.avisoPendiente("Aplicador", 5L))
    }

    @Test
    fun `el administrador no recibe avisos`() {
        assertNull(repository.avisoPendiente("Administrador", 0L))
    }
}
