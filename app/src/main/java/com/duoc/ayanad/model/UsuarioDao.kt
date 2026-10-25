package com.duoc.ayanad.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios")
    fun getAllUsuarios(): List<UsuarioEntity>

    @Insert
    fun insertUsuario(usuario: UsuarioEntity)
}