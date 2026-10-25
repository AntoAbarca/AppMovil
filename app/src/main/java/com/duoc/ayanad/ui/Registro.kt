package com.duoc.ayanad.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.ayanad.viewmodel.UsuarioFormViewModel

@Composable
fun Registro(viewModel: UsuarioFormViewModel) {

    val form by viewModel.form.collectAsState()
    val errorMessage by viewModel.mensajeError.collectAsState()
    val registroExitoso by viewModel.registroExitoso.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Registro de Usuario", fontSize = 24.sp)

        OutlinedTextField(
            value = form.nombre,
            onValueChange = { viewModel.actualizarCampo(nombre = it) },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = form.apellido,
            onValueChange = { viewModel.actualizarCampo(apellido = it) },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = form.rut,
            onValueChange = { viewModel.actualizarCampo(rut = it) },
            label = { Text("RUT (ej: 123456785)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = form.email,
            onValueChange = { viewModel.actualizarCampo(email = it) },
            label = { Text("Email") },
            placeholder = { Text("ejemplo@gmail.com") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = "+56 9",
                onValueChange = {},
                enabled = false,
                modifier = Modifier.width(100.dp)
            )
            OutlinedTextField(
                value = form.telefono,
                onValueChange = { if (it.length <= 8) viewModel.actualizarCampo(telefono = it) },
                label = { Text("8 dígitos") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        errorMessage?.let { error ->
            Text(text = error, color = Color.Red, fontSize = 14.sp)
        }

        // Muestra mensaje de éxito si se guardó correctamente en Room
        if (registroExitoso) {
            Text(text = "¡Usuario guardado con éxito en SQLite!", color = Color.Green, fontSize = 14.sp)
        }

        val context = LocalContext.current

        Button(
            onClick = { viewModel.submitForm(context) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
        }
    }
}