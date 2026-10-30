package com.duoc.ayanad.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.ayanad.model.Oferta
import com.duoc.ayanad.viewmodel.SolicitudViewModel
import java.text.NumberFormat
import java.util.Locale


@Composable
fun DetalleSolicitudProductor(
    viewModel: SolicitudViewModel,
    solicitudId: Int,
    onVolver: () -> Unit = {},
    onComunicarse: (Oferta) -> Unit = {}
) {
    LaunchedEffect(solicitudId) { viewModel.cargarDetalle(solicitudId) }

    val solicitud by viewModel.solicitudActual.collectAsState()
    val ofertas by viewModel.ofertas.collectAsState()

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
            solicitud?.let { s ->
                TarjetaTm {
                    EtiquetaEstado(s.estado)
                    Spacer(Modifier.height(10.dp))
                    Text("Producto: ${s.tipoProducto}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        "Superficie: ${formatoHa(s.superficie)} ha - ${s.aplicacion}",
                        color = Color.Gray, fontSize = 14.sp
                    )
                }
            }

            Text("Ofertas", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            if (ofertas.isEmpty()) {
                Text("Aún no hay ofertas para esta solicitud.", color = Color.Gray)
            }

            ofertas.forEach { oferta ->
                TarjetaOferta(
                    oferta = oferta,
                    onAceptar = { viewModel.aceptarOferta(oferta.id) },
                    onComunicarse = { onComunicarse(oferta) },
                    onCalificar = { viewModel.calificarOferta(oferta.id, it) }
                )
            }
        }
    }
}

@Composable
private fun TarjetaOferta(
    oferta: Oferta,
    onAceptar: () -> Unit,
    onComunicarse: () -> Unit,
    onCalificar: (Int) -> Unit
) {
    TarjetaTm {
        Text(oferta.aplicador, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(4.dp))
        Text("Precio por ha: ${formatoCLP(oferta.precioPorHa)} CLP", fontSize = 14.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                if (oferta.calificacion > 0) "Calificación: ${oferta.calificacion}" else "Sin calificación",
                color = Color.Gray, fontSize = 14.sp
            )
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
        }
        if (oferta.estado == "Aceptada") {
            Spacer(Modifier.height(8.dp))
            Text(
                if (oferta.calificacionDada > 0) "Tu calificación a este aplicador:" else "Califica el trabajo del aplicador:",
                fontSize = 14.sp
            )
            Row {
                for (n in 1..5) {
                    IconButton(onClick = { onCalificar(n) }, modifier = Modifier.size(40.dp)) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "$n estrellas",
                            tint = if (n <= oferta.calificacionDada) Color(0xFFFFB300) else Color.LightGray,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onAceptar,
                enabled = oferta.estado == "Enviada",
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = TmAzul)
            ) { Text(
                    when (oferta.estado) {
                        "Aceptada" -> "Aceptada"
                        "Rechazada" -> "Rechazada"
                        else -> "Aceptar oferta"
                    },
                    color = Color.White, fontSize = 13.sp
                ) }
            OutlinedButton(onClick = onComunicarse, modifier = Modifier.weight(1f)) {
                Text("Comunicarse", color = TmAzul, fontSize = 13.sp)
            }
        }
    }
}

internal fun formatoCLP(valor: Int): String =
    NumberFormat.getIntegerInstance(Locale("es", "CL")).format(valor)

internal fun formatoHa(valor: Double): String =
    if (valor % 1.0 == 0.0) valor.toInt().toString() else valor.toString()
