package com.duoc.ayanad.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import com.duoc.ayanad.ui.navigation.NavegacionItem
import com.duoc.ayanad.ui.navigation.Rutas

class NavegacionRepository {

    // Maneja la lógica de ruteo a partir del tipo de usuario en el item de navegación.
    fun obtenerMenu(tipoUsuario: String): List<NavegacionItem> {
        // Opciones de cada usuario + perfil global abajo.
        val items = when (tipoUsuario) {
            "Productor" -> listOf(
                NavegacionItem(
                    titulo = "Inicio",
                    ruta = Rutas.INICIO_PRODUCTOR,
                    icono = Icons.Default.Home
                ),
                NavegacionItem(
                    titulo = "Nueva Solicitud",
                    ruta = Rutas.CREAR_SOLICITUD,
                    icono = Icons.Default.AddCircle
                ),
                NavegacionItem(
                    titulo = "Mis Solicitudes",
                    ruta = Rutas.MIS_SOLICITUDES,
                    icono = Icons.AutoMirrored.Filled.List
                )
            )

            "Aplicador" -> listOf(
                NavegacionItem(
                    titulo = "Inicio",
                    ruta = Rutas.INICIO_APLICADOR,
                    icono = Icons.Default.Home
                ),
                NavegacionItem(
                    titulo = "Buscar Cerca",
                    ruta = Rutas.SOLICITUDES_CERCA,
                    icono = Icons.Default.LocationOn
                ),
                NavegacionItem(
                    titulo = "Mis Ofertas",
                    ruta = Rutas.MIS_OFERTAS,
                    icono = Icons.Default.Check
                )
            )

            // El admin ve su vista de usuarios. No tiene perfil porque su cuenta no está en la base de datos.
            SesionManager.TIPO_ADMIN -> return listOf(
                NavegacionItem(
                    titulo = "Usuarios",
                    ruta = Rutas.ADMIN,
                    icono = Icons.Default.Settings
                )
            )

            else -> return emptyList()
        }

        // Devuelve los items por tipo de usuario y le suma el perfil.
        return items + NavegacionItem(
            titulo = "Mi Perfil",
            ruta = Rutas.PERFIL,
            icono = Icons.Default.Person
        )
    }

    /** Primera pantalla de cada rol después de iniciar sesión. */
    fun rutaInicio(tipoUsuario: String): String = when (tipoUsuario) {
        SesionManager.TIPO_ADMIN -> Rutas.ADMIN
        "Aplicador" -> Rutas.INICIO_APLICADOR
        else -> Rutas.INICIO_PRODUCTOR
    }

    /** La barra inferior se muestra en todas las pantallas menos login y registro. */
    fun debeMostrarBarra(ruta: String): Boolean = ruta != Rutas.LOGIN && ruta != Rutas.REGISTRO

    /** Qué botón de la barra queda marcado: las pantallas de detalle marcan su sección. */
    fun seccionDe(ruta: String): String = when (ruta) {
        Rutas.DETALLE_PRODUCTOR -> Rutas.MIS_SOLICITUDES
        Rutas.DETALLE_APLICADOR -> Rutas.SOLICITUDES_CERCA
        else -> ruta
    }
}
