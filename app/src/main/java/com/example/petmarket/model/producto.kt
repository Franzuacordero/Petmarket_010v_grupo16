package com.example.petmarket.model

data class Producto(
    val id: String? = null,
    val name: String = "",
    val desc: String = "",
    val price: Int = 0,
    val stock: Int = 0,
    val image: String = ""
)
