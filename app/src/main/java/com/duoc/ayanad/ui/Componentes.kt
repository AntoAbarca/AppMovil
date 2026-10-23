package com.duoc.ayanad.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Colores compartidos (mismos valores que usan Login / Inicio)
val TmAzul = Color(0xFF2962FF)
val TmFondo = Color(0xFFF5F7FA)
val TmBorde = Color(0xFFB3C4FF)

val REGIONES = listOf(
    "Arica y Parinacota", "Tarapacá", "Antofagasta", "Atacama", "Coquimbo",
    "Valparaíso", "Región Metropolitana", "O'Higgins", "Maule", "Ñuble",
    "Biobío", "La Araucanía", "Los Ríos", "Los Lagos", "Aysén", "Magallanes"
)

val TIPOS_PRODUCTO = listOf("Granulada", "Líquida")


@Composable
fun EncabezadoTm(titulo: String, onVolver: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TmAzul)
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onVolver != null) {
            IconButton(onClick = onVolver) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
        } else {
            Spacer(Modifier.width(12.dp))
        }
        Text(titulo, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BarraNavegacionTm(onInicio: () -> Unit = {}, onPerfil: () -> Unit = {}) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(selected = false, onClick = {},
            icon = { Icon(Icons.Default.Info, contentDescription = "Información") })
        NavigationBarItem(selected = false, onClick = onInicio,
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") })
        NavigationBarItem(selected = false, onClick = onPerfil,
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") })
    }
}

@Composable
fun EtiquetaEstado(texto: String) {
    Surface(shape = RoundedCornerShape(50), color = Color(0xFFE0E7FF)) {
        Text(
            texto, color = TmAzul, fontSize = 12.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}


@Composable
fun TarjetaTm(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, TmBorde),
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}


@Composable
fun SelectorTm(
    etiqueta: String,
    valor: String,
    opciones: List<String>,
    onSeleccion: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    var abierto by remember { mutableStateOf(false) }
    Column(modifier) {
        Box {
            OutlinedTextField(
                value = valor,
                onValueChange = {},
                readOnly = true,
                label = { Text(etiqueta) },
                trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) },
                isError = error != null,
                modifier = Modifier.fillMaxWidth()
            )
            // Capa transparente para capturar el click sobre el campo
            Box(
                Modifier
                    .matchParentSize()
                    .clickable { abierto = true }
            )
            DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = { onSeleccion(opcion); abierto = false }
                    )
                }
            }
        }
        if (error != null) Text(error, color = Color.Red, fontSize = 12.sp)
    }
}
