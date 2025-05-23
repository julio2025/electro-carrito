package com.android.electrocarrito.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ordenes")
data class Orden(
    @PrimaryKey val id: Int = 0,
    val id_usuario: Int,
    val fecha: String,
    val estado: String,
    var total: Double,
    val vigente: Boolean
)