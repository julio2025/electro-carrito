package com.android.electrocarrito.dto

import java.time.LocalDate

enum class OrderStatus { ATENDIDO, RECHAZADO }
enum class PaymentMethod { VISA, MASTERCARD, AMEX }

data class Order(
    val id: String,
    val items: List<CartItem>,
    val total: Double,
    val status: OrderStatus,
    val paymentMethod: PaymentMethod,
    val paymentDate: LocalDate // o LocalDate si usarás operaciones con fechas
)