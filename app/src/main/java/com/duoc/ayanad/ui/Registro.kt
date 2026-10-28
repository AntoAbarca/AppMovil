package com.duoc.ayanad.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.ayanad.viewmodel.UsuarioFormViewModel

private val AzulTecMundo = Color(0xFF2962FF)

private val regiones = listOf(
    "Arica y Parinacota",
    "Tarapacá",
    "Antofagasta",
    "Atacama",
    "Coquimbo",
    "Valparaíso",
    "Región Metropolitana",
    "O'Higgins",
    "Maule",
    "Ñuble",
    "Biobío",
    "La Araucanía",
    "Los Ríos",
    "Los Lagos",
    "Aysén",
    "Magallanes"
)

private val especialidades = listOf("Drones", "Fumigar")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registro(viewModel: UsuarioFormViewModel) {

    val form by viewModel.form.collectAsState()
    val errorMessage by viewModel.mensajeError.collectAsState()
    val registroExitoso by viewModel.registroExitoso.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // Franja superior azul
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(AzulTecMundo),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Crear tu cuenta",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Selección de perfil
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.actualizarCampo(tipoUsuario = "Productor", especialidad = "") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (form.tipoUsuario == "Productor") AzulTecMundo else Color.LightGray
                    )
                ) {
                    Text("Soy un productor", color = Color.White)
                }

                Button(
                    onClick = { viewModel.actualizarCampo(tipoUsuario = "Aplicador") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (form.tipoUsuario == "Aplicador") AzulTecMundo else Color.LightGray
                    )
                ) {
                    Text("Soy un aplicador", color = Color.White)
                }
            }

            // Nombre
            OutlinedTextField(
                value = form.nombre,
                onValueChange = { viewModel.actualizarCampo(nombre = it) },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Apellido
            OutlinedTextField(
                value = form.apellido,
                onValueChange = { viewModel.actualizarCampo(apellido = it) },
                label = { Text("Apellido") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // RUT
            OutlinedTextField(
                value = form.rut,
                onValueChange = { if (it.length <= 12) viewModel.actualizarCampo(rut = it) },
                label = { Text("RUT (ej: 12.345.678-K)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Email
            OutlinedTextField(
                value = form.email,
                onValueChange = { viewModel.actualizarCampo(email = it) },
                label = { Text("Email") },
                placeholder = { Text("ejemplo@gmail.com") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Teléfono con prefijo alineado
            OutlinedTextField(
                value = form.telefono,
                onValueChange = {
                    if (it.length <= 8 && it.all { c -> c.isDigit() }) {
                        viewModel.actualizarCampo(telefono = it)
                    }
                },
                prefix = { Text("+56 9 ", color = Color.Black) },
                label = { Text("Teléfono (8 dígitos)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Región (desplegable)
            var regionExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = regionExpanded,
                onExpandedChange = { regionExpanded = !regionExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = form.region,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Región") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = regionExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = regionExpanded,
                    onDismissRequest = { regionExpanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    regiones.forEach { region ->
                        DropdownMenuItem(
                            text = { Text(text = region, color = Color.Black) },
                            onClick = {
                                viewModel.actualizarCampo(region = region)
                                regionExpanded = false
                            }
                        )
                    }
                }
            }

            // Especialidad: solo aparece si es Aplicador
            if (form.tipoUsuario == "Aplicador") {
                var espExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = espExpanded,
                    onExpandedChange = { espExpanded = !espExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = form.especialidad,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Especialidad") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = espExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = espExpanded,
                        onDismissRequest = { espExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        especialidades.forEach { esp ->
                            DropdownMenuItem(
                                text = { Text(text = esp, color = Color.Black) },
                                onClick = {
                                    viewModel.actualizarCampo(especialidad = esp)
                                    espExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Mensaje de error
            errorMessage?.let { error ->
                Text(text = error, color = Color.Red, fontSize = 14.sp)
            }

            // Botón Registrarse
            Button(
                onClick = { viewModel.submitForm(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulTecMundo)
            ) {
                Text("Registrarse", color = Color.White, fontSize = 16.sp)
            }

            if (registroExitoso) {
                Text(
                    text = "¡Usuario registrado con éxito!",
                    color = Color(0xFF00C853),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}