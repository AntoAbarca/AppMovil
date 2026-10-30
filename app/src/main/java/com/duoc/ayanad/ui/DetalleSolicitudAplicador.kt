package com.duoc.ayanad.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.ayanad.viewmodel.SolicitudViewModel

/** Pantalla 9: el aplicador ve el detalle de la solicitud y envía su oferta. */
@Composable
fun DetalleSolicitudAplicador(
    viewModel: SolicitudViewModel,
    solicitudId: Int,
    onVolver: () -> Unit = {},
    onOfertaEnviada: () -> Unit = {}
) {
    LaunchedEffect(solicitudId) { viewModel.cargarDetalleAplicador(solicitudId) }

    val solicitud by viewModel.solicitudActual.collectAsState()
    val precio by viewModel.precioOferta.collectAsState()
    val error by viewModel.errorOferta.collectAsState()
    val enviada by viewModel.ofertaEnviada.collectAsState()
    val miOferta by viewModel.miOferta.collectAsState()

    LaunchedEffect(enviada) {
        if (enviada) {
            viewModel.consumirOfertaEnviada()
            onOfertaEnviada()
        }
    }

    Scaffold(
        containerColor = TmFondo,
        topBar = { EncabezadoTm("Detalle de solicitud", onVolver) },
        bottomBar = { BarraNavegacionTm(onInicio = onVolver) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val s = solicitud
            if (s == null) {
                Text("No se encontró la solicitud.", color = Color.Gray)
            } else {
                TarjetaTm {
                    EtiquetaEstado(s.estado)
                    Spacer(Modifier.height(10.dp))
                    Text("Producto: ${s.tipoProducto}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Superficie: ${formatoHa(s.superficie)} ha", fontSize = 14.sp)
                    Text("Aplicación: ${s.aplicacion}", fontSize = 14.sp)
                    Text("Región: ${s.region}", fontSize = 14.sp)
                    Text("¿Tiene agua?: ${if (s.tieneAgua) "Sí" else "No"}", fontSize = 14.sp)
                    Text(
                        "Obstáculos: " + if (s.obstaculos.isEmpty()) "Ninguno" else s.obstaculos.joinToString(", "),
                        fontSize = 14.sp
                    )
                    if (s.comentarios.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text("Comentarios: ${s.comentarios}", color = Color.Gray, fontSize = 14.sp)
                    }
                }

                val cerrada = s.estado == "Finalizada" || miOferta?.estado == "Aceptada"

                if (miOferta != null) {
                    Text(
                        "Tu oferta actual: ${formatoCLP(miOferta!!.precioPorHa)} CLP por ha (${miOferta!!.estado})",
                        color = TmAzul, fontSize = 14.sp, fontWeight = FontWeight.Medium
                    )
                }

                if (cerrada) {
                    Text("Esta solicitud ya fue finalizada y no admite más ofertas.", color = Color.Gray)
                } else {
                    OutlinedTextField(
                        value = precio,
                        colors = tmCamposColors(),
                        onValueChange = viewModel::onPrecioOfertaChange,
                        label = { Text("Precio por ha (CLP)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = error != null,
                        supportingText = error?.let { { Text(it, color = Color.Red) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = viewModel::enviarOferta,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TmAzul)
                    ) {
                        Text(
                            if (miOferta != null) "Actualizar oferta" else "Enviar oferta",
                            color = Color.White, fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
