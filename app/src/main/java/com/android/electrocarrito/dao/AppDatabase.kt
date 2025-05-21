package com.android.electrocarrito.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Producto::class, Orden::class, OrdenDetalle::class, Pago::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun ordenDao(): OrdenDao
    abstract fun ordenDetalleDao(): OrdenDetalleDao
    abstract fun pagoDao(): PagoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "electrocarrito-db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}