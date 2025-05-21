package com.android.electrocarrito.dao

import androidx.room.Database
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
}