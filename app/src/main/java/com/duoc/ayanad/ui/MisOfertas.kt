package com.duoc.ayanad.ui

import androidx.compose.foundation.background
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
import com.duoc.ayanad.model.OfertaDetalle

private fun estiloOferta(estado: String): Pair<Color, Color> = when (estado) {
    "Aceptada" -> Pair(Color(0xFF388E3C), Color(0xFFE8F5E9))
    "Rechazada" -> Pair(Color(0xFFD32F2F), Color(0xFFFFEBEE))
    else -> Pair(Color(0xFF1976D2), Color(0xFFE3F2FD))
}

@Composable
fun MisOfertas(
    ofertas: List<OfertaDetalle> = emptyList(),
    onVolver: (() -> Unit)? = null,
    onOfertaClick: (OfertaDetalle) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = TmFondo,
        topBar = { EncabezadoTm("Mis ofertas", onVolver) },
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
            if (ofertas.isEmpty()) {
                Text("Aún no has enviado ofertas.", color = Color.Gray)
            }

            ofertas.forEach { detalle ->
                val (color, fondo) = estiloOferta(detalle.oferta.estado)
                val s = detalle.solicitud
                OfertaCard(
                    estado = detalle.oferta.estado,
                    colorEstado = color,
                    fondoEstado = fondo,
                    producto = s?.tipoProducto ?: "-",
                    superficie = if (s != null) "${formatoHa(s.superficie)} ha - ${s.aplicacion}" else "-",
                    tuOferta = "${formatoCLP(detalle.oferta.precioPorHa)} CLP por ha",
                    onClick = { onOfertaClick(detalle) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    }
}

@Composable
fun OfertaCard(
    estado: String,
    colorEstado: Color,
    fondoEstado: Color,
    producto: String,
    superficie: String,
    tuOferta: String,
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
            // Etiqueta de estado
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

            Text(
                text = "Producto: $producto",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Superficie: $superficie",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Tu oferta: $tuOferta",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
        }
    }
}