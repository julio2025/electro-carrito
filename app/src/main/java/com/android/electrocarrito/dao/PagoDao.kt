package com.android.electrocarrito.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PagoDao {
    @Query("SELECT * FROM pagos")
    fun getAll(): List<Pago>

    @Insert
    fun insert(pago: Pago)

    @Delete
    fun delete(pago: Pago)
}