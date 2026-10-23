package com.example.lab10.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lab10.data.PostModel
import com.example.lab10.data.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun PostsScreen() {
    val scope = rememberCoroutineScope()
    val posts = remember { mutableStateListOf<PostModel>() }
    var mensaje by remember { mutableStateOf("Cargando...") }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf<PostModel?>(null) }

    // READ
    LaunchedEffect(Unit) {
        try {
            posts.addAll(RetrofitClient.api.getPosts().take(15))
            mensaje = ""
        } catch (e: Exception) {
            mensaje = "Error: ${e.message}"
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { editando = null; mostrarDialogo = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Posts (API JSONPlaceholder)", style = MaterialTheme.typography.titleLarge)
            if (mensaje.isNotEmpty()) Text(mensaje)
            LazyColumn {
                items(posts) { post ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("${post.id}. ${post.title}", style = MaterialTheme.typography.titleMedium)
                            Text(post.body, maxLines = 2, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { editando = post; mostrarDialogo = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = {
                            scope.launch {
                                try {
                                    // DELETE
                                    val r = RetrofitClient.api.deletePost(post.id)
                                    if (r.isSuccessful) { posts.remove(post); mensaje = "Eliminado" }
                                    else mensaje = "Error al eliminar: ${r.code()}"
                                } catch (e: Exception) { mensaje = "Error: ${e.message}" }
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }

    if (mostrarDialogo) {
        var titulo by remember { mutableStateOf(editando?.title ?: "") }
        var cuerpo by remember { mutableStateOf(editando?.body ?: "") }

        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text(if (editando == null) "Nuevo post" else "Editar post") },
            text = {
                Column {
                    OutlinedTextField(titulo, { titulo = it }, label = { Text("Título") })
                    OutlinedTextField(cuerpo, { cuerpo = it }, label = { Text("Contenido") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val actual = editando
                    mostrarDialogo = false
                    scope.launch {
                        try {
                            if (actual == null) {
                                // CREATE
                                val r = RetrofitClient.api.createPost(PostModel(1, 0, titulo, cuerpo))
                                if (r.isSuccessful) {
                                    r.body()?.let { posts.add(0, it) }
                                    mensaje = "Creado (POST)"
                                } else mensaje = "Error al crear: ${r.code()}"
                            } else {
                                // UPDATE
                                val nuevo = actual.copy(title = titulo, body = cuerpo)
                                val r = RetrofitClient.api.updatePost(actual.id, nuevo)
                                if (r.isSuccessful) {
                                    val i = posts.indexOf(actual)
                                    if (i >= 0) posts[i] = nuevo
                                    mensaje = "Actualizado (PUT)"
                                } else mensaje = "Error al actualizar: ${r.code()}"
                            }
                        } catch (e: Exception) { mensaje = "Error: ${e.message}" }
                    }
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") } }
        )
    }
}