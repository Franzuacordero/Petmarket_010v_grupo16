package com.example.petmarket.model

// --- tienda ---
data class Product(
    val id: Long,
    val name: String,
    val desc: String,
    val priceCents: Long,
    val rentable: Boolean = false,
    val stock: Int = 0
)

data class CartItem(val product: Product, var qty: Int)

// --- foro ---
data class Topic(val id: Long, val category: String, val title: String, val author: String)
data class Post(val topicId: Long, val author: String, val body: String)

// --- Servicios ---
data class Service(val id: Long, val name: String, val durationMin: Int, val basePriceCents: Long)
data class ServiceOrder(
    val id: Long,
    val serviceId: Long,
    val customer: String,
    var status: String = "pendiente"
)

// --- reservas ---
data class Professional(val id: Long, val name: String, val specialty: String)
data class Slot(val id: Long, val professionalId: Long, val date: String, val time: String)
data class Appointment(
    val id: Long,
    val professionalId: Long,
    val customer: String,
    val slotId: Long,
    var status: String = "reservado"
)
