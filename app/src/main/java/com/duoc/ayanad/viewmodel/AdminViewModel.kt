package com.duoc.ayanad.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.duoc.ayanad.model.AppDatabase
import com.duoc.ayanad.model.UsuarioEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)

    private val _usuarios = MutableStateFlow<List<UsuarioEntity>>(emptyList())
    val usuarios: StateFlow<List<UsuarioEntity>> = _usuarios

    fun cargar() {
        _usuarios.value = db.usuarioDao().getAllUsuarios()
    }

    /** Elimina al usuario y limpia todo lo que dependía de él. */
    fun eliminar(usuario: UsuarioEntity) {
        val solicitudDao = db.solicitudDao()
        val ofertaDao = db.ofertaDao()

        // Si era productor: se borran sus solicitudes y las ofertas que recibieron
        solicitudDao.getDeProductor(usuario.id).forEach { ofertaDao.eliminarDeSolicitud(it.id) }
        solicitudDao.eliminarDeProductor(usuario.id)

        // Si era aplicador: se borran sus ofertas y se reabren las solicitudes que afectaban
        val afectadas = ofertaDao.getDeAplicador(usuario.id).map { it.solicitudId }.distinct()
        ofertaDao.eliminarDeAplicador(usuario.id)
        afectadas.forEach { solicitudId ->
            val restantes = ofertaDao.getDeSolicitud(solicitudId)
            if (restantes.none { it.estado == "Aceptada" }) {
                ofertaDao.reabrirRechazadas(solicitudId)
                solicitudDao.actualizarEstado(
                    solicitudId,
                    if (restantes.isEmpty()) "Pendiente" else "Con ofertas"
                )
            }
        }

        // Foto de perfil guardada en el teléfono
        File(getApplication<Application>().filesDir, "perfil_${usuario.id}.jpg").delete()

        db.usuarioDao().eliminar(usuario.id)
        cargar()
    }
}
