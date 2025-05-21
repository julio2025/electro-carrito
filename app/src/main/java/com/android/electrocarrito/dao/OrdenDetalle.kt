package com.android.electrocarrito.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orden_detalle")
data class OrdenDetalle(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val id_orden: Int,
    val id_producto: Int,
    val cantidad: Int,
    val precio: Double
)