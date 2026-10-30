package com.duoc.ayanad.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.ayanad.viewmodel.SolicitudViewModel

private val OBSTACULOS = listOf("Antenas", "Cables", "Torres de alta tensión", "Otros...")

@Composable
fun NuevaSolicitud(
    viewModel: SolicitudViewModel,
    onVolver: () -> Unit = {},
    onPublicada: (solicitudId: Int) -> Unit = {}
) {
    val form by viewModel.form.collectAsState()
    val errors by viewModel.errors.collectAsState()
    val publicada by viewModel.publicada.collectAsState()

    LaunchedEffect(publicada) {
        publicada?.let {
            onPublicada(it.id)
            viewModel.consumirPublicada()
        }
    }

    Scaffold(
        containerColor = TmFondo,
        topBar = { EncabezadoTm("Nueva solicitud", onVolver) },
        bottomBar = { BarraNavegacionTm(onInicio = onVolver) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                colors = tmCamposColors(),
                value = form.superficie,
                onValueChange = viewModel::onSuperficieChange,
                label = { Text("Superficie (ha)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = errors.superficie != null,
                supportingText = errors.superficie?.let { { Text(it, color = Color.Red) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            SelectorTm(
                etiqueta = "Tipo de producto",
                valor = form.tipoProducto,
                opciones = TIPOS_PRODUCTO,
                onSeleccion = viewModel::onTipoProductoChange,
                error = errors.tipoProducto
            )

            SelectorTm(
                etiqueta = "Región",
                valor = form.region,
                opciones = REGIONES,
                onSeleccion = viewModel::onRegionChange,
                error = errors.region
            )

            // ¿Tiene agua? Sí / No
            OpcionesExcluyentes(
                titulo = "¿Tiene agua?",
                opciones = listOf("Sí", "No"),
                seleccionada = if (form.tieneAgua) "Sí" else "No",
                onSeleccion = { viewModel.onTieneAguaChange(it == "Sí") }
            )

            // Aplicación Diurna / Nocturna
            OpcionesExcluyentes(
                titulo = "Aplicación",
                opciones = listOf("Diurna", "Nocturna"),
                seleccionada = form.aplicacion,
                onSeleccion = viewModel::onAplicacionChange
            )

            // Obstáculos
            Text("Obstáculos", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Column {
                OBSTACULOS.forEach { nombre ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = nombre in form.obstaculos,
                            onCheckedChange = { viewModel.onObstaculoToggle(nombre) },
                            colors = CheckboxDefaults.colors(checkedColor = TmAzul)
                        )
                        Text(nombre)
                    }
                }
            }

            OutlinedTextField(
                colors = tmCamposColors(),
                value = form.comentarios,
                onValueChange = viewModel::onComentariosChange,
                label = { Text("Comentarios adicionales") },
                placeholder = { Text("Escribe aquí...") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = viewModel::publicarSolicitud,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TmAzul)
            ) {
                Text("Publicar solicitud", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun OpcionesExcluyentes(
    titulo: String,
    opciones: List<String>,
    seleccionada: String,
    onSeleccion: (String) -> Unit
) {
    Column {
        Text(titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            opciones.forEach { opcion ->
                val activa = opcion == seleccionada
                Button(
                    onClick = { onSeleccion(opcion) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activa) TmAzul else Color.White,
                        contentColor = if (activa) Color.White else TmAzul
                    ),
                    border = if (activa) null else androidx.compose.foundation.BorderStroke(1.dp, TmBorde)
                ) { Text(opcion) }
            }
        }
    }
}

