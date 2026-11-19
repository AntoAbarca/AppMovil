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
import com.duoc.ayanad.repository.NavegacionRepository
import com.duoc.ayanad.repository.SesionManager
import com.duoc.ayanad.ui.navigation.Rutas
import com.duoc.ayanad.viewmodel.AdminViewModel
import com.duoc.ayanad.viewmodel.LoginViewModel
import com.duoc.ayanad.viewmodel.PerfilViewModel
import com.duoc.ayanad.viewmodel.SolicitudViewModel
import com.duoc.ayanad.viewmodel.UsuarioFormViewModel

/*
 * Navegación entre las pantallas (sin librerías extra).
 *
 * - Los nombres de las pantallas están en ui/navigation/Rutas.kt
 * - Qué botones tiene la barra inferior (según el tipo de usuario) lo decide NavegacionRepository
 * - Aquí solo se muestra la pantalla que corresponde a la ruta actual
 */

private fun rutaDeArranque(context: Context, navegacion: NavegacionRepository): String {
    // Si el usuario guardado en la sesión fue eliminado por el administrador, se cierra la sesión
    if (SesionManager.haySesion() && !SesionManager.esAdmin) {
        val existe = AppDatabase.getDatabase(context).usuarioDao().getPorId(SesionManager.usuarioId) != null
        if (!existe) SesionManager.cerrar()
    }
    return if (SesionManager.haySesion()) navegacion.rutaInicio(SesionManager.tipo) else Rutas.LOGIN
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
    val navegacion = remember { NavegacionRepository() }

    // Pila de pantallas; se guarda para sobrevivir a rotaciones
    var pila by rememberSaveable { mutableStateOf(arrayListOf(rutaDeArranque(context, navegacion))) }

    fun ir(ruta: String) { pila = ArrayList(pila + ruta) }
    fun volver() { if (pila.size > 1) pila = ArrayList(pila.dropLast(1)) }
    fun reiniciar(ruta: String) { pila = arrayListOf(ruta) }
    fun irAInicio() = reiniciar(navegacion.rutaInicio(SesionManager.tipo))

    // Botones de la barra inferior: la pila queda [inicio, sección] y "atrás" vuelve al inicio
    fun irASeccion(ruta: String) {
        val inicio = navegacion.rutaInicio(SesionManager.tipo)
        pila = if (ruta == inicio) arrayListOf(inicio) else arrayListOf(inicio, ruta)
    }

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
    val base = partes[0]                                   // nombre de la pantalla, sin el id
    val id = partes.getOrNull(1)?.toIntOrNull() ?: 0

    // Menú de la barra inferior: lo decide el repositorio según el tipo de usuario
    val menu: MenuNavegacion? = if (navegacion.debeMostrarBarra(base)) {
        MenuNavegacion(
            items = navegacion.obtenerMenu(SesionManager.tipo),
            rutaActual = navegacion.seccionDe(base),
            onNavegar = { irASeccion(it) }
        )
    } else null

    CompositionLocalProvider(LocalMenuNavegacion provides menu) {
        when (base) {

            Rutas.LOGIN -> Login(
                viewModel = loginVM,
                onLoginExitoso = {
                    loginVM.consumirLogin()
                    irAInicio()
                },
                onRecuperarPassword = {
                    Toast.makeText(context, "Función no disponible todavía", Toast.LENGTH_SHORT).show()
                },
                onCrearCuenta = { ir(Rutas.REGISTRO) }
            )

            Rutas.REGISTRO -> Registro(
                viewModel = registroVM,
                onRegistroExitoso = {
                    registroVM.reiniciar()
                    Toast.makeText(context, "Cuenta creada. Ya puedes iniciar sesión", Toast.LENGTH_LONG).show()
                    reiniciar(Rutas.LOGIN)
                },
                onVolver = { volver() }
            )

            // ---------- Productor ----------
            Rutas.INICIO_PRODUCTOR -> {
                val resumen by solicitudVM.resumen.collectAsState()
                LaunchedEffect(ruta) { solicitudVM.cargarResumenProductor() }
                InicioProductor(
                    solicitudesActivas = resumen.first,
                    ofertasNuevas = resumen.second,
                    onNuevaSolicitud = { ir(Rutas.CREAR_SOLICITUD) },
                    onMisSolicitudes = { ir(Rutas.MIS_SOLICITUDES) },
                    onMiPerfil = { ir(Rutas.PERFIL) }
                )
            }

            Rutas.MIS_SOLICITUDES -> {
                val lista by solicitudVM.misSolicitudes.collectAsState()
                LaunchedEffect(ruta) { solicitudVM.cargarMisSolicitudes() }
                MisSolicitudes(
                    solicitudes = lista,
                    onVolver = { volver() },
                    onNuevaSolicitudClick = { ir(Rutas.CREAR_SOLICITUD) },
                    onSolicitudClick = { ir(Rutas.detalleProductor(it.id)) }
                )
            }

            Rutas.CREAR_SOLICITUD -> NuevaSolicitud(
                viewModel = solicitudVM,
                onVolver = { volver() },
                onPublicada = {
                    // reemplaza esta pantalla por "Mis solicitudes"
                    pila = ArrayList(pila.dropLast(1) + Rutas.MIS_SOLICITUDES)
                }
            )

            Rutas.DETALLE_PRODUCTOR -> DetalleSolicitudProductor(
                viewModel = solicitudVM,
                solicitudId = id,
                onVolver = { volver() },
                onComunicarse = { oferta -> llamar(oferta.telefono) }
            )

            // ---------- Aplicador ----------
            Rutas.INICIO_APLICADOR -> {
                val resumen by solicitudVM.resumen.collectAsState()
                LaunchedEffect(ruta) { solicitudVM.cargarResumenAplicador() }
                InicioAplicador(
                    ofertasEnviadas = resumen.first,
                    ofertasAceptadas = resumen.second,
                    onSolicitudesCercanas = { ir(Rutas.SOLICITUDES_CERCA) },
                    onMisOfertas = { ir(Rutas.MIS_OFERTAS) },
                    onMiPerfil = { ir(Rutas.PERFIL) }
                )
            }

            Rutas.SOLICITUDES_CERCA -> {
                LaunchedEffect(ruta) { solicitudVM.cargarCercanas() }
                SolicitudesCercanas(
                    viewModel = solicitudVM,
                    onVolver = { volver() },
                    onSolicitudClick = { ir(Rutas.detalleAplicador(it.id)) }
                )
            }

            Rutas.DETALLE_APLICADOR -> DetalleSolicitudAplicador(
                viewModel = solicitudVM,
                solicitudId = id,
                onVolver = { volver() },
                onOfertaEnviada = {
                    Toast.makeText(context, "Oferta enviada", Toast.LENGTH_SHORT).show()
                    volver()
                }
            )

            Rutas.MIS_OFERTAS -> {
                val lista by solicitudVM.misOfertas.collectAsState()
                LaunchedEffect(ruta) { solicitudVM.cargarMisOfertas() }
                MisOfertas(
                    ofertas = lista,
                    onVolver = { volver() },
                    onOfertaClick = { ir(Rutas.detalleAplicador(it.oferta.solicitudId)) }
                )
            }

            // ---------- Todos ----------
            Rutas.PERFIL -> Perfil(
                viewModel = perfilVM,
                onVolver = { volver() },
                onInicio = { irAInicio() },
                onCerrarSesion = {
                    SesionManager.cerrar()
                    reiniciar(Rutas.LOGIN)
                }
            )

            // ---------- Administrador ----------
            Rutas.ADMIN -> Administracion(
                viewModel = adminVM,
                onCerrarSesion = {
                    SesionManager.cerrar()
                    reiniciar(Rutas.LOGIN)
                }
            )
        }
    }
}
