package com.example.petmarket.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.petmarket.model.Producto
import com.example.petmarket.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(vm: AdminVM = viewModel()) {

    Column(
        Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {

        Text("Admin — Agregar producto", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(12.dp))

        AdminForm(vm)

        Spacer(Modifier.height(16.dp))

        Text("Productos disponibles", style = MaterialTheme.typography.titleMedium)

        LazyColumn(Modifier.weight(1f)) {
            items(vm.productos) { p ->
                AdminProductCard(
                    p = p,
                    onDelete = { vm.deleteProduct(p.id!!) },
                    onEdit = { vm.startEditing(p) }
                )

            }
        }
    }
}

@Composable
fun AdminForm(vm: AdminVM) {
    OutlinedTextField(
        value = vm.name.value,
        onValueChange = { vm.name.value = it },
        label = { Text("Nombre") },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(8.dp))

    OutlinedTextField(
        value = vm.desc.value,
        onValueChange = { vm.desc.value = it },
        label = { Text("Descripción") },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(8.dp))

    OutlinedTextField(
        value = vm.price.value,
        onValueChange = { vm.price.value = it },
        label = { Text("Precio") },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(8.dp))

    OutlinedTextField(
        value = vm.stock.value,
        onValueChange = { vm.stock.value = it },
        label = { Text("Stock") },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(8.dp))

    OutlinedTextField(
        value = vm.image.value,
        onValueChange = { vm.image.value = it },
        label = { Text("URL Imagen (opcional)") },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(12.dp))

    Button(
        onClick = { vm.addProduct() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(if (vm.editingId.value == null) "Agregar producto" else "Guardar cambios")
    }

}

@Composable
fun AdminProductCard(
    p: Producto,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {

        Column(Modifier.padding(16.dp)) {

            Text(p.name, style = MaterialTheme.typography.titleMedium)
            Text(p.desc)
            Text("Precio: $${p.price}")
            Text("Stock: ${p.stock}")

            if (p.image.isNotEmpty()) {
                AsyncImage(
                    model = p.image,
                    contentDescription = null,
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth()
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar")
                Button(
                    onClick = { onEdit() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Editar")

                }

            }
        }
    }
}

class AdminVM : ViewModel() {

    val productos = mutableStateListOf<Producto>()

    val name = mutableStateOf("")
    val desc = mutableStateOf("")
    val price = mutableStateOf("")
    val stock = mutableStateOf("")
    val image = mutableStateOf("")
    var editingId = mutableStateOf<String?>(null)

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            try {
                val data = RetrofitClient.instance.getProductos()
                productos.clear()
                productos.addAll(data)
            } catch (_: Exception) {}
        }
    }

    fun addProduct() {
        if (name.value.isBlank() || price.value.isBlank() || stock.value.isBlank()) return

        val nuevo = Producto(
            id = editingId.value,      // si es edición usamos el ID existente
            name = name.value,
            desc = desc.value,
            price = price.value.toInt(),
            stock = stock.value.toInt(),
            image = image.value
        )

        viewModelScope.launch {
            try {
                if (editingId.value == null) {
                    // AGREGAR NUEVO
                    RetrofitClient.instance.addProducto(nuevo)
                } else {
                    // EDITAR EXISTENTE
                    RetrofitClient.instance.updateProducto(editingId.value!!, nuevo)
                }

                limpiarForm()
                cargarProductos()

            } catch (_: Exception) {}
        }
    }

    fun startEditing(p: Producto) {
        editingId.value = p.id
        name.value = p.name
        desc.value = p.desc
        price.value = p.price.toString()
        stock.value = p.stock.toString()
        image.value = p.image
    }



    fun deleteProduct(id: String) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.deleteProducto(id)
                cargarProductos()
            } catch (_: Exception) {}
        }
    }

    fun limpiarForm() {
        editingId.value = null
        name.value = ""
        desc.value = ""
        price.value = ""
        stock.value = ""
        image.value = ""
    }
}
