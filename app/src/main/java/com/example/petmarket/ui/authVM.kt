package com.example.petmarket.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.petmarket.data.AuthRepo

class AuthVm : ViewModel() {

    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var isLoggedIn = mutableStateOf(false)
    var isAdmin = mutableStateOf(false)
    var error = mutableStateOf<String?>(null)
    var info = mutableStateOf<String?>(null)

    // ---- login con 2 roles ----
    fun login() {
        val userEmail = email.value.trim()
        val pwd = password.value

        if (userEmail == "admin@pet.com" && pwd == "admin123") {
            // admin
            isLoggedIn.value = true
            isAdmin.value = true
            error.value = null
            info.value = "Sesión iniciada como ADMIN"
        } else if (userEmail == "cliente@pet.com" && pwd == "cliente123") {
            // cliente
            isLoggedIn.value = true
            isAdmin.value = false
            error.value = null
            info.value = "Sesión iniciada como CLIENTE"
        } else {
            // credencial mal
            isLoggedIn.value = false
            isAdmin.value = false
            error.value = "Credenciales incorrectas"
            info.value = null
        }
    }

    // ---- registro de nueva cuenta----
    fun register() {
        error.value = null
        info.value = null

        if (!validateEmail(email.value)) {
            error.value = "Email inválido"
            return
        }
        if (!validatePassword(password.value)) {
            error.value = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        if (AuthRepo.register(email.value, password.value)) {
            info.value = "Cuenta creada. ¡Ahora puedes iniciar sesión!"
        } else {
            error.value = "Ya existe una cuenta con ese email o datos inválidos"
        }
    }

    // ---- recuperar contraseña ----
    fun resetPassword(targetEmail: String) {
        error.value = null
        info.value = null

        if (!validateEmail(targetEmail)) {
            error.value = "Email inválido"
            return
        }

        val ok = AuthRepo.resetPassword(targetEmail)
        info.value = if (ok) {
            "Si el email existe, enviamos un enlace de recuperación."
        } else {
            "No encontramos una cuenta con ese email."
        }
    }

    // ---- logout ----
    fun logout() {
        isLoggedIn.value = false
        isAdmin.value = false
        email.value = ""
        password.value = ""
        error.value = null
        info.value = null
    }

    // ---- validacion simple----
    private fun validateEmail(value: String): Boolean {
        return value.contains("@") && value.contains(".") && value.length >= 5
    }

    private fun validatePassword(value: String): Boolean {
        return value.length >= 6
    }
}
