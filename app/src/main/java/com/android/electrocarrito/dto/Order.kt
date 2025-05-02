package com.android.electrocarrito.dto

data class Order(
    val id: String,
    val items: List<CartItem>,
    val total: String,
    val status: String,
    val paymentMethod: String,
    val paymentDate: String
)