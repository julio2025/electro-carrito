package com.android.electrocarrito.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface OrdenDetalleDao {
    @Query("SELECT * FROM orden_detalle")
    fun getAll(): List<OrdenDetalle>

    @Query("SELECT * FROM orden_detalle WHERE id_orden = :id_orden")
    fun getByOrderId(id_orden: Int): List<OrdenDetalle>

    @Insert
    fun insert(orden_detalle: OrdenDetalle)

    @Update
    fun update(orden_detalle: OrdenDetalle)

    @Delete
    fun delete(orden_detalle: OrdenDetalle)

    @Query("DELETE FROM orden_detalle")
    fun deleteAll()
}