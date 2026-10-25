package com.duoc.ayanad.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val apellido: String,
    val rut: String,
    val email: String,
    val telefono: String,
    val region: String,
    val tipoUsuario: String,
    val especialidad: String
)