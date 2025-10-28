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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*

import com.example.petmarket.ui.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val nav = rememberNavController()
    val authVm: AuthVm = viewModel()


    val startDest = if (authVm.isLoggedIn.value) "store" else "auth"


    val showBottomBar = true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val current by nav.currentBackStackEntryAsState()
                    val route = current?.destination?.route
                    listOf(
                        "store" to Icons.Default.ShoppingCart,
                        "forum" to Icons.Default.Forum,
                        "services" to Icons.Default.Build,
                        "booking" to Icons.Default.Event
                    ).forEach { (r, icon) ->
                        NavigationBarItem(
                            selected = (route == r),
                            onClick = {
                                nav.navigate(r) {
                                    popUpTo(nav.graph.startDestinationId) { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(icon, contentDescription = null) },
                            label = { Text(r) }
                        )
                    }
                }
            }
        }
    ) { padd ->
        NavHost(
            navController = nav,
            startDestination = startDest,
            modifier = Modifier.padding(padd)
        ) {

            composable("auth") {
                AuthScreen(
                    onLoggedIn = {
                        nav.navigate("store") {
                            popUpTo("auth") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }


            composable("store")   { StoreScreen() }
            composable("forum")   { ForumScreen() }
            composable("services"){ ServicesScreen() }
            composable("booking") { BookingScreen() }
        }
    }
}
