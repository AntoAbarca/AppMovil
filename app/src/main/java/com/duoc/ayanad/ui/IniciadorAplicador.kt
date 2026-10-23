package com.duoc.ayanad.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Azul = Color(0xFF2962FF)
private val Fondo = Color(0xFFF5F7FA)
private val BordeAzul = Color(0xFFB3C4FF)

@Composable
fun InicioAplicador(
    ofertasEnviadas: Int = 5,
    ofertasAceptadas: Int = 2,
    onSolicitudesCercanas: () -> Unit = {},
    onMisOfertas: () -> Unit = {},
    onMiPerfil: () -> Unit = {}
) {
    Scaffold(
        containerColor = Fondo,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Icon(Icons.Default.Info, contentDescription = "Información") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onMiPerfil,
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // Encabezado azul
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Azul)
                    .statusBarsPadding()
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(shape = RoundedCornerShape(20.dp), color = Color.White) {
                        // Reemplazar por Image(painterResource(R.drawable.logo_tecmundo), ...)
                        Text(
                            text = "TECMUNDO",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Bienvenido", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.25f)) {
                        Text(
                            text = "Cuenta aplicador",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {

                // Tarjeta grande: Solicitudes cerca de ti
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Azul,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onSolicitudesCercanas() }
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Azul)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Solicitudes cerca de ti", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Filtra por región y ofrece", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        }
                        Text(">", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dos tarjetas: Mis ofertas y Mi perfil
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    TarjetaAccion(
                        modifier = Modifier.weight(1f),
                        icono = Icons.Default.Menu,
                        titulo = "Mis ofertas",
                        subtitulo = "Estado de tus ofertas",
                        onClick = onMisOfertas
                    )
                    TarjetaAccion(
                        modifier = Modifier.weight(1f),
                        icono = Icons.Default.Person,
                        titulo = "Mi perfil",
                        subtitulo = "Datos y calificación",
                        onClick = onMiPerfil
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Resumen
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BordeAzul),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Resumen", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ofertasEnviadas.toString(), color = Azul, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                Text("Ofertas enviadas", color = Color.Gray, fontSize = 12.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ofertasAceptadas.toString(), color = Azul, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                Text("Ofertas aceptadas", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actividad reciente
                Text("Actividad reciente", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                TarjetaActividad("Tu oferta fue aceptada", "Líquida - 50 ha")
                Spacer(modifier = Modifier.height(8.dp))
                TarjetaActividad("Nueva solicitud cerca de ti", "Granulada - 10 ha - A 20 km")
            }
        }
    }
}

@Composable
private fun TarjetaAccion(
    modifier: Modifier,
    icono: ImageVector,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BordeAzul),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFFE0E7FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = Azul)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(subtitulo, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
private fun TarjetaActividad(titulo: String, detalle: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BordeAzul),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Azul))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(detalle, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}