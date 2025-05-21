package com.android.electrocarrito.dto

import com.android.electrocarrito.dao.Producto

data class CartItem(
    val product: Producto,
    var quantity: Int
) {
    val total: Double
        get() = product.precio * quantity
}