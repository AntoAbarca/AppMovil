package com.duoc.ayanad.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SolicitudDao {
    @Query("SELECT * FROM solicitudes ORDER BY id DESC")
    fun getAll(): List<SolicitudEntity>

    @Query("SELECT * FROM solicitudes WHERE productorId = :productorId ORDER BY id DESC")
    fun getDeProductor(productorId: Long): List<SolicitudEntity>

    @Query("SELECT * FROM solicitudes WHERE id = :id LIMIT 1")
    fun getPorId(id: Int): SolicitudEntity?

    @Insert
    fun insertar(solicitud: SolicitudEntity): Long

    @Query("UPDATE solicitudes SET estado = :estado WHERE id = :id")
    fun actualizarEstado(id: Int, estado: String)

    @Query("DELETE FROM solicitudes WHERE productorId = :productorId")
    fun eliminarDeProductor(productorId: Long)

    @Query("SELECT COUNT(*) FROM solicitudes WHERE productorId = :productorId AND estado != 'Finalizada'")
    fun contarActivas(productorId: Long): Int
}

@Dao
interface OfertaDao {
    @Query("SELECT * FROM ofertas WHERE solicitudId = :solicitudId ORDER BY precioPorHa ASC")
    fun getDeSolicitud(solicitudId: Int): List<OfertaEntity>

    @Query("SELECT * FROM ofertas WHERE aplicadorId = :aplicadorId ORDER BY id DESC")
    fun getDeAplicador(aplicadorId: Long): List<OfertaEntity>

    @Query("SELECT * FROM ofertas WHERE solicitudId = :solicitudId AND aplicadorId = :aplicadorId LIMIT 1")
    fun getPorSolicitudYAplicador(solicitudId: Int, aplicadorId: Long): OfertaEntity?

    @Query("SELECT * FROM ofertas WHERE id = :id LIMIT 1")
    fun getPorId(id: Int): OfertaEntity?

    @Insert
    fun insertar(oferta: OfertaEntity): Long

    @Query("UPDATE ofertas SET precioPorHa = :precio WHERE id = :id")
    fun actualizarPrecio(id: Int, precio: Int)

    @Query("UPDATE ofertas SET estado = :estado WHERE id = :id")
    fun actualizarEstado(id: Int, estado: String)

    @Query("UPDATE ofertas SET estado = 'Rechazada' WHERE solicitudId = :solicitudId AND id != :aceptadaId")
    fun rechazarOtras(solicitudId: Int, aceptadaId: Int)

    @Query("DELETE FROM ofertas WHERE solicitudId = :solicitudId")
    fun eliminarDeSolicitud(solicitudId: Int)

    @Query("DELETE FROM ofertas WHERE aplicadorId = :aplicadorId")
    fun eliminarDeAplicador(aplicadorId: Long)

    @Query("UPDATE ofertas SET estado = 'Enviada' WHERE solicitudId = :solicitudId AND estado = 'Rechazada'")
    fun reabrirRechazadas(solicitudId: Int)

    @Query("UPDATE ofertas SET calificacion = :estrellas WHERE id = :id")
    fun calificar(id: Int, estrellas: Int)

    @Query("SELECT AVG(calificacion) FROM ofertas WHERE aplicadorId = :aplicadorId AND calificacion > 0")
    fun promedioDeAplicador(aplicadorId: Long): Double?

    @Query("SELECT COUNT(*) FROM ofertas WHERE aplicadorId = :aplicadorId AND calificacion > 0")
    fun contarCalificaciones(aplicadorId: Long): Int

    @Query("SELECT COUNT(*) FROM ofertas WHERE aplicadorId = :aplicadorId")
    fun contarDeAplicador(aplicadorId: Long): Int

    @Query("SELECT COUNT(*) FROM ofertas WHERE aplicadorId = :aplicadorId AND estado = 'Aceptada'")
    fun contarAceptadasDeAplicador(aplicadorId: Long): Int

    @Query("SELECT COUNT(*) FROM ofertas o INNER JOIN solicitudes s ON s.id = o.solicitudId WHERE s.productorId = :productorId AND o.estado = 'Enviada'")
    fun contarNuevasDeProductor(productorId: Long): Int
}
