package com.duoc.ayanad.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios")
    fun getAllUsuarios(): List<UsuarioEntity>

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    fun getPorId(id: Long): UsuarioEntity?

    // El login acepta correo o RUT
    @Query("SELECT * FROM usuarios WHERE lower(email) = lower(:login) OR rut = :login LIMIT 1")
    fun getPorLogin(login: String): UsuarioEntity?

    @Insert
    fun insertUsuario(usuario: UsuarioEntity)

    @Query("DELETE FROM usuarios WHERE id = :id")
    fun eliminar(id: Long)

    @Update
    fun actualizarUsuario(usuario: UsuarioEntity)
}
