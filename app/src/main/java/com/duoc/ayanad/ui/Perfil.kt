package com.duoc.ayanad.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.ayanad.viewmodel.PerfilViewModel

@Composable
fun Perfil(
    viewModel: PerfilViewModel,
    clasificacion: String = "—",
    onVolver: (() -> Unit)? = null,
    onInicio: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onCerrarSesion: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val form by viewModel.form.collectAsState()
    val errors by viewModel.errors.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    LaunchedEffect(Unit) { viewModel.cargarPerfil(context) }

    Scaffold(
        containerColor = TmFondo,
        topBar = { EncabezadoTm("Mi perfil", onVolver) },
        bottomBar = { BarraNavegacionTm(onInicio, onPerfil) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E7FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Foto de perfil",
                    tint = TmAzul,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            TarjetaTm {
                Text("Datos", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                CampoPerfil("Nombre", form.nombre, viewModel::onNombreChange, errors.nombre)
                CampoPerfil("Apellido", form.apellido, viewModel::onApellidoChange, errors.apellido)
                CampoPerfil("RUT", form.rut, viewModel::onRutChange, errors.rut)
                CampoPerfil(
                    "Email", form.email, viewModel::onEmailChange, errors.email,
                    teclado = KeyboardType.Email
                )
                CampoPerfil(
                    "Teléfono", form.telefono, viewModel::onTelefonoChange, errors.telefono,
                    teclado = KeyboardType.Number, prefijo = "+56 9 "
                )
                SelectorTm(
                    etiqueta = "Región",
                    valor = form.region,
                    opciones = REGIONES,
                    onSeleccion = viewModel::onRegionChange,
                    error = errors.region
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TarjetaTm {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Clasificación", color = Color.Gray, fontSize = 12.sp)
                        Text(
                            text = clasificacion,
                            color = TmAzul,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(Icons.Default.Star, contentDescription = null, tint = TmAzul)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.guardarCambios(context) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TmAzul)
            ) {
                Text("Guardar cambios", color = Color.White, fontSize = 16.sp)
            }

            mensaje?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = TmAzul, fontSize = 14.sp)
            }

            if (onCerrarSesion != null) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onCerrarSesion,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Cerrar sesión", color = TmAzul, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun CampoPerfil(
    etiqueta: String,
    valor: String,
    onChange: (String) -> Unit,
    error: String?,
    teclado: KeyboardType = KeyboardType.Text,
    prefijo: String? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text(etiqueta) },
        prefix = prefijo?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        isError = error != null,
        supportingText = error?.let { { Text(it, color = Color.Red) } },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
    )
}
