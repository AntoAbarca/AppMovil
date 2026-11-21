package com.duoc.ayanad.notificacion

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.duoc.ayanad.MainActivity
import com.duoc.ayanad.model.AvisoNotificacion

/** Muestra las notificaciones de ofertas (recurso nativo). El texto lo decide el repositorio. */
object NotificacionHelper {

    private const val CANAL_OFERTAS = "canal_ofertas"

    /** Antes de Android 13 no hace falta permiso; desde Android 13 hay que pedirlo. */
    fun tienePermiso(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
    }

    // Desde Android 8 toda notificación necesita un canal
    private fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val canal = NotificationChannel(
            CANAL_OFERTAS,
            "Ofertas",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Avisos de ofertas nuevas y ofertas aceptadas" }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    @SuppressLint("MissingPermission")   // el permiso se revisa en tienePermiso()
    fun mostrar(context: Context, aviso: AvisoNotificacion) {
        if (!tienePermiso(context)) return
        crearCanal(context)

        // Al tocar la notificación se abre la app
        val abrirApp = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendiente = PendingIntent.getActivity(
            context, 0, abrirApp, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificacion = NotificationCompat.Builder(context, CANAL_OFERTAS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(aviso.titulo)
            .setContentText(aviso.mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(aviso.mensaje))
            .setContentIntent(pendiente)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(aviso.id, notificacion)
    }
}
