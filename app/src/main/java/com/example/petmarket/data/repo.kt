package com.example.petmarket.data

import androidx.compose.runtime.mutableStateListOf
import com.example.petmarket.model.*

object Repo {
    // --- TIENDA ---
    val products = mutableStateListOf(
        Product(1, "Alimento perro 3kg", "Receta pollo + arroz", 89900, stock = 5),
        Product(2, "Arena gato 10L", "Aglomerante sin perfume", 129900, stock = 3),
        Product(3, "Correa nylon", "1.5m resistente", 49900, rentable = true, stock = 10)
    )
    val cart = mutableStateListOf<CartItem>()

    private fun cartQty(productId: Long): Int =
        cart.firstOrNull { it.product.id == productId }?.qty ?: 0

    fun canAddToCart(p: Product, qty: Int): Boolean =
        cartQty(p.id) + qty <= p.stock

    fun addToCart(p: Product, qty: Int) {
        if (!canAddToCart(p, qty)) return
        val existing = cart.find { it.product.id == p.id }
        if (existing != null) existing.qty += qty else cart.add(CartItem(p, qty))
    }

    fun removeFromCart(productId: Long) {
        cart.removeAll { it.product.id == productId }
    }

    fun clearCart() {
        cart.clear()
    }

    fun totalCents(): Long =
        cart.sumOf { it.product.priceCents * it.qty }

    /** Confirmar pedido: descuenta stock y limpia carrito */
    fun confirmOrder() {
        cart.forEach { ci ->
            val idx = products.indexOfFirst { it.id == ci.product.id }
            if (idx >= 0) {
                val current = products[idx]
                val newStock = (current.stock - ci.qty).coerceAtLeast(0)
                products[idx] = current.copy(stock = newStock)
            }
        }
        clearCart()
    }

    // --- FORO ---
    val topics = mutableStateListOf(
        Topic(1, "Noticias", "Nueva vacuna antirrábica en stock", "Admin"),
        Topic(2, "Preguntas", "¿Cada cuánto cambiar arena?", "Carla")
    )

    val posts = mutableStateListOf(
        Post(1, "Admin", "Llegan esta semana."),
        Post(2, "Matías", "Yo la cambio cada 5 días."),
        Post(2, "Sofía", "Depende del gato y la marca.")
    )

    fun addTopic(category: String, title: String, author: String) {
        val id = (topics.maxOfOrNull { it.id } ?: 0) + 1
        topics.add(Topic(id, category, title, author))
    }

    fun addPost(topicId: Long, author: String, body: String) {
        posts.add(Post(topicId, author, body))
    }

    // --- SERVICIOS ---
    val services = listOf(
        Service(1, "Consulta veterinaria", 30, 150000),
        Service(2, "Baño y corte", 45, 200000)
    )

    val serviceOrders = mutableStateListOf<ServiceOrder>()

    fun createServiceOrder(serviceId: Long, customer: String) {
        val id = (serviceOrders.maxOfOrNull { it.id } ?: 0) + 1
        serviceOrders.add(ServiceOrder(id, serviceId, customer))
    }

    fun advanceOrder(id: Long) {
        val o = serviceOrders.find { it.id == id } ?: return
        o.status = when (o.status) {
            "pendiente" -> "en_proceso"
            "en_proceso" -> "finalizado"
            else -> o.status
        }
    }

    // --- RESERVAS ---
    val professionals = listOf(
        Professional(1, "Dra. Valdés", "Veterinaria"),
        Professional(2, "Carlos Soto", "Grooming")
    )

    val slots = listOf(
        Slot(1, 1, "2025-10-16", "10:00"),
        Slot(2, 1, "2025-10-16", "11:00"),
        Slot(3, 2, "2025-10-16", "15:00"),
        Slot(4, 2, "2025-10-16", "16:00")
    )

    val appointments = mutableStateListOf<Appointment>()

    fun bookAppointment(professionalId: Long, slotId: Long, customer: String) {
        val id = (appointments.maxOfOrNull { it.id } ?: 0) + 1
        appointments.add(Appointment(id, professionalId, customer, slotId))
    }

    fun cancelAppointment(id: Long) {
        appointments.find { it.id == id }?.let { it.status = "cancelado" }
    }
}
