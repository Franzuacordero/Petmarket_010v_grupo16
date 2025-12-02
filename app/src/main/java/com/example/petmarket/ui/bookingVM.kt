package com.example.petmarket.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class Professional(
    val id: String,
    val name: String,
    val specialty: String,
)

data class Booking(
    val id: String,
    val professionalName: String,
    val hour: String,
    val clientName: String
)

class BookingVM : ViewModel() {

    // Profesionales fijos, como la app antigua
    val professionals = listOf(
        Professional("1", "Dra. Torres", "Veterinaria"),
        Professional("2", "Carlos López", "Peluquero canino")
    )

    // Horarios fijos
    val hours = listOf("10:00", "11:00", "12:00", "15:00", "16:00")

    val selectedProf = mutableStateOf<Professional?>(null)
    val selectedHour = mutableStateOf("")

    val clientName = mutableStateOf("")
    val bookings = mutableStateListOf<Booking>()

    fun reserve() {
        if (selectedProf.value != null &&
            selectedHour.value.isNotBlank() &&
            clientName.value.isNotBlank()
        ) {
            bookings.add(
                Booking(
                    id = System.currentTimeMillis().toString(),
                    professionalName = selectedProf.value!!.name,
                    hour = selectedHour.value,
                    clientName = clientName.value
                )
            )

            selectedHour.value = ""
        }
    }
}
