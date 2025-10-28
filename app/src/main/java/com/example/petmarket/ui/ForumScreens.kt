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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun ForumScreen(vm: ForumVm = viewModel()) {
    var cat by remember { mutableStateOf("General") }
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("Usuario") }
    var selectedTopicId by remember { mutableStateOf<Long?>(null) }

    var newComment by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Comunidad / Foro") }) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padd ->

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Temas",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                if (vm.topics.isEmpty()) {
                    EmptyStateCard(text = "Aún no hay temas. ¡Crea el primero!")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        items(vm.topics) { t ->
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedTopicId = t.id },
                                shape = MaterialTheme.shapes.large
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(
                                        t.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "${t.category} • por ${t.author}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Nueva discusión
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Crear tema",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                        )
                        OutlinedTextField(
                            value = cat,
                            onValueChange = { cat = it },
                            label = { Text("Categoría (ej: Preguntas, Noticias)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Título del tema") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = author,
                            onValueChange = { author = it },
                            label = { Text("Tu nombre / alias") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                if (title.isNotBlank()) {
                                    vm.addTopic(cat, title, author)
                                    title = ""
                                    scope.launch {
                                        snackbar.showSnackbar("Tema creado ✔")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Publicar tema")
                        }
                    }
                }
            }


            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val posts = remember(selectedTopicId) {
                    selectedTopicId?.let { vm.postsOf(it) } ?: emptyList()
                }

                Text(
                    "Comentarios",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                if (selectedTopicId == null) {
                    InfoCard("Selecciona un tema para ver la conversación 🐾")
                } else {
                    if (posts.isEmpty()) {
                        EmptyStateCard(text = "Aún no hay comentarios. ¡Sé el primero!")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            items(posts) { p ->
                                ElevatedCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text(
                                            p.author,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            p.body,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }


                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newComment,
                                onValueChange = { newComment = it },
                                label = { Text("Escribe un comentario...") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(
                                onClick = {
                                    if (selectedTopicId != null && newComment.isNotBlank()) {
                                        vm.addPost(selectedTopicId!!, author, newComment)
                                        newComment = ""
                                        scope.launch {
                                            snackbar.showSnackbar("Comentario publicado 💬")
                                        }
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



