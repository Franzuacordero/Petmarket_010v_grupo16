package com.example.petmarket.model

data class Pedido(
    val id: String? = null,
    val items: List<CartItem> = emptyList(),
    val total: Int = 0
)

data class CartItem(
    val name: String = "",
    val price: Int = 0
)
