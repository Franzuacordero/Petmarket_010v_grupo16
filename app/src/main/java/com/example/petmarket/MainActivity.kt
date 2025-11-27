package com.example.petmarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.petmarket.ui.AuthScreen
import com.example.petmarket.ui.AuthVm
import com.example.petmarket.ui.BookingScreen
import com.example.petmarket.ui.ForumScreen
import com.example.petmarket.ui.ServicesScreen
import com.example.petmarket.ui.StoreScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppRoot()
        }
    }
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()

    val authVm: AuthVm = viewModel()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Barra visible cuando alguien está logueado (admin o cliente)
    val showBottomBar = authVm.isLoggedIn.value && currentRoute != "auth"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == "store",
                        onClick = {
                            navController.navigate("store") {
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                        label = { Text("Tienda") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "forum",
                        onClick = {
                            navController.navigate("forum") {
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.Forum, contentDescription = null) },
                        label = { Text("Foro") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "services",
                        onClick = {
                            navController.navigate("services") {
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.Build, contentDescription = null) },
                        label = { Text("Servicios") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "booking",
                        onClick = {
                            navController.navigate("booking") {
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.Event, contentDescription = null) },
                        label = { Text("Reservas") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "auth",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("auth") {
                // 👇 Le pasamos EL MISMO authVm a AuthScreen
                AuthScreen(
                    vm = authVm,
                    onLoggedIn = {
                        navController.navigate("store") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                )
            }

            composable("store") {
                StoreScreen(isAdmin = authVm.isAdmin.value)
            }
            composable("forum") { ForumScreen() }
            composable("services") { ServicesScreen() }
            composable("booking") { BookingScreen() }
        }
    }
}
