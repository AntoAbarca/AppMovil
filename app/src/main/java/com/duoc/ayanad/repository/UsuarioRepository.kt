package com.duoc.ayanad.repository

import com.duoc.ayanad.model.UsuarioDao
import com.duoc.ayanad.model.UsuarioEntity
import java.security.MessageDigest

class UsuarioRepository(private val dao: UsuarioDao) {

    suspend fun obtenerUsuarios(): List<UsuarioEntity> = dao.getAllUsuarios()

    suspend fun guardarUsuario(usuario: UsuarioEntity) {
        dao.insertUsuario(usuario)
    }

    suspend fun obtenerPorId(id: Long): UsuarioEntity? = dao.getPorId(id)

    suspend fun buscarPorLogin(login: String): UsuarioEntity? = dao.getPorLogin(login)

    suspend fun actualizarUsuario(usuario: UsuarioEntity) {
        dao.actualizarUsuario(usuario)
    }

    companion object {
        /** La contraseña nunca se guarda en texto plano. */
        fun hashear(texto: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(("tecmundo:$texto").toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
}
