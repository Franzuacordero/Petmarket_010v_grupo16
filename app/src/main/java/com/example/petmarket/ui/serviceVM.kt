package com.example.petmarket.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petmarket.model.Servicio
import com.example.petmarket.network.RetrofitClient
import kotlinx.coroutines.launch

class ServiceVM : ViewModel() {

    val servicios = mutableStateListOf<Servicio>()
    val cargando = mutableStateOf(false)

    val nombre = mutableStateOf("")
    val mascota = mutableStateOf("")

    init {
        cargarServicios()
    }

    fun cargarServicios() {
        viewModelScope.launch {
            cargando.value = true
            try {
                val data = RetrofitClient.instance.obtenerServicios()
                servicios.clear()
                servicios.addAll(data)
            } catch (_: Exception) { }
            cargando.value = false
        }
    }
    val randomDogUrl = mutableStateOf("")
    val loadingDog = mutableStateOf(false)

    fun cargarPerro() {
        viewModelScope.launch {
            try {
                loadingDog.value = true
                val response = RetrofitClient.instance.getRandomDog()
                randomDogUrl.value = response.message
            } catch (e: Exception) {
                randomDogUrl.value = ""
            } finally {
                loadingDog.value = false
            }
        }
    }

    fun solicitarServicio() {
        if (nombre.value.isBlank() || mascota.value.isBlank()) return

        val nuevo = Servicio(
            id = "",
            nombre = nombre.value,
            mascota = mascota.value,
            fecha = "Hoy",
            estado = "Pendiente"
        )

        viewModelScope.launch {
            try {
                RetrofitClient.instance.crearServicio(nuevo)

                // limpiar el formulario
                nombre.value = ""
                mascota.value = ""

                cargarServicios()
            } catch (_: Exception) { }
        }
    }
}
