package com.duoc.ayanad.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.ayanad.model.Solicitud

private fun estiloSolicitud(estado: String): Pair<Color, Color> = when (estado) {
    "Pendiente" -> Pair(Color(0xFFFFA726), Color(0xFFFFF3E0))
    "Con ofertas" -> Pair(Color(0xFF1976D2), Color(0xFFE3F2FD))
    else -> Pair(Color(0xFF388E3C), Color(0xFFE8F5E9))
}

@Composable
fun MisSolicitudes(
    solicitudes: List<Solicitud> = emptyList(),
    onVolver: (() -> Unit)? = null,
    onNuevaSolicitudClick: () -> Unit = {},
    onSolicitudClick: (Solicitud) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = TmFondo,
        topBar = { EncabezadoTm("Mis solicitudes", onVolver) },
        bottomBar = { BarraNavegacionTm() }
    ) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(scrollState)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Button(
                onClick = onNuevaSolicitudClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "+ Nueva solicitud",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (solicitudes.isEmpty()) {
                Text("Aún no has publicado solicitudes.", color = Color.Gray)
            }

            solicitudes.forEach { s ->
                val (color, fondo) = estiloSolicitud(s.estado)
                SolicitudCard(
                    estado = s.estado,
                    colorEstado = color,
                    fondoEstado = fondo,
                    producto = s.tipoProducto,
                    superficie = "${formatoHa(s.superficie)} ha",
                    horario = s.aplicacion,
                    onClick = { onSolicitudClick(s) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    }
}

@Composable
fun SolicitudCard(
    estado: String,
    colorEstado: Color,
    fondoEstado: Color,
    producto: String,
    superficie: String,
    horario: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Etiqueta de estado tipo píldora
            Box(
                modifier = Modifier
                    .background(fondoEstado, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = estado,
                    color = colorEstado,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información principal del producto
            Text(
                text = "Producto: $producto",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Superficie y horario
            Text(
                text = "Superficie: $superficie - $horario",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}