package com.duoc.ayanad.repository

import android.content.Context
import android.content.SharedPreferences
import com.duoc.ayanad.model.UsuarioEntity

/** Guarda quién inició sesión (sobrevive al cierre de la app). */
object SesionManager {
    // Cuenta de administración fija: NO está en la base de datos, por eso no se puede
    // listar, editar ni eliminar desde la app.
    const val ADMIN_EMAIL = "admin@admin.cl"
    const val ADMIN_PASSWORD = "admin"
    const val TIPO_ADMIN = "Administrador"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences("sesion_tecmundo", Context.MODE_PRIVATE)
        }
    }

    val usuarioId: Long get() = prefs?.getLong("id", -1L) ?: -1L
    val tipo: String get() = prefs?.getString("tipo", "") ?: ""
    val nombre: String get() = prefs?.getString("nombre", "") ?: ""
    val telefono: String get() = prefs?.getString("telefono", "") ?: ""

    fun haySesion(): Boolean = usuarioId != -1L

    fun iniciar(u: UsuarioEntity) {
        val e = prefs?.edit() ?: return
        e.putLong("id", u.id)
        e.putString("tipo", u.tipoUsuario)
        e.putString("nombre", "${u.nombre} ${u.apellido}")
        e.putString("telefono", u.telefono)
        e.apply()
    }

    fun iniciarAdmin() {
        val e = prefs?.edit() ?: return
        e.putLong("id", 0L)
        e.putString("tipo", TIPO_ADMIN)
        e.putString("nombre", "Administrador")
        e.putString("telefono", "")
        e.apply()
    }

    val esAdmin: Boolean get() = haySesion() && tipo == TIPO_ADMIN

    fun cerrar() {
        prefs?.edit()?.clear()?.apply()
    }
}
