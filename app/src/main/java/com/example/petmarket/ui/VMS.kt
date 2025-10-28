package com.example.petmarket.ui

import androidx.lifecycle.ViewModel
import com.example.petmarket.data.Repo
import com.example.petmarket.model.Product

class StoreVm : ViewModel() {
    val products = Repo.products
    val cart = Repo.cart

    fun add(p: Product, qty: Int) = Repo.addToCart(p, qty)
    fun remove(id: Long) = Repo.removeFromCart(id)
    fun total() = Repo.totalCents()
    fun confirm() = Repo.confirmOrder()
    fun canAdd(p: Product, qty: Int) = Repo.canAddToCart(p, qty)
}

class ForumVm : ViewModel() {
    val topics = Repo.topics
    fun addTopic(cat: String, title: String, author: String) = Repo.addTopic(cat, title, author)
    fun postsOf(topicId: Long) = Repo.posts.filter { it.topicId == topicId }
    fun addPost(topicId: Long, author: String, body: String) = Repo.addPost(topicId, author, body)
}

class ServicesVm : ViewModel() {
    val services = Repo.services
    val orders = Repo.serviceOrders
    fun create(serviceId: Long, customer: String) = Repo.createServiceOrder(serviceId, customer)
    fun advance(orderId: Long) = Repo.advanceOrder(orderId)
}

class BookingVm : ViewModel() {
    val professionals = Repo.professionals
    fun slotsOf(proId: Long) = Repo.slots.filter { it.professionalId == proId }
    val appointments = Repo.appointments
    fun book(proId: Long, slotId: Long, customer: String) = Repo.bookAppointment(proId, slotId, customer)
    fun cancel(id: Long) = Repo.cancelAppointment(id)
}


