package com.example.petmarket.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.petmarket.model.Product

@Composable
fun StoreScreen(
    isAdmin: Boolean,
    vm: StoreVm = viewModel()
) {
    // Campos para formulario admin
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var rentable by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Para mostrar/ocultar la ventanita del carrito
    var showCartDialog by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Título
        Text(
            "Tienda de Mascotas",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        // --- bloque admin: agregar productos ---
        if (isAdmin) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Admin: agregar producto",
                        style = MaterialTheme.typography.titleMedium
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Precio (en pesos, sin puntos)") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Stock") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rentable,
                            onCheckedChange = { rentable = it }
                        )
                        Text("Rentable (arriendo)")
                    }

                    if (error != null) {
                        Text(
                            error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = {
                            val priceLong = price.toLongOrNull() ?: -1
                            val stockInt = stock.toIntOrNull() ?: -1
                            if (name.isBlank() || desc.isBlank()
                                || priceLong <= 0 || stockInt < 0
                            ) {
                                error =
                                    "Revisa los datos. Precio > 0, stock ≥ 0, sin campos vacíos."
                            } else {
                                vm.addProduct(name, desc, priceLong, rentable, stockInt)
                                name = ""
                                desc = ""
                                price = ""
                                stock = ""
                                rentable = false
                                error = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Agregar producto")
                    }
                }
            }
        }

        // --- lista de productos ---
        Text(
            "Productos disponibles",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(vm.products) { p ->
                ProductItem(
                    product = p,
                    onAddToCart = { vm.add(p, 1) },
                    onDelete = { vm.deleteProduct(p.id) },
                    canAdd = vm.canAdd(p, 1),
                    isAdmin = isAdmin
                )
            }
        }

        // --- carrito /total ---
        val total = vm.total()
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Total carrito: $${total / 100}")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(
                    onClick = { if (vm.cart.isNotEmpty()) showCartDialog = true }
                ) {
                    Text("Ver carrito")
                }

                Button(
                    onClick = {
                        if (vm.cart.isNotEmpty()) {
                            showCartDialog = true
                        }
                    },
                    enabled = total > 0
                ) {
                    Text("Confirmar pedido")
                }
            }
        }
    }

    // --- dialogo con detalle del carrito ---
    if (showCartDialog) {
        AlertDialog(
            onDismissRequest = { showCartDialog = false },
            title = { Text("Carrito de compras") },
            text = {
                if (vm.cart.isEmpty()) {
                    Text("Tu carrito está vacío.")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        vm.cart.forEach { item ->
                            val subtotal = (item.product.priceCents * item.qty) / 100
                            Text("${item.qty} x ${item.product.name} - $$subtotal")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Total: $${vm.total() / 100}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.confirm()   // descuenta stock y limpia carrito
                        showCartDialog = false
                    },
                    enabled = vm.cart.isNotEmpty()
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCartDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
fun ProductItem(
    product: Product,
    onAddToCart: () -> Unit,
    onDelete: () -> Unit,
    canAdd: Boolean,
    isAdmin: Boolean
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(product.name, style = MaterialTheme.typography.titleMedium)
            Text(product.desc, style = MaterialTheme.typography.bodyMedium)
            Text("Precio: $${product.priceCents / 100}")
            Text("Stock: ${product.stock}")
            if (product.rentable) {
                Text(
                    "Disponible para arriendo",
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onAddToCart,
                    enabled = canAdd
                ) {
                    Text("Agregar al carrito")
                }

                if (isAdmin) {
                    TextButton(onClick = onDelete) {
                        Text("Eliminar producto")
                    }
                }
            }
        }
    }
}
