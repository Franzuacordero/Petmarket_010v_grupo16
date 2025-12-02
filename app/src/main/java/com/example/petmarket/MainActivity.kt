package com.example.petmarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.petmarket.ui.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {

                val nav = rememberNavController()

                NavHost(
                    navController = nav,
                    startDestination = "login"
                ) {

                    // login
                    composable("login") {
                        LoginScreen(nav)
                    }

                    // admin
                    composable("admin") {
                        AdminScreen()
                    }

                    // cliente
                    composable("store") { ClientLayout(nav) { StoreScreen() } }

                    composable("forum") { ClientLayout(nav) { ForumScreen() } }

                    composable("services") { ClientLayout(nav) { ServiceScreen() } }

                    composable("bookings") { ClientLayout(nav) { BookingScreen() } }

                    composable("booking") { ClientLayout(nav) { BookingScreen() } }
                }
            }
        }
    }
}

@Composable
fun ClientLayout(nav: NavController, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
        BottomNavBar(nav)
    }
}
