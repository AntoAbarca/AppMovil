package com.duoc.ayanad.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.duoc.ayanad.model.AppDatabase
import com.duoc.ayanad.repository.SesionManager
import com.duoc.ayanad.viewmodel.AdminViewModel
import com.duoc.ayanad.viewmodel.LoginViewModel
import com.duoc.ayanad.viewmodel.PerfilViewModel
import com.duoc.ayanad.viewmodel.SolicitudViewModel
import com.duoc.ayanad.viewmodel.UsuarioFormViewModel

/*
 * Navegación entre las 11 pantallas (sin librerías extra).
 *
 *  1 login            2 registro
 *  3 inicioProductor  4 misSolicitudes  5 nuevaSolicitud  6 detalleProductor/{id}
 *  7 inicioAplicador  8 cercanas        9 detalleAplicador/{id}  10 misOfertas
 *  11 perfil
 */

private fun rutaInicio(tipo: String) = when (tipo) {
    SesionManager.TIPO_ADMIN -> "administracion"
    "Aplicador" -> "inicioAplicador"
    else -> "inicioProductor"
}

private fun rutaDeArranque(context: Context): String {
    // Si el usuario guardado en la sesión fue eliminado por el administrador, se cierra la sesión
    if (SesionManager.haySesion() && !SesionManager.esAdmin) {
        val existe = AppDatabase.getDatabase(context).usuarioDao().getPorId(SesionManager.usuarioId) != null
        if (!existe) SesionManager.cerrar()
    }
    return if (SesionManager.haySesion()) rutaInicio(SesionManager.tipo) else "login"
}

