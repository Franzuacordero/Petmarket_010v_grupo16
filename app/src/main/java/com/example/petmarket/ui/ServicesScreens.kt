@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.petmarket.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment

@Composable
fun ServicesScreen(vm: ServicesVm = viewModel()) {
    var customer by remember { mutableStateOf("Cliente") }
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Servicios & Órdenes") }) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padd ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(padd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Servicios disponibles",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                if (vm.services.isEmpty()) {
                    EmptyStateCard("No hay servicios publicados aún.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(vm.services) { s ->
                            ElevatedCard(
                                shape = MaterialTheme.shapes.large,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(
                                        s.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "${s.durationMin} min · $${s.basePriceCents / 100}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            vm.create(s.id, customer)
                                            scope.launch {
                                                snackbar.showSnackbar("Orden creada para ${s.name}")
                                            }
                                        },
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text("Solicitar")
                                    }
                                }
                            }
                        }
                    }
                }
            }


            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Tus órdenes",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                if (vm.orders.isEmpty()) {
                    InfoCard("Aún no has solicitado ningún servicio.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(vm.orders) { o ->
                            ElevatedCard(
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(
                                        "Orden #${o.id}",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "Servicio ID: ${o.serviceId}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        "Cliente: ${o.customer}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        "Estado: ${o.status}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = when (o.status) {
                                            "finalizado" -> MaterialTheme.colorScheme.primary
                                            "en_proceso" -> MaterialTheme.colorScheme.tertiary
                                            else -> MaterialTheme.colorScheme.secondary
                                        }
                                    )
                                    Spacer(Modifier.height(12.dp))

                                    Button(
                                        enabled = o.status != "finalizado",
                                        onClick = {
                                            vm.advance(o.id)
                                            scope.launch {
                                                snackbar.showSnackbar("Estado actualizado")
                                            }
                                        },
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text("Avanzar estado")
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
