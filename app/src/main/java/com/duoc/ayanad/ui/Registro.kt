package com.duoc.ayanad.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.ayanad.viewmodel.UsuarioFormViewModel

@Composable
fun Registro(viewModel: UsuarioFormViewModel) {

    val form by viewModel.form.collectAsState()
    val errors by viewModel.errors.collectAsState()
    val registroExitoso by viewModel.registroExitoso.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        // Franja superior azul igual a la de TecMundo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(Color(0xFF2962FF)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Crear tu cuenta",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Formulario con desplazamiento o columnas
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Botones de selección (Cliente / Aplicador)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.onTipoUsuarioChange("Cliente") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (form.tipoUsuario == "Cliente") Color(0xFF2962FF) else Color.LightGray
                    )
                ) {
                    Text("Cliente", color = Color.White)
                }

                Button(
                    onClick = { viewModel.onTipoUsuarioChange("Aplicador") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (form.tipoUsuario == "Aplicador") Color(0xFF2962FF) else Color.LightGray
                    )
                ) {
                    Text("Aplicador", color = Color.White)
                }
            }

            // Campos de texto con validaciones dinámicas
            OutlinedTextField(
                value = form.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre") },
                isError = errors.nombre != null,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            if (errors.nombre != null) {
                Text(text = errors.nombre!!, color = Color.Red, fontSize = 12.sp)
            }

            OutlinedTextField(
                value = form.apellido,
                onValueChange = { viewModel.onApellidoChange(it) },
                label = { Text("Apellido") },
                isError = errors.apellido != null,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            if (errors.apellido != null) {
                Text(text = errors.apellido!!, color = Color.Red, fontSize = 12.sp)
            }

            OutlinedTextField(
                value = form.rut,
                onValueChange = { nuevoTexto ->
                    // Solo permite escribir si no supera los 12 caracteres
                    if (nuevoTexto.length <= 12) {
                        viewModel.onRutChange(nuevoTexto)
                    }
                },
                label = { Text("RUT (ej: 12.345.678-K)") },
                isError = errors.rut != null,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            if (errors.rut != null) {
                Text(text = errors.rut!!, color = Color.Red, fontSize = 12.sp)
            }

            OutlinedTextField(
                value = form.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = errors.email != null,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            if (errors.email != null) {
                Text(text = errors.email!!, color = Color.Red, fontSize = 12.sp)
            }

            OutlinedTextField(
                value = form.telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = { Text("Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = errors.telefono != null,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            if (errors.telefono != null) {
                Text(text = errors.telefono!!, color = Color.Red, fontSize = 12.sp)
            }

            OutlinedTextField(
                value = form.region,
                onValueChange = { viewModel.onRegionChange(it) },
                label = { Text("Región") },
                isError = errors.region != null,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
            if (errors.region != null) {
                Text(text = errors.region!!, color = Color.Red, fontSize = 12.sp)
            }

            // Botón Registrarse
            Button(
                onClick = { viewModel.submit() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF))
            ) {
                Text("Registrarse", color = Color.White, fontSize = 16.sp)
            }

            if (registroExitoso) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "¡Usuario registrado con éxito en la lista!",
                    color = Color(0xFF00C853),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}