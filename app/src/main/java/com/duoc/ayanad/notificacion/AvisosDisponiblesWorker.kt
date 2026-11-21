package com.duoc.ayanad.notificacion

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.duoc.ayanad.model.AppDatabase
import com.duoc.ayanad.repository.SesionManager
import com.duoc.ayanad.repository.SolicitudRepository
import java.util.concurrent.TimeUnit

/**
 * Trabajo en segundo plano: descarga las ofertas desde Xano y avisa aunque la app esté cerrada.
 * Qué avisar lo decide SolicitudRepository; aquí solo se muestran las notificaciones.
 */
class AvisosDisponiblesWorker(contexto: Context, parametros: WorkerParameters) : CoroutineWorker(contexto, parametros) {

    override suspend fun doWork(): Result {
        SesionManager.init(applicationContext)
        if (!SesionManager.haySesion() || !NotificacionHelper.tienePermiso(applicationContext)) {
            return Result.success()
        }

        val tipo = SesionManager.tipo
        val usuarioId = SesionManager.usuarioId
        val repositorio = SolicitudRepository(AppDatabase.getDatabase(applicationContext))

        repositorio.sincronizarParaAvisos(tipo, usuarioId)
        listOfNotNull(
            repositorio.avisoPendiente(tipo, usuarioId),
            repositorio.avisoDisponibles(tipo, usuarioId)
        ).forEach { NotificacionHelper.mostrar(applicationContext, it) }

        return Result.success()
    }
}

/** Deja programado el trabajo periódico (el mínimo que permite Android es cada 15 minutos). */
object ProgramadorAvisos {

    private const val NOMBRE_TRABAJO = "avisos_ofertas"

    fun programar(context: Context) {
        val conInternet = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val peticion = PeriodicWorkRequestBuilder<AvisosDisponiblesWorker>(15, TimeUnit.MINUTES)
            .setConstraints(conInternet)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            NOMBRE_TRABAJO,
            ExistingPeriodicWorkPolicy.KEEP,   // si ya estaba programado, no lo duplica
            peticion
        )
    }
}
