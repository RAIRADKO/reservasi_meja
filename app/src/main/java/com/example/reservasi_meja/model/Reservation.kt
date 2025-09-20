package com.example.reservasi_meja.model

data class Reservation(
    val id: String = "",
    val userId: String = "",
    val customerName: String = "",
    val date: String = "",
    val time: String = "",
    val guests: Int = 0,
    val tableNumber: Int = 0,
    val status: String = "pending" // pending, confirmed, cancelled
)