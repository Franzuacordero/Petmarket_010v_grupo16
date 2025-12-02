package com.example.petmarket.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.petmarket.model.CartItem
import com.example.petmarket.model.Pedido
import com.example.petmarket.model.Producto
import com.example.petmarket.network.RetrofitClient
import kotlinx.coroutines.launch

// ==================== STORE SCREEN (MEJORADA) =========================

@Composable
fun StoreScreen(vm: StoreVM = viewModel()) {

    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {

        // Título bonito
        Text(
            "Tienda",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Loader
        if (vm.loading.value) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Lista moderna con espaciado
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(vm.productos) { p ->
                    ProductCardClean(
                        p = p,
                        onAdd = { vm.addToCart(p) }
                    )
                }
            }
        }

        // Divider entre lista y total
        Divider(Modifier.padding(vertical = 12.dp))

        // Total destacado
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total:", style = MaterialTheme.typography.titleMedium)
            Text("$${vm.total.value}", style = MaterialTheme.typography.titleLarge)
        }

        Spacer(Modifier.height(10.dp))

        // Botón de confirmar
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = vm.total.value > 0
        ) {
            Text("Confirmar pedido")
        }

        // dialogo de resumen de pedido
        if (showDialog) {
            PedidoDialog(
                vm = vm,
                onDismiss = { showDialog = false },
                onConfirm = {
                    showDialog = false
                    vm.enviarPedido()
                }
            )
        }
    }
}

// product card

@Composable
fun ProductCardClean(
    p: Producto,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // Imagen grande y ordenada
            if (p.image.isNotEmpty()) {
                AsyncImage(
                    model = p.image,
                    contentDescription = p.name,
                    modifier = Modifier
                        .height(160.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }

            // Nombre del producto
            Text(
                text = p.name,
                style = MaterialTheme.typography.titleMedium
            )

            // Descripción
            Text(
                text = p.desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Precio
            Text(
                text = "Precio: $${p.price}",
                style = MaterialTheme.typography.titleMedium
            )

            // Stock
            Text(
                text = "Stock: ${p.stock}",
                style = MaterialTheme.typography.bodySmall
            )

            // Botón
            Button(
                onClick = onAdd,
                enabled = p.stock > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar al carrito")
            }
        }
    }
}

// pedido dialog
@Composable
fun PedidoDialog(
    vm: StoreVM,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Resumen del pedido") },
        text = {
            Column {

                Text(
                    "Productos seleccionados:",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                vm.cartItems.forEach { item ->
                    Text("- ${item.name}: $${item.price}")
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "Total a pagar: $${vm.total.value}",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Pagar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

//  viewmodel

class StoreVM : ViewModel() {

    val productos = mutableStateListOf<Producto>()
    val loading = mutableStateOf(false)

    val cartItems = mutableStateListOf<CartItem>()
    val total = mutableStateOf(0)

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            loading.value = true
            try {
                val data = RetrofitClient.instance.getProductos()
                productos.clear()
                productos.addAll(data)
            } catch (_: Exception) { }
            loading.value = false
        }
    }

    fun addToCart(p: Producto) {
        if (p.stock <= 0) return

        cartItems.add(CartItem(p.name, p.price))
        total.value = cartItems.sumOf { it.price }

        disminuirStock(p)
    }

    private fun disminuirStock(p: Producto) {
        val actualizado = p.copy(stock = p.stock - 1)

        viewModelScope.launch {
            try {
                RetrofitClient.instance.updateProducto(p.id!!, actualizado)
                cargarProductos()
            } catch (_: Exception) {}
        }
    }

    fun enviarPedido() {
        val pedido = Pedido(
            items = cartItems.toList(),
            total = total.value
        )

        viewModelScope.launch {
            try {
                RetrofitClient.instance.enviarPedido(pedido)
                cartItems.clear()
                total.value = 0
            } catch (_: Exception) { }
        }
    }
}
