@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.petmarket.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.ExperimentalMaterial3Api

@Composable
fun AuthScreen(
    vm: AuthVm = viewModel(),
    onLoggedIn: () -> Unit
) {
    val isLogged = vm.isLoggedIn.value
    LaunchedEffect(isLogged) {
        if (isLogged) onLoggedIn()
    }

    var isRegister by remember { mutableStateOf(false) }
    var showReset by remember { mutableStateOf(false) }
    val error = vm.error.value
    val info = vm.info.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isRegister) "Crear cuenta" else "Iniciar sesión")
                }
            )
        }
    ) { padd ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padd)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = vm.email.value,
                onValueChange = { vm.email.value = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = vm.password.value,
                onValueChange = { vm.password.value = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            if (!isRegister) {
                Text(
                    "¿Olvidaste tu contraseña?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp)
                        .clickable { showReset = true }
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { if (isRegister) vm.register() else vm.login() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isRegister) "Crear cuenta" else "Entrar")
            }

            TextButton(onClick = { isRegister = !isRegister }) {
                Text(
                    if (isRegister)
                        "¿Ya tienes cuenta? Inicia sesión"
                    else
                        "¿Aún no tienes cuenta? Regístrate"
                )
            }

            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(error, color = MaterialTheme.colorScheme.error)
            }
            if (info != null) {
                Spacer(Modifier.height(8.dp))
                Text(info, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }

    if (showReset) {
        ResetDialog(
            onDismiss = { showReset = false },
            onSend = { email ->
                vm.resetPassword(email)
                showReset = false
            }
        )
    }
}

@Composable
private fun ResetDialog(
    onDismiss: () -> Unit,
    onSend: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onSend(email) }) { Text("Enviar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        title = { Text("Recuperar contraseña") },
        text = {
            Column {
                Text("Ingresa tu email. Si existe una cuenta, te enviaremos un enlace.")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true
                )
            }
        }
    )
}


