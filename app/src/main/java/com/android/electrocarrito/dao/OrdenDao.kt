package com.android.electrocarrito.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrdenDao {
    @Query("SELECT * FROM ordenes")
    fun getAll(): List<Orden>

    @Insert
    fun insert(orden: Orden)

    @Delete
    fun delete(orden: Orden)
}