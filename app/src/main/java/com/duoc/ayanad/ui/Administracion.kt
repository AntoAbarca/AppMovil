package com.duoc.ayanad.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.ayanad.model.UsuarioEntity
import com.duoc.ayanad.viewmodel.AdminViewModel

/** Menú de Administración: lista de usuarios con su rol y opción de eliminarlos. */
@Composable
fun Administracion(
    viewModel: AdminViewModel,
    onCerrarSesion: () -> Unit = {}
) {
    LaunchedEffect(Unit) { viewModel.cargar() }

    val usuarios by viewModel.usuarios.collectAsState()
    var porEliminar by remember { mutableStateOf<UsuarioEntity?>(null) }

    Scaffold(
        containerColor = TmFondo,
        topBar = { EncabezadoTm("Administración", null) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Usuarios registrados (${usuarios.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            if (usuarios.isEmpty()) {
                Text("No hay usuarios registrados.", color = Color.Gray)
            }

            usuarios.forEach { u ->
                TarjetaTm {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${u.nombre} ${u.apellido}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(u.email, color = Color.Gray, fontSize = 13.sp)
                            Spacer(Modifier.height(6.dp))
                            EtiquetaEstado(
                                if (u.tipoUsuario == "Aplicador" && u.especialidad.isNotBlank())
                                    "${u.tipoUsuario} - ${u.especialidad}"
                                else u.tipoUsuario
                            )
                            Spacer(Modifier.height(6.dp))
                            Text("RUT: ${u.rut}  |  ${u.region}", color = Color.Gray, fontSize = 12.sp)
                        }
                        TextButton(onClick = { porEliminar = u }) {
                            Text("Eliminar", color = Color.Red)
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = onCerrarSesion,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Cerrar sesión", color = TmAzul, fontSize = 16.sp)
            }
        }
    }

    porEliminar?.let { u ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            title = { Text("Eliminar usuario") },
            text = {
                Text(
                    "¿Seguro que quieres eliminar a ${u.nombre} ${u.apellido}? " +
                        "También se borrarán sus solicitudes u ofertas. Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminar(u)
                    porEliminar = null
                }) { Text("Eliminar", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { porEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}
