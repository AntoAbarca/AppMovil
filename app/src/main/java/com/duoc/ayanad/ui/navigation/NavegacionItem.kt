package com.duoc.ayanad.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

// Aquí definimos el modelo por item de navegación, para poder definir su apariencia y su ruteo.
data class NavegacionItem(
    val titulo: String,
    val ruta: String,
    // ImageVector maneja los íconos vectoriales que se van a usar en la navegación.
    val icono: ImageVector
)
