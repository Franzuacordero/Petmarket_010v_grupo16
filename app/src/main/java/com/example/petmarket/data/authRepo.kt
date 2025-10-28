package com.example.petmarket.data

object AuthRepo {

    data class User(val email: String, val password: String)

    // Usuario de prueba
    private val users = mutableListOf(
        User(email = "demo@pet.com", password = "123456")
    )

    fun login(email: String, password: String): Boolean {
        val u = users.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) }
        return u?.password == password
    }

    fun register(email: String, password: String): Boolean {
        if (email.isBlank() || password.length < 6) return false
        if (users.any { it.email.equals(email.trim(), ignoreCase = true) }) return false
        users.add(User(email.trim(), password))
        return true
    }

    fun resetPassword(email: String): Boolean {
        return users.any { it.email.equals(email.trim(), ignoreCase = true) }
    }
}


