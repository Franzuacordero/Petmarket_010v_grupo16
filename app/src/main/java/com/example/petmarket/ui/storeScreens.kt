@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.petmarket.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.petmarket.model.Product
import kotlinx.coroutines.launch

@Composable
fun StoreScreen(vm: StoreVm = viewModel()) {
    val snackbar = remember { SnackbarHostState() }
    var showCart by remember { mutableStateOf(false) }

    if (showCart) {
        CartDialog(
            onClose = { showCart = false },
            vm = vm
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tienda de Mascotas") },
                actions = {
                    FilledTonalButton(onClick = { showCart = true }) {
                        Text("Carrito (${vm.cart.size})")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padd ->
        if (vm.products.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padd),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay productos disponibles", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            val scope = rememberCoroutineScope()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vm.products) { p ->
                    ProductCard(
                        product = p,
                        onAdd = { qty ->
                            if (vm.canAdd(p, qty)) {
                                vm.add(p, qty)
                                scope.launch {
                                    snackbar.showSnackbar("Agregado: ${p.name} ×$qty")
                                }
                            }
                        },
                        canAdd = { q -> vm.canAdd(p, q) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onAdd: (qty: Int) -> Unit,
    canAdd: (Int) -> Boolean
) {
    var qty by remember(product.id) { mutableStateOf(1) }
    val canAddNow by remember(product.stock, qty) { mutableStateOf(canAdd(qty)) }

    ElevatedCard(
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = product.desc,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "$${product.priceCents / 100}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(Modifier.height(10.dp))


            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip(
                    onClick = {},
                    label = { Text("Stock: ${product.stock}") },
                    enabled = false
                )
                if (product.rentable) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Arrendable") },
                        enabled = false
                    )
                }
            }

            Spacer(Modifier.height(12.dp))


            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                FilledTonalIconButton(
                    onClick = { if (qty > 1) qty-- },
                    enabled = qty > 1
                ) { Icon(Icons.Default.Remove, contentDescription = "Menos") }

                Text(
                    text = qty.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                FilledTonalIconButton(
                    onClick = { if (canAdd(qty + 1)) qty++ },
                    enabled = canAdd(qty + 1)
                ) { Icon(Icons.Default.Add, contentDescription = "Más") }

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = { onAdd(qty) },
                    enabled = canAddNow
                ) { Text(if (canAddNow) "Agregar" else "Sin stock") }
            }
        }
    }
}

@Composable
private fun CartDialog(onClose: () -> Unit, vm: StoreVm) {
    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            Button(
                onClick = { vm.confirm(); onClose() },
                enabled = vm.cart.isNotEmpty()
            ) { Text("Confirmar pedido") }
        },
        dismissButton = {
            TextButton(onClick = onClose) { Text("Cerrar") }
        },
        title = { Text("Carrito") },
        text = {
            if (vm.cart.isEmpty()) {
                Text("Tu carrito está vacío.")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    vm.cart.forEach { ci ->
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(ci.product.name, style = MaterialTheme.typography.titleSmall)
                                    Text("x${ci.qty} · $${ci.product.priceCents / 100}")
                                }
                                IconButton(onClick = { vm.remove(ci.product.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
                    Divider(Modifier.padding(vertical = 4.dp))
                    Text(
                        "Total: $${vm.total() / 100}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    )
}
