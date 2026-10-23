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

@Composable
fun MisOfertasS(
    onOfertaClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .verticalScroll(scrollState)
    ) {
        // Franja de TecMundo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0xFF2962FF)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Mis ofertas",
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

            OfertaCard(
                estado = "Enviada",
                colorEstado = Color(0xFF1976D2), // Azul
                fondoEstado = Color(0xFFE3F2FD),
                producto = "granulada",
                superficie = "10 ha - Diurna",
                tuOferta = "xxxx CLP por ha",
                onClick = onOfertaClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            OfertaCard(
                estado = "Aceptada",
                colorEstado = Color(0xFF388E3C), // Verde
                fondoEstado = Color(0xFFE8F5E9),
                producto = "líquida",
                superficie = "50 ha - Diurna",
                tuOferta = "xxxx CLP por ha",
                onClick = onOfertaClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            OfertaCard(
                estado = "Rechazada",
                colorEstado = Color(0xFFD32F2F), // Rojo
                fondoEstado = Color(0xFFFFEBEE),
                producto = "líquida",
                superficie = "14 ha - Nocturna",
                tuOferta = "xxxx CLP por ha",
                onClick = onOfertaClick
            )
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