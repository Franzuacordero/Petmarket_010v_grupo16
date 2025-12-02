package com.example.petmarket.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.petmarket.model.Foro

@Composable
fun ForumScreen(vm: ForumVM = viewModel()) {

    Column(Modifier.fillMaxSize().padding(16.dp)) {

        Text(
            "Foro de Mascotas",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxSize()) {

            // ========================================================
            //  columna izquierda: lista de temas

            Column(
                Modifier.weight(0.45f)
            ) {

                Text(
                    "Temas recientes",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(10.dp))

                if (vm.cargando.value) {
                    CircularProgressIndicator()
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(vm.temas) { tema ->
                            ForoListItem(
                                tema = tema,
                                seleccionado = vm.selectedTema.value?.id == tema.id,
                                onClick = { vm.selectedTema.value = tema }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Divider()

                Spacer(Modifier.height(12.dp))

                // formulario para nuevo tema
                Text("Crear nuevo tema", style = MaterialTheme.typography.titleMedium)

                Spacer(Modifier.height(6.dp))

                OutlinedTextField(
                    value = vm.titulo.value,
                    onValueChange = { vm.titulo.value = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(6.dp))

                OutlinedTextField(
                    value = vm.descripcion.value,
                    onValueChange = { vm.descripcion.value = it },
                    label = { Text("Contenido") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { vm.agregarTema() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Publicar tema")
                }
            }

            Spacer(Modifier.width(16.dp))


            //  columna derecha: detalles del temas selecionado

            Column(
                Modifier.weight(0.55f)
            ) {

                val tema = vm.selectedTema.value

                if (tema == null) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color(0xFFEFEFEF), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Selecciona un tema para ver los detalles",
                            color = Color.Gray
                        )
                    }
                } else {
                    ForoDetalle(vm = vm, tema = tema)
                }
            }
        }
    }
}


//  item en la izquierda

@Composable
fun ForoListItem(
    tema: Foro,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = if (seleccionado)
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        else
            CardDefaults.cardColors()
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(tema.titulo, fontWeight = FontWeight.SemiBold)
            Text("Fecha: ${tema.fecha}", style = MaterialTheme.typography.bodySmall)
        }
    }
}


//  columna derecha: detalle + comentarios

@Composable
fun ForoDetalle(vm: ForumVM, tema: Foro) {

    Column(Modifier.fillMaxSize()) {

        Text(
            tema.titulo,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(6.dp))

        Text(
            tema.contenido,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(12.dp))

        Text("Comentarios", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(vm.comentariosDelTema()) { c ->
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(c.texto)
                        Spacer(Modifier.height(4.dp))
                        Text("Fecha: ${c.fecha}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = vm.nuevoComentario.value,
            onValueChange = { vm.nuevoComentario.value = it },
            label = { Text("Escribe un comentario…") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Row {
            Button(
                onClick = { vm.agregarComentario() }
            ) {
                Text("Enviar")
            }

            Spacer(Modifier.width(12.dp))

            Button(
                onClick = { vm.eliminarTema(tema.id!!) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar tema")
            }
        }
    }
}
