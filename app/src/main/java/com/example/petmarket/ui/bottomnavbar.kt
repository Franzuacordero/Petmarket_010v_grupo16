package com.example.petmarket.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.navigation.NavController

@Composable
fun BottomNavBar(nav: NavController) {

    NavigationBar {

        NavigationBarItem(
            selected = false,
            onClick = { nav.navigate("store") },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Tienda") },
            label = { Text("Tienda") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { nav.navigate("forum") },
            icon = { Icon(Icons.Default.Forum, contentDescription = "Foro") },
            label = { Text("Foro") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { nav.navigate("services") },
            icon = { Icon(Icons.Default.Build, contentDescription = "Servicios") },
            label = { Text("Servicios") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { nav.navigate("booking") },
            icon = { Icon(Icons.Default.Event, contentDescription = "Reservas") },
            label = { Text("Reservas") }
        )
    }
}
