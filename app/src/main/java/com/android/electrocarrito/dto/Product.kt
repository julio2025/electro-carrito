package com.android.electrocarrito.dto

data class Product(
    val id: Int,
    val image: String,
    val name: String,
    val description: String,
    val price: Double
)