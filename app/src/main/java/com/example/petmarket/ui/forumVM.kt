package com.example.petmarket.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petmarket.model.Comentario
import com.example.petmarket.model.Foro
import com.example.petmarket.network.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ForumVM : ViewModel() {

    val temas = mutableStateListOf<Foro>()
    val comentarios = mutableStateListOf<Comentario>()

    val titulo = mutableStateOf("")
    val descripcion = mutableStateOf("")
    val nuevoComentario = mutableStateOf("")

    val selectedTema = mutableStateOf<Foro?>(null)

    val cargando = mutableStateOf(false)

    init {
        cargarTemas()
        cargarComentarios()
    }

    //  TEMAS

    fun cargarTemas() {
        viewModelScope.launch {
            cargando.value = true
            try {
                val data = RetrofitClient.instance.getForos()
                temas.clear()
                temas.addAll(data)
            } catch (_: Exception) {}
            cargando.value = false
        }
    }

    fun agregarTema() {
        if (titulo.value.isBlank() || descripcion.value.isBlank()) return

        val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val nuevo = Foro(
            titulo = titulo.value,
            contenido = descripcion.value,
            fecha = fecha
        )

        viewModelScope.launch {
            try {
                RetrofitClient.instance.addForo(nuevo)
                titulo.value = ""
                descripcion.value = ""
                cargarTemas()
            } catch (_: Exception) {}
        }
    }

    fun eliminarTema(id: String) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.deleteForo(id)
                cargarTemas()
                cargarComentarios()
                selectedTema.value = null
            } catch (_: Exception) {}
        }
    }

    //COMENTARIOS

    fun cargarComentarios() {
        viewModelScope.launch {
            try {
                val data = RetrofitClient.instance.getComentarios()
                comentarios.clear()
                comentarios.addAll(data)
            } catch (_: Exception) {}
        }
    }

    fun agregarComentario() {
        val tema = selectedTema.value ?: return
        if (nuevoComentario.value.isBlank()) return

        val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val comentario = Comentario(
            foroId = tema.id!!,
            texto = nuevoComentario.value,
            fecha = fecha
        )

        viewModelScope.launch {
            try {
                RetrofitClient.instance.addComentario(comentario)
                nuevoComentario.value = ""
                cargarComentarios()
            } catch (_: Exception) {}
        }
    }

    // Devuelve solo los comentarios del tema seleccionado
    fun comentariosDelTema(): List<Comentario> {
        val tema = selectedTema.value ?: return emptyList()
        return comentarios.filter { it.foroId == tema.id }
    }
}
