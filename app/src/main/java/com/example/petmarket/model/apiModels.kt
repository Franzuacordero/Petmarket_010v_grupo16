package com.example.petmarket.model

data class ApiProduct(
    val id: String,
    val name: String,
    val price: Int,
    val image: String
)

data class ApiTopic(
    val id: String,
    val title: String,
    val author: String
)

data class ApiServiceItem(
    val id: String,
    val name: String,
    val durationMin: Int,
    val basePriceCents: Int
)

data class ApiSlot(
    val id: String,
    val professional: String,
    val date: String,
    val time: String
)
