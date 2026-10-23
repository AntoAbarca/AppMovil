package com.duoc.ayanad.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.ayanad.model.Solicitud
import com.duoc.ayanad.viewmodel.SolicitudViewModel

@Composable
fun SolicitudesCercanas(
    viewModel: SolicitudViewModel,
    onVolver: () -> Unit = {},
    onSolicitudClick: (Solicitud) -> Unit = {}   // lleva a la pantalla 9
) {
    val filtro by viewModel.filtroRegion.collectAsState()
    val solicitudes by viewModel.solicitudesCercanas.collectAsState()

    Scaffold(
        containerColor = TmFondo,
        topBar = { EncabezadoTm("Solicitudes cerca de ti", onVolver) },
        bottomBar = { BarraNavegacionTm(onInicio = onVolver) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text("Filtrar por región", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SelectorTm(
                    etiqueta = "Región",
                    valor = filtro,
                    opciones = REGIONES,
                    onSeleccion = viewModel::onFiltroRegionChange,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = viewModel::limpiarFiltro,
                    modifier = Modifier.padding(top = 8.dp)
                ) { Text("Limpiar", color = TmAzul) }
            }

            Spacer(Modifier.height(12.dp))

            if (solicitudes.isEmpty()) {
                Text("No hay solicitudes en esta región.", color = Color.Gray)
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(solicitudes, key = { it.id }) { s ->
                    TarjetaTm(onClick = { onSolicitudClick(s) }) {
                        Text("Producto: ${s.tipoProducto}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            "Superficie: ${formatoHa(s.superficie)} ha - ${s.aplicacion}",
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${s.region} - A ${s.distanciaKm} km",
                            color = Color.Gray, fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
