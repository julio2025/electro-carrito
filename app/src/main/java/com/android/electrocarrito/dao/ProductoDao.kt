package com.android.electrocarrito.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos")
    fun getAll(): List<Producto>

    @Query("SELECT * FROM productos WHERE id = :id")
    fun getById(id: Int): Producto

    @Insert
    fun insert(product: Producto)

    @Update
    suspend fun update(producto: Producto)

    @Delete
    fun delete(product: Producto)

    @Query("DELETE FROM productos")
    suspend fun deleteAll()
}