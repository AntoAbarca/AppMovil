package com.duoc.ayanad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.duoc.ayanad.ui.Registro
import com.duoc.ayanad.ui.theme.AyanadTheme
import com.duoc.ayanad.viewmodel.UsuarioFormViewModel
import com.duoc.ayanad.ui.Login
import com.duoc.ayanad.viewmodel.LoginViewModel
import com.duoc.ayanad.ui.InicioProductor
import com.duoc.ayanad.ui.InicioAplicador
import com.duoc.ayanad.ui.MisOfertasS
import com.duoc.ayanad.ui.MisSolicitudes

class MainActivity : ComponentActivity() {

    private val viewModelForm = UsuarioFormViewModel()
    private val viewModelLogin = LoginViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AyanadTheme {
                MisOfertasS ()

            }
        }
    }
}

//Login(viewModel = viewModelLogin) llama a la  pantalla 1
//Registro(viewModel = viewModelForm) llama a la pantalla 2
//InicioProductor() a la pantalla 3
//MisSolicitudes() a la pantalla 4
//InicioAplicador() a la  pantalla 7