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

@Composable
fun MisSolicitudes(
    onNuevaSolicitudClick: () -> Unit = {},
    onSolicitudClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .verticalScroll(scrollState)
    ) {
        // Franja superior azul característica de TecMundo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0xFF2962FF)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Mis solicitudes",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Botón superior "+ Nueva solicitud"
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

            // Tarjeta 1: Pendiente
            SolicitudCard(
                estado = "Pendiente",
                colorEstado = Color(0xFFFFA726), // Naranja suave
                fondoEstado = Color(0xFFFFF3E0),
                producto = "granulada",
                superficie = "10 ha",
                horario = "Diurna",
                onClick = onSolicitudClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta 2: Con ofertas
            SolicitudCard(
                estado = "Con ofertas",
                colorEstado = Color(0xFF1976D2), // Azul suave
                fondoEstado = Color(0xFFE3F2FD),
                producto = "líquida",
                superficie = "50 ha",
                horario = "Diurna",
                onClick = onSolicitudClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta 3: Finalizada
            SolicitudCard(
                estado = "Finalizada",
                colorEstado = Color(0xFF388E3C), // Verde suave
                fondoEstado = Color(0xFFE8F5E9),
                producto = "líquida",
                superficie = "14 ha",
                horario = "Nocturna",
                onClick = onSolicitudClick
            )
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