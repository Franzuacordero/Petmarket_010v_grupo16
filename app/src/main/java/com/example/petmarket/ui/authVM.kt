package com.example.petmarket.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.petmarket.data.AuthRepo

class AuthVm : ViewModel() {
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val isLoggedIn = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)
    val info = mutableStateOf<String?>(null)

    fun validateEmail(e: String) =
        e.contains('@') && e.contains('.') && e.length >= 5

    fun validatePassword(p: String) =
        p.length >= 6

    fun login() {
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
        if (AuthRepo.login(email.value, password.value)) {
            isLoggedIn.value = true
            info.value = "Inicio de sesión exitoso"
        } else {
            error.value = "Credenciales incorrectas"
        }
    }

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

    fun logout() {
        isLoggedIn.value = false
        password.value = ""
        info.value = "Sesión cerrada"
    }
}


