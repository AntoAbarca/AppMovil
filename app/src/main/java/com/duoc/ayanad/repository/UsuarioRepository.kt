package com.duoc.ayanad.repository

import com.duoc.ayanad.model.UsuarioDao
import com.duoc.ayanad.model.UsuarioEntity

class UsuarioRepository(private val dao: UsuarioDao) {

    suspend fun obtenerUsuarios(): List<UsuarioEntity> {
        return dao.getAllUsuarios()
    }
    suspend fun guardarUsuario(usuario: UsuarioEntity) {
        dao.insertUsuario(usuario)
    }
}