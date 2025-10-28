@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.petmarket.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun BookingScreen(vm: BookingVm = viewModel()) {
    var selectedProId by remember { mutableStateOf<Long?>(null) }
    var customer by remember { mutableStateOf("Cliente") }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Reservas / Agenda") }) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padd ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // -------- PROFESIONALES --------
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Profesionales",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                if (vm.professionals.isEmpty()) {
                    EmptyStateCard("No hay profesionales registrados.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(vm.professionals) { pro ->
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedProId = pro.id },
                                shape = MaterialTheme.shapes.large
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(
                                        pro.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        pro.specialty,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    if (selectedProId == pro.id) {
                                        AssistChip(
                                            onClick = {},
                                            label = { Text("Seleccionado") },
                                            enabled = false,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // -------- AGENDA Y RESERVAS --------
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Agenda",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                val slots = remember(selectedProId) {
                    selectedProId?.let { vm.slotsOf(it) } ?: emptyList()
                }

                if (selectedProId == null) {
                    InfoCard("Elige un profesional para ver horarios disponibles 🐾")
                } else if (slots.isEmpty()) {
                    EmptyStateCard("No hay horas disponibles.")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        items(slots) { s ->
                            ElevatedCard(
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            "${s.date} • ${s.time}",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                        Text(
                                            "Profesional ID: ${s.professionalId}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            vm.book(s.professionalId, s.id, customer)
                                            scope.launch {
                                                snackbar.showSnackbar("Hora reservada ✅")
                                            }
                                        }
                                    ) {
                                        Text("Reservar")
                                    }
                                }
                            }
                        }
                    }
                }

                // -------- RESERVAS HECHAS --------
                Text(
                    "Mis reservas",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                if (vm.appointments.isEmpty()) {
                    InfoCard("No tienes reservas todavía.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(vm.appointments) { a ->
                            ElevatedCard(
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            "#${a.id} • ${a.status}",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                        Text(
                                            "Pro ${a.professionalId} • Slot ${a.slotId}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                    TextButton(
                                        onClick = {
                                            vm.cancel(a.id)
                                            scope.launch {
                                                snackbar.showSnackbar("Reserva cancelada ❌")
                                            }
                                        }
                                    ) {
                                        Text("Cancelar")
                                    }
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = customer,
                    onValueChange = { customer = it },
                    label = { Text("Tu nombre / mascota") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
