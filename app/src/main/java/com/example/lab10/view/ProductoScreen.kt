package com.example.lab10.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lab10.data.ProductoModel
import com.example.lab10.data.RetrofitClient

@Composable
fun ProductoScreen() {
    var listaProductos by remember { mutableStateOf(listOf<ProductoModel>()) }

    LaunchedEffect(Unit) {
        try {
            listaProductos = RetrofitClient.apiService.getProductos()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(listaProductos) { producto ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Precio: S/ ${producto.precio}")
                    Text(text = "Stock: ${producto.stock}")
                    Text(text = "Categoría: ${producto.categoria}")
                }
            }
        }
    }
}