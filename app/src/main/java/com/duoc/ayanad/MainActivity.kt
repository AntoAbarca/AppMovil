package com.duoc.ayanad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.duoc.ayanad.repository.SesionManager
import com.duoc.ayanad.ui.AppNavegacion
import com.duoc.ayanad.ui.theme.AyanadTheme
import com.duoc.ayanad.viewmodel.AdminViewModel
import com.duoc.ayanad.viewmodel.LoginViewModel
import com.duoc.ayanad.viewmodel.PerfilViewModel
import com.duoc.ayanad.viewmodel.SolicitudViewModel
import com.duoc.ayanad.viewmodel.UsuarioFormViewModel

class MainActivity : ComponentActivity() {

    private val viewModelForm: UsuarioFormViewModel by viewModels()
    private val viewModelLogin: LoginViewModel by viewModels()
    private val viewModelSolicitud: SolicitudViewModel by viewModels()
    private val viewModelPerfil: PerfilViewModel by viewModels()
    private val viewModelAdmin: AdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SesionManager.init(this)
        enableEdgeToEdge()
        setContent {
            AyanadTheme {
                AppNavegacion(
                    loginVM = viewModelLogin,
                    registroVM = viewModelForm,
                    solicitudVM = viewModelSolicitud,
                    perfilVM = viewModelPerfil,
                    adminVM = viewModelAdmin
                )
            }
        }
    }
}

// Pantallas: 1 Login, 2 Registro, 3 InicioProductor, 4 MisSolicitudes, 5 NuevaSolicitud,
// 6 DetalleSolicitudProductor, 7 InicioAplicador, 8 SolicitudesCercanas,
// 9 DetalleSolicitudAplicador, 10 MisOfertas, 11 Perfil.  Ver ui/AppNavegacion.kt
