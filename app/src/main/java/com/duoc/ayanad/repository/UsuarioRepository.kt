package com.duoc.ayanad.repository

import com.duoc.ayanad.model.UsuarioForm

class UsuarioRepository {

    val listaUsuario = mutableListOf<UsuarioForm>()

    fun guardarUsuario(usuario: UsuarioForm): Boolean {
        return listaUsuario.add(usuario)
    }

    fun obtenerUsuarios(): List<UsuarioForm> {
        return listaUsuario
    }
}