package com.duoc.ayanad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.duoc.ayanad.ui.Registro
import com.duoc.ayanad.ui.theme.AyanadTheme
import com.duoc.ayanad.viewmodel.UsuarioFormViewModel

class MainActivity : ComponentActivity() {

    private val viewModelForm = UsuarioFormViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AyanadTheme {
                Registro(viewModel = viewModelForm)
            }
        }
    }
}
