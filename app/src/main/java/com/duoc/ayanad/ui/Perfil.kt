package com.duoc.ayanad.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.duoc.ayanad.repository.SesionManager
import com.duoc.ayanad.viewmodel.PerfilViewModel
import java.io.File

@Composable
fun Perfil(
    viewModel: PerfilViewModel,
    onVolver: (() -> Unit)? = null,
    onInicio: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onCerrarSesion: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val form by viewModel.form.collectAsState()
    val errors by viewModel.errors.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val esAplicador by viewModel.esAplicador.collectAsState()
    val clasificacion by viewModel.clasificacion.collectAsState()

    LaunchedEffect(Unit) { viewModel.cargarPerfil(context) }

    // ---------- Foto de perfil con la cámara ----------
    val usuarioId = SesionManager.usuarioId
    var fotoVersion by remember { mutableStateOf(0) }
    val foto: ImageBitmap? = remember(fotoVersion, usuarioId) { cargarFotoPerfil(context, usuarioId) }

    val camaraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { tomada ->
        if (tomada) {
            guardarFotoTomada(context, usuarioId)
            fotoVersion++
        }
    }

    val permisoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) {
            camaraLauncher.launch(uriTemporal(context))
        } else {
            Toast.makeText(
                context,
                "Sin permiso de cámara no se puede tomar la foto de perfil",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun tomarFoto() {
        val permiso = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permiso == PackageManager.PERMISSION_GRANTED) {
            camaraLauncher.launch(uriTemporal(context))
        } else {
            permisoLauncher.launch(Manifest.permission.CAMERA)   // muestra el cuadro de autorización
        }
    }

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
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E7FF))
                    .clickable { tomarFoto() },
                contentAlignment = Alignment.Center
            ) {
                if (foto != null) {
                    Image(
                        bitmap = foto,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Foto de perfil",
                        tint = TmAzul,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            TextButton(onClick = { tomarFoto() }) {
                Text(if (foto != null) "Cambiar foto" else "Tomar foto", color = TmAzul)
            }

            Spacer(modifier = Modifier.height(8.dp))

            TarjetaTm {
                Text("Datos", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                CampoPerfil("Nombre", form.nombre, viewModel::onNombreChange, errors.nombre)
                CampoPerfil("Apellido", form.apellido, viewModel::onApellidoChange, errors.apellido)
                CampoPerfil("RUT", form.rut, viewModel::onRutChange, errors.rut)
                CampoPerfil(
                    "Email", form.email, viewModel::onEmailChange, errors.email,
                    teclado = KeyboardType.Email
                )
                CampoPerfil(
                    "Teléfono", form.telefono, viewModel::onTelefonoChange, errors.telefono,
                    teclado = KeyboardType.Number, prefijo = "+56 9 "
                )
                SelectorTm(
                    etiqueta = "Región",
                    valor = form.region,
                    opciones = REGIONES,
                    onSeleccion = viewModel::onRegionChange,
                    error = errors.region
                )
            }

            if (esAplicador) {
                Spacer(modifier = Modifier.height(16.dp))
                TarjetaTm {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Clasificación", color = Color.Gray, fontSize = 12.sp)
                            Text(
                                text = clasificacion,
                                color = TmAzul,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.guardarCambios(context) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TmAzul)
            ) {
                Text("Guardar cambios", color = Color.White, fontSize = 16.sp)
            }

            mensaje?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = TmAzul, fontSize = 14.sp)
            }

            if (onCerrarSesion != null) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onCerrarSesion,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Cerrar sesión", color = TmAzul, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun CampoPerfil(
    etiqueta: String,
    valor: String,
    onChange: (String) -> Unit,
    error: String?,
    teclado: KeyboardType = KeyboardType.Text,
    prefijo: String? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text(etiqueta) },
        prefix = prefijo?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        isError = error != null,
        supportingText = error?.let { { Text(it, color = Color.Red) } },
        singleLine = true,
        colors = tmCamposColors(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
    )
}

// ---------- Utilidades de la foto ----------

private fun archivoTemporal(context: Context) = File(context.cacheDir, "foto_tmp.jpg")

private fun archivoFoto(context: Context, usuarioId: Long) = File(context.filesDir, "perfil_$usuarioId.jpg")

/** Archivo temporal donde la cámara deja la foto (compartido con la cámara vía FileProvider). */
private fun uriTemporal(context: Context): Uri {
    val f = archivoTemporal(context)
    if (f.exists()) f.delete()
    f.createNewFile()
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", f)
}

/** Si la cámara tomó la foto, la guarda como foto definitiva de este usuario. */
private fun guardarFotoTomada(context: Context, usuarioId: Long) {
    val tmp = archivoTemporal(context)
    if (tmp.exists() && tmp.length() > 0) {
        tmp.copyTo(archivoFoto(context, usuarioId), overwrite = true)
    }
    tmp.delete()
}

private fun cargarFotoPerfil(context: Context, usuarioId: Long): ImageBitmap? {
    val f = archivoFoto(context, usuarioId)
    if (!f.exists()) return null
    val opciones = BitmapFactory.Options().apply { inSampleSize = 4 }   // reduce el tamaño para ahorrar memoria
    val bmp = BitmapFactory.decodeFile(f.absolutePath, opciones) ?: return null

    val grados = try {
        when (ExifInterface(f.absolutePath).getAttributeInt(
            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
    } catch (e: Exception) {
        0f
    }

    val resultado: Bitmap = if (grados != 0f) {
        val matriz = Matrix().apply { postRotate(grados) }
        Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matriz, true)
    } else {
        bmp
    }
    return resultado.asImageBitmap()
}
