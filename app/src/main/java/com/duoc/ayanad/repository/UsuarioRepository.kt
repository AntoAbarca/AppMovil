package com.duoc.ayanad.repository

import com.duoc.ayanad.model.AppDatabase
import com.duoc.ayanad.model.LoginForm
import com.duoc.ayanad.model.LoginFormError
import com.duoc.ayanad.model.PerfilForm
import com.duoc.ayanad.model.PerfilFormError
import com.duoc.ayanad.model.ResultadoPerfil
import com.duoc.ayanad.model.UsuarioEntity
import com.duoc.ayanad.model.UsuarioForm
import com.duoc.ayanad.model.UsuarioRequest
import com.duoc.ayanad.remote.RetrofitInstance
import java.io.File
import java.security.MessageDigest
import java.util.Locale

class UsuarioRepository(db: AppDatabase, private val carpetaFotos: File) {

    private val dao = db.usuarioDao()
    private val solicitudDao = db.solicitudDao()
    private val ofertaDao = db.ofertaDao()

    private val prefijoTelefono = "+56 9 "
    private val soloLetras = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,}$".toRegex()
    private val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    suspend fun obtenerUsuarios(): List<UsuarioEntity> {
        return try {
            val usuariosRemotos = RetrofitInstance.api.obtenerUsuarios()
            usuariosRemotos.forEach { dao.insertUsuario(it) }
            usuariosRemotos
        } catch (e: Exception) {
            dao.getAllUsuarios()
        }
    }

    suspend fun obtenerPorId(id: Long): UsuarioEntity? = dao.getPorId(id)

    suspend fun buscarPorLogin(login: String): UsuarioEntity? = dao.getPorLogin(login)

    fun validarLogin(f: LoginForm): LoginFormError = LoginFormError(
        usuario = if (f.usuario.isBlank()) "El usuario es obligatorio" else null,
        password = if (f.password.isBlank()) "La contraseña es obligatoria" else null
    )

    suspend fun iniciarSesion(f: LoginForm): Boolean {
        val exito = autenticar(f)
        if (exito) SesionManager.guardarAviso(SesionManager.usuarioId, SesionManager.AVISO_DISPONIBLES, -1)  // al entrar se vuelve a avisar lo disponible
        return exito
    }

    private suspend fun autenticar(f: LoginForm): Boolean {
        val login = f.usuario.trim()

        if (login.equals(SesionManager.ADMIN_EMAIL, ignoreCase = true) &&
            f.password == SesionManager.ADMIN_PASSWORD
        ) {
            SesionManager.iniciarAdmin()
            return true
        }

        return try {
            val credenciales = mapOf("login_text" to login, "password" to hashear(f.password))
            val usuarioRemoto = RetrofitInstance.api.loginUsuario(credenciales)

            dao.insertUsuario(usuarioRemoto)
            SesionManager.iniciar(usuarioRemoto)
            true
        } catch (e: Exception) {
            val usuarioLocal = dao.getPorLogin(login) ?: return false
            if (usuarioLocal.password != hashear(f.password)) return false
            SesionManager.iniciar(usuarioLocal)
            true
        }
    }

    fun validarRegistro(f: UsuarioForm): String? {
        if (f.tipoUsuario.isBlank()) return "Selecciona si eres productor o aplicador."
        if (!f.nombre.matches(soloLetras)) return "El nombre debe tener al menos 2 letras y sin números."
        if (!f.apellido.matches(soloLetras)) return "El apellido debe tener al menos 2 letras y sin números."
        if (f.rut.length !in 9..12) return "El RUT debe tener entre 9 y 12 caracteres."
        if (!f.email.matches(emailRegex)) return "El correo debe tener un formato válido (ej: ejemplo@gmail.com)."
        if (f.telefono.length != 8 || !f.telefono.all { it.isDigit() }) return "El teléfono debe contener exactamente 8 dígitos."
        if (f.region.isBlank()) return "Selecciona una región."
        if (f.tipoUsuario == "Aplicador" && f.especialidad.isBlank()) return "Selecciona una especialidad."
        if (f.password.length < 6) return "La contraseña debe tener al menos 6 caracteres."
        if (f.email.trim().equals(SesionManager.ADMIN_EMAIL, ignoreCase = true)) return "Ese correo está reservado. Usa otro."
        return null
    }

    suspend fun registrar(f: UsuarioForm): String? {
        validarRegistro(f)?.let { return it }
        if (dao.getPorLogin(f.email.trim()) != null) return "Ya existe una cuenta con ese correo."
        if (dao.getPorLogin(f.rut.trim()) != null) return "Ya existe una cuenta con ese RUT."

        val nuevoUsuario = UsuarioEntity(
            nombre = f.nombre.trim(),
            apellido = f.apellido.trim(),
            rut = f.rut.trim(),
            email = f.email.trim(),
            telefono = prefijoTelefono + f.telefono,
            region = f.region,
            tipoUsuario = f.tipoUsuario,
            especialidad = f.especialidad,
            password = hashear(f.password)
        )

        return try {
            val peticionXano = UsuarioRequest(
                nombre = nuevoUsuario.nombre,
                apellido = nuevoUsuario.apellido,
                rut = nuevoUsuario.rut,
                email = nuevoUsuario.email,
                telefono = nuevoUsuario.telefono,
                region = nuevoUsuario.region,
                tipoUsuario = nuevoUsuario.tipoUsuario,
                especialidad = nuevoUsuario.especialidad,
                password = nuevoUsuario.password
            )

            val respuesta = RetrofitInstance.api.registrarUsuario(peticionXano)

            if (respuesta.isSuccessful) {
                val usuarioRemoto = respuesta.body()
                if (usuarioRemoto != null) {
                    dao.insertUsuario(usuarioRemoto)
                } else {
                    dao.insertUsuario(nuevoUsuario)
                }
                null // Registro exitoso
            } else {
                // Muestra el código HTTP y el motivo exacto si Xano rechaza la petición
                val detalleError = respuesta.errorBody()?.string() ?: "Sin detalle"
                "Error HTTP ${respuesta.code()}: $detalleError"
            }
        } catch (e: Exception) {
            "Fallo de red: ${e.message}"
        }
    }

    fun aceptaTelefono(v: String): Boolean = v.all { it.isDigit() } && v.length <= 8
    fun aceptaRut(v: String): Boolean = v.length <= 12
    fun esAplicador(u: UsuarioEntity?): Boolean = u?.tipoUsuario == "Aplicador"

    fun clasificacionDe(u: UsuarioEntity?): String {
        if (u == null || !esAplicador(u)) return "Sin calificaciones"
        val cantidad = ofertaDao.contarCalificaciones(u.id)
        if (cantidad == 0) return "Sin calificaciones"
        val promedio = ofertaDao.promedioDeAplicador(u.id) ?: 0.0
        return String.format(Locale.US, "%.1f / 5  (%d)", promedio, cantidad)
    }

    fun perfilFormDe(u: UsuarioEntity): PerfilForm = PerfilForm(
        nombre = u.nombre,
        apellido = u.apellido,
        rut = u.rut,
        email = u.email,
        telefono = u.telefono.removePrefix(prefijoTelefono),
        region = u.region
    )

    fun validarPerfil(f: PerfilForm): PerfilFormError = PerfilFormError(
        nombre = if (!f.nombre.matches(soloLetras)) "Mínimo 2 letras, sin números" else null,
        apellido = if (!f.apellido.matches(soloLetras)) "Mínimo 2 letras, sin números" else null,
        rut = if (f.rut.length !in 9..12) "El RUT debe tener entre 9 y 12 caracteres" else null,
        email = if (!f.email.matches(emailRegex)) "Formato válido: ejemplo@gmail.com" else null,
        telefono = if (f.telefono.length != 8) "Debe tener exactamente 8 dígitos" else null,
        region = if (f.region.isBlank()) "Selecciona una región" else null
    )

    suspend fun guardarPerfil(actual: UsuarioEntity, f: PerfilForm): ResultadoPerfil {
        val errores = validarPerfil(f)
        if (!errores.isValid()) return ResultadoPerfil(null, errores)

        val otroCorreo = dao.getPorLogin(f.email.trim())
        if (otroCorreo != null && otroCorreo.id != actual.id) {
            return ResultadoPerfil(null, errores.copy(email = "Ese correo ya está en uso"))
        }
        val otroRut = dao.getPorLogin(f.rut.trim())
        if (otroRut != null && otroRut.id != actual.id) {
            return ResultadoPerfil(null, errores.copy(rut = "Ese RUT ya está en uso"))
        }

        val actualizado = actual.copy(
            nombre = f.nombre.trim(),
            apellido = f.apellido.trim(),
            rut = f.rut.trim(),
            email = f.email.trim(),
            telefono = prefijoTelefono + f.telefono,
            region = f.region
        )

        return try {
            val actualizadoRemoto = RetrofitInstance.api.actualizarUsuario(actualizado.id, actualizado)
            dao.actualizarUsuario(actualizadoRemoto)
            SesionManager.iniciar(actualizadoRemoto)
            ResultadoPerfil(actualizadoRemoto, errores)
        } catch (e: Exception) {
            dao.actualizarUsuario(actualizado)
            SesionManager.iniciar(actualizado)
            ResultadoPerfil(actualizado, errores)
        }
    }

    suspend fun eliminarUsuario(usuario: UsuarioEntity) {

        try {
            RetrofitInstance.api.eliminarUsuario(usuario.id)
        } catch (e: Exception) {

            e.printStackTrace()
        }

        // 2. Limpieza local (cascada)
        solicitudDao.getDeProductor(usuario.id).forEach { ofertaDao.eliminarDeSolicitud(it.id) }
        solicitudDao.eliminarDeProductor(usuario.id)

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

        File(carpetaFotos, "perfil_${usuario.id}.jpg").delete()
        dao.eliminar(usuario.id)
    }

    companion object {
        fun hashear(texto: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(("tecmundo:$texto").toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
}