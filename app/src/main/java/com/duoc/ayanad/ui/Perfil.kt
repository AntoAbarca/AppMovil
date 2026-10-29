package com.duoc.ayanad.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Perfil(
    nombre: String = "Nombre Ejemplo",
    apellido: String = "Apellido Ejemplo",
    rut: String = "11.111.111-1",
    email: String = "holasoyuncorreo@correo.zl",
    telefono: String = "+5691234567",
    region: String = "Región Metropolitana",
    clasificacion: String = "4.6",
    onVolver: (() -> Unit)? = null,
    onInicio: () -> Unit = {},
    onPerfil: () -> Unit = {}
) {
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
            // Foto de perfil (de relleno, la pauta no pide cámara)
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

            // Datos del usuario
            TarjetaTm {
                Text("Datos", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                CampoDato("Nombre", nombre)
                CampoDato("Apellido", apellido)
                CampoDato("RUT", rut)
                CampoDato("Email", email)
                CampoDato("Teléfono", telefono)
                CampoDato("Región", region, conFlecha = true)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Clasificación
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
        }
    }
}

@Composable
private fun CampoDato(etiqueta: String, valor: String, conFlecha: Boolean = false) {
    OutlinedTextField(
        value = valor,
        onValueChange = {},
        readOnly = true,
        label = { Text(etiqueta) },
        trailingIcon = if (conFlecha) {
            ({ Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) })
        } else null,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}