@Composable
fun AppNavegacion(
    loginVM: LoginViewModel,
    registroVM: UsuarioFormViewModel,
    solicitudVM: SolicitudViewModel,
    perfilVM: PerfilViewModel,
    adminVM: AdminViewModel
) {
    val context = LocalContext.current

    // Pila de pantallas; se guarda para sobrevivir a rotaciones
    var pila by rememberSaveable { mutableStateOf(arrayListOf(rutaDeArranque(context))) }

    fun ir(ruta: String) { pila = ArrayList(pila + ruta) }
    fun volver() { if (pila.size > 1) pila = ArrayList(pila.dropLast(1)) }
    fun reiniciar(ruta: String) { pila = arrayListOf(ruta) }
    fun irAInicio() = reiniciar(rutaInicio(SesionManager.tipo))

    fun llamar(telefono: String) {
        val numero = telefono.filter { it.isDigit() || it == '+' }
        if (numero.isBlank()) {
            Toast.makeText(context, "El aplicador no tiene teléfono registrado", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numero")))
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo abrir el marcador", Toast.LENGTH_SHORT).show()
        }
    }

    BackHandler(enabled = pila.size > 1) { volver() }

    val ruta = pila.last()
    val partes = ruta.split("/")
    val id = partes.getOrNull(1)?.toIntOrNull() ?: 0

    // Botón "home" de la barra inferior en todas las pantallas, salvo login, registro y los menús principales
    val sinHome = partes[0] in listOf("login", "registro", "inicioProductor", "inicioAplicador", "administracion")
    val homeLambda: (() -> Unit)? = if (sinHome) null else ({ irAInicio() })
    val perfilLambda: (() -> Unit)? =
        if (partes[0] in listOf("login", "registro", "perfil", "administracion")) null else ({ ir("perfil") })

    CompositionLocalProvider(
        LocalIrAInicio provides homeLambda,
        LocalIrAPerfil provides perfilLambda
    ) {
    when (partes[0]) {

        // 1
        "login" -> Login(
            viewModel = loginVM,
            onLoginExitoso = {
                loginVM.consumirLogin()
                irAInicio()
            },
            onRecuperarPassword = {
                Toast.makeText(context, "Función no disponible todavía", Toast.LENGTH_SHORT).show()
            },
            onCrearCuenta = { ir("registro") }
        )

        // 2
        "registro" -> Registro(
            viewModel = registroVM,
            onRegistroExitoso = {
                registroVM.reiniciar()
                Toast.makeText(context, "Cuenta creada. Ya puedes iniciar sesión", Toast.LENGTH_LONG).show()
                reiniciar("login")
            },
            onVolver = { volver() }
        )

        // 3
        "inicioProductor" -> {
            val resumen by solicitudVM.resumen.collectAsState()
            LaunchedEffect(ruta) { solicitudVM.cargarResumenProductor() }
            InicioProductor(
                solicitudesActivas = resumen.first,
                ofertasNuevas = resumen.second,
                onNuevaSolicitud = { ir("nuevaSolicitud") },
                onMisSolicitudes = { ir("misSolicitudes") },
                onMiPerfil = { ir("perfil") }
            )
        }

        // 4
        "misSolicitudes" -> {
            val lista by solicitudVM.misSolicitudes.collectAsState()
            LaunchedEffect(ruta) { solicitudVM.cargarMisSolicitudes() }
            MisSolicitudes(
                solicitudes = lista,
                onVolver = { volver() },
                onNuevaSolicitudClick = { ir("nuevaSolicitud") },
                onSolicitudClick = { ir("detalleProductor/${it.id}") }
            )
        }

        // 5
        "nuevaSolicitud" -> NuevaSolicitud(
            viewModel = solicitudVM,
            onVolver = { volver() },
            onPublicada = {
                // reemplaza esta pantalla por "Mis solicitudes"
                pila = ArrayList(pila.dropLast(1) + "misSolicitudes")
            }
        )

        // 6
        "detalleProductor" -> DetalleSolicitudProductor(
            viewModel = solicitudVM,
            solicitudId = id,
            onVolver = { volver() },
            onComunicarse = { oferta -> llamar(oferta.telefono) }
        )

        // 7
        "inicioAplicador" -> {
            val resumen by solicitudVM.resumen.collectAsState()
            LaunchedEffect(ruta) { solicitudVM.cargarResumenAplicador() }
            InicioAplicador(
                ofertasEnviadas = resumen.first,
                ofertasAceptadas = resumen.second,
                onSolicitudesCercanas = { ir("cercanas") },
                onMisOfertas = { ir("misOfertas") },
                onMiPerfil = { ir("perfil") }
            )
        }

        // 8
        "cercanas" -> {
            LaunchedEffect(ruta) { solicitudVM.cargarCercanas() }
            SolicitudesCercanas(
                viewModel = solicitudVM,
                onVolver = { volver() },
                onSolicitudClick = { ir("detalleAplicador/${it.id}") }
            )
        }

        // 9
        "detalleAplicador" -> DetalleSolicitudAplicador(
            viewModel = solicitudVM,
            solicitudId = id,
            onVolver = { volver() },
            onOfertaEnviada = {
                Toast.makeText(context, "Oferta enviada", Toast.LENGTH_SHORT).show()
                volver()
            }
        )

        // 10
        "misOfertas" -> {
            val lista by solicitudVM.misOfertas.collectAsState()
            LaunchedEffect(ruta) { solicitudVM.cargarMisOfertas() }
            MisOfertas(
                ofertas = lista,
                onVolver = { volver() },
                onOfertaClick = { ir("detalleAplicador/${it.oferta.solicitudId}") }
            )
        }

        // Administración (solo la cuenta admin)
        "administracion" -> Administracion(
            viewModel = adminVM,
            onCerrarSesion = {
                SesionManager.cerrar()
                reiniciar("login")
            }
        )

        // 11
        "perfil" -> Perfil(
            viewModel = perfilVM,
            onVolver = { volver() },
            onInicio = { irAInicio() },
            onCerrarSesion = {
                SesionManager.cerrar()
                reiniciar("login")
            }
        )
    }
    }
}
