package com.duoc.ayanad.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.ayanad.model.AppDatabase
import com.duoc.ayanad.model.UsuarioEntity
import com.duoc.ayanad.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Solo guarda la lista; eliminar usuarios (y todo lo que dependía de ellos) lo hace UsuarioRepository.
class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UsuarioRepository(AppDatabase.getDatabase(application), application.filesDir)

    private val _usuarios = MutableStateFlow<List<UsuarioEntity>>(emptyList())
    val usuarios: StateFlow<List<UsuarioEntity>> = _usuarios

    fun cargar() {
        viewModelScope.launch { _usuarios.value = repository.obtenerUsuarios() }
    }

    fun eliminar(usuario: UsuarioEntity) {
        viewModelScope.launch {
            repository.eliminarUsuario(usuario)
            _usuarios.value = repository.obtenerUsuarios()
        }
    }
}
