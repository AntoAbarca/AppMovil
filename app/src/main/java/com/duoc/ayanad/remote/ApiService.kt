package com.duoc.ayanad.remote

import com.duoc.ayanad.model.OfertaEntity
import com.duoc.ayanad.model.SolicitudEntity
import com.duoc.ayanad.model.UsuarioEntity
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("usuarios")
    suspend fun obtenerUsuarios(): List<UsuarioEntity>

    @GET("usuarios/{id}")
    suspend fun obtenerUsuario(@Path("id") id: Long): UsuarioEntity

    @POST("usuarios/login")
    suspend fun loginUsuario(@Body credenciales: Map<String, String>): UsuarioEntity

    @POST("usuarios")
    suspend fun registrarUsuario(@Body usuario: UsuarioEntity): UsuarioEntity

    @PATCH("usuarios/{id}")
    suspend fun actualizarUsuario(@Path("id") id: Long, @Body usuario: UsuarioEntity): UsuarioEntity

    @DELETE("usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Long)

    @GET("solicitudes")
    suspend fun obtenerSolicitudes(): List<SolicitudEntity>

    @GET("solicitudes/productor/{productorId}")
    suspend fun obtenerSolicitudesDeProductor(@Path("productorId") productorId: Long): List<SolicitudEntity>

    @GET("solicitudes/{id}")
    suspend fun obtenerSolicitud(@Path("id") id: Int): SolicitudEntity

    @POST("solicitudes")
    suspend fun crearSolicitud(@Body solicitud: SolicitudEntity): SolicitudEntity

    @PATCH("solicitudes/{id}/estado")
    suspend fun actualizarEstadoSolicitud(@Path("id") id: Int, @Body body: Map<String, String>): SolicitudEntity

    @DELETE("solicitudes/productor/{productorId}")
    suspend fun eliminarSolicitudesDeProductor(@Path("productorId") productorId: Long)

    @GET("ofertas/solicitud/{solicitudId}")
    suspend fun obtenerOfertasPorSolicitud(@Path("solicitudId") solicitudId: Int): List<OfertaEntity>

    @GET("ofertas/aplicador/{aplicadorId}")
    suspend fun obtenerOfertasPorAplicador(@Path("aplicadorId") aplicadorId: Long): List<OfertaEntity>

    @POST("ofertas")
    suspend fun crearOferta(@Body oferta: OfertaEntity): OfertaEntity

    @PATCH("ofertas/{id}/precio")
    suspend fun actualizarPrecioOferta(@Path("id") id: Int, @Body body: Map<String, Int>): OfertaEntity

    @PATCH("ofertas/{id}/estado")
    suspend fun actualizarEstadoOferta(@Path("id") id: Int, @Body body: Map<String, String>): OfertaEntity

    @PATCH("ofertas/{id}/calificar")
    suspend fun calificarOferta(@Path("id") id: Int, @Body body: Map<String, Int>): OfertaEntity
}