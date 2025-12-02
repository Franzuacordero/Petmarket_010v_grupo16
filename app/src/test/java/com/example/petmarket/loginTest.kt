package com.example.petmarket

import org.junit.Assert.assertEquals
import org.junit.Test

class LoginTest {

    private fun validarLogin(email: String, pass: String): String {
        return if (email == "admin@pet.cl" && pass == "1234") {
            "admin"
        } else if (email.isNotEmpty() && pass.isNotEmpty()) {
            "cliente"
        } else {
            "error"
        }
    }

    @Test
    fun loginAdmin_correcto() {
        val rol = validarLogin("admin@pet.cl", "1234")
        assertEquals("admin", rol)
    }

    @Test
    fun loginCliente_correcto() {
        val rol = validarLogin("cliente@mail.com", "abcd")
        assertEquals("cliente", rol)
    }

    @Test
    fun login_incorrecto() {
        val rol = validarLogin("", "")
        assertEquals("error", rol)
    }
}
