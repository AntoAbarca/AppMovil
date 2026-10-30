package com.duoc.ayanad.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [UsuarioEntity::class, SolicitudEntity::class, OfertaEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun solicitudDao(): SolicitudDao
    abstract fun ofertaDao(): OfertaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tecmundo_db"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration() // cambio de versión borra los datos de prueba
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
