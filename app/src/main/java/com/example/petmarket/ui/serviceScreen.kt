package com.example.petmarket.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.petmarket.model.Servicio

@Composable
fun ServiceScreen(vm: ServiceVM = viewModel()) {

    Column(
        Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {

        Text("Servicios", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        // formulario para enviar solicitud
        OutlinedTextField(
            value = vm.nombre.value,
            onValueChange = { vm.nombre.value = it },
            label = { Text("Tipo de servicio (Ej: Baño, Corte)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = vm.mascota.value,
            onValueChange = { vm.mascota.value = it },
            label = { Text("Mascota / Cliente") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { vm.solicitarServicio() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Solicitar servicio")
        }

        Spacer(Modifier.height(20.dp))

        Divider()

        Spacer(Modifier.height(10.dp))

        Text("Mis solicitudes", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(10.dp))

        if (vm.cargando.value) {
            CircularProgressIndicator()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(vm.servicios) { s ->
                    ServicioCard(s)
                }
            }
        }


        // api externa — perro aleatorio


        Spacer(Modifier.height(24.dp))

        Text("perro aleatorio", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { vm.cargarPerro() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mostrar perro aleatorio 🐶")
        }

        Spacer(Modifier.height(16.dp))

        if (vm.loadingDog.value) {
            CircularProgressIndicator()
        }

        if (vm.randomDogUrl.value.isNotEmpty()) {
            AsyncImage(
                model = vm.randomDogUrl.value,
                contentDescription = "Random dog",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        }
    }
}

@Composable
fun ServicioCard(s: Servicio) {
    Card(
        Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(Modifier.padding(16.dp)) {

            Text("Servicio: ${s.nombre}", style = MaterialTheme.typography.titleMedium)
            Text("Mascota / Cliente: ${s.mascota}")
            Text("Fecha: ${s.fecha}")
            Text("Estado: ${s.estado}", color = MaterialTheme.colorScheme.primary)
        }
    }
}
