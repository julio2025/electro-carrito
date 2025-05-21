package com.android.electrocarrito.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pagos")
data class Pago(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val id_orden: Int,
    val red_pago: String,
    val numero_tarjeta: String,
    val exp_date: String,
    val cvv: String,
    val monto: Double,
    val estado_pago: String
)