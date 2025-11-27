package com.example.petmarket.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.petmarket.model.Post

@Composable
fun ForumScreen(vm: ForumVm = viewModel()) {

    // Formulario para nuevo tema
    var cat by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var topicError by remember { mutableStateOf<String?>(null) }

    // Comentarios
    var selectedTopicId by remember { mutableStateOf<Long?>(null) }
    var commentAuthor by remember { mutableStateOf("") }
    var commentBody by remember { mutableStateOf("") }
    var commentError by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            "Foro de Mascotas",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        // ---- Formular io nuevo tema (arriba) ----
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
                    "Crear nuevo tema",
                    style = MaterialTheme.typography.titleMedium
                )
                OutlinedTextField(
                    value = cat,
                    onValueChange = { cat = it },
                    label = { Text("Categoría (ej: Adopciones, Salud)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título del tema") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Autor") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (topicError != null) {
                    Text(
                        topicError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = {
                        if (cat.isBlank() || title.isBlank() || author.isBlank()) {
                            topicError = "Todos los campos son obligatorios."
                        } else {
                            vm.addTopic(cat, title, author)
                            cat = ""
                            title = ""
                            author = ""
                            topicError = null
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Publicar tema")
                }
            }
        }

        // ---- Zona de contenidos: lista de temas + detalle ----
        Row(
            Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // --- Lista de temas (izquierda) ---
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Temas",
                    style = MaterialTheme.typography.titleMedium
                )

                if (vm.topics.isEmpty()) {
                    EmptyStateCard("No hay temas aún. Crea el primero.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(vm.topics) { t ->
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedTopicId = t.id },
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        t.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = if (selectedTopicId == t.id)
                                            FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        "Cat: ${t.category} • por ${t.author}",
                                        style = MaterialTheme.typography.bodySmall
                                    )

                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(onClick = {
                                            vm.deleteTopic(t.id)
                                            if (selectedTopicId == t.id) {
                                                selectedTopicId = null
                                            }
                                        }) {
                                            Text("Eliminar tema")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // -------- Detalle y comentarios (derecha) --------
            Column(
                modifier = Modifier.weight(1.3f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Comentarios",
                    style = MaterialTheme.typography.titleMedium
                )

                val currentId = selectedTopicId
                if (currentId == null) {
                    InfoCard("Selecciona un tema para ver y escribir comentarios.")
                } else {
                    val posts = vm.postsOf(currentId)
                    if (posts.isEmpty()) {
                        EmptyStateCard("Aún no hay comentarios en este tema.")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            items(posts) { p ->
                                CommentItem(
                                    post = p,
                                    onDelete = { vm.deletePost(p) }
                                )
                            }
                        }
                    }

                    // --- Formulario nuevo comentario ---
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Agregar comentario")

                            OutlinedTextField(
                                value = commentAuthor,
                                onValueChange = { commentAuthor = it },
                                label = { Text("Autor") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = commentBody,
                                onValueChange = { commentBody = it },
                                label = { Text("Comentario") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (commentError != null) {
                                Text(
                                    commentError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Button(
                                onClick = {
                                    if (commentAuthor.isBlank() || commentBody.isBlank()) {
                                        commentError = "Autor y comentario son obligatorios."
                                    } else {
                                        vm.addPost(currentId, commentAuthor, commentBody)
                                        commentAuthor = ""
                                        commentBody = ""
                                        commentError = null
                                    }
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Publicar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentItem(
    post: Post,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                post.author,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(post.body, style = MaterialTheme.typography.bodyMedium)

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDelete) {
                    Text("Eliminar comentario")
                }
            }
        }
    }
}
