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

    // Claves de los avisos ya mostrados (para no repetir la misma notificación)
    const val AVISO_OFERTAS = "ofertas"            // último id de oferta avisado al productor
    const val AVISO_ACEPTADAS = "aceptadas"        // ofertas aceptadas ya avisadas al aplicador
    const val AVISO_DISPONIBLES = "disponibles"    // última cantidad de ofertas disponibles avisada

    private var prefs: SharedPreferences? = null
    private var prefsAvisos: SharedPreferences? = null   // aparte de la sesión: sobrevive al cerrar sesión

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences("sesion_tecmundo", Context.MODE_PRIVATE)
        }
        if (prefsAvisos == null) {
            prefsAvisos = context.applicationContext.getSharedPreferences("avisos_tecmundo", Context.MODE_PRIVATE)
        }
    }

    val usuarioId: Long get() = prefs?.getLong("id", -1L) ?: -1L
    val tipo: String get() = prefs?.getString("tipo", "") ?: ""
    val nombre: String get() = prefs?.getString("nombre", "") ?: ""
    val telefono: String get() = prefs?.getString("telefono", "") ?: ""

    /** Último valor avisado para este usuario (-1 si nunca se avisó). */
    fun ultimoAviso(usuarioId: Long, clave: String): Int =
        prefsAvisos?.getInt("${clave}_$usuarioId", -1) ?: -1

    fun guardarAviso(usuarioId: Long, clave: String, valor: Int) {
        prefsAvisos?.edit()?.putInt("${clave}_$usuarioId", valor)?.apply()
    }

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
