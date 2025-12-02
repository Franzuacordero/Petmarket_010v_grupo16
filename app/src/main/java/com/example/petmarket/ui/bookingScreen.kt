package com.example.petmarket.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color

@Composable
fun BookingScreen(vm: BookingVM = viewModel()) {

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Reservas", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(10.dp))


        // profesionales

        Text("Profesionales", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        vm.professionals.forEach { prof ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable {
                        vm.selectedProf.value = prof
                    },
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(prof.name)
                    Text(prof.specialty)
                }
            }
        }

        Spacer(Modifier.height(20.dp))


        // horarios (solo si hay profesional seleccionado)

        val selected = vm.selectedProf.value
        if (selected != null) {

            Text("Horarios disponibles con ${selected.name}", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            vm.hours.forEach { hour ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            vm.selectedHour.value = hour
                        },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(hour)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = vm.clientName.value,
                onValueChange = { vm.clientName.value = it },
                label = { Text("Nombre del cliente / mascota") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = { vm.reserve() },
                modifier = Modifier.fillMaxWidth(),
                enabled = vm.selectedHour.value.isNotBlank() &&
                        vm.clientName.value.isNotBlank()
            ) {
                Text("Reservar")
            }

            Spacer(Modifier.height(20.dp))
        }


        // mis reservas

        Text("Mis reservas", style = MaterialTheme.typography.titleMedium)

        if (vm.bookings.isEmpty()) {
            Text("No hay reservas aún.", Modifier.padding(8.dp))
        } else {
            LazyColumn {
                items(vm.bookings.size) { i ->
                    val b = vm.bookings[i]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Profesional: ${b.professionalName}")
                            Text("Hora: ${b.hour}")
                            Text("Cliente: ${b.clientName}")
                        }
                    }
                }
            }
        }
    }
}
