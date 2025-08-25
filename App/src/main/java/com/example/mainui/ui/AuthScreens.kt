package com.example.mainui.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mainui.TranquilBlue
import com.example.mainui.TranquilText
import com.example.mainui.Inter
import com.example.mainui.InterBold
import com.example.mainui.ScreenSurface
import com.example.mainui.TranquilSurface

@Composable
fun LoginScreen(
    darkMode: Boolean,
    authVm: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit
) = ScreenSurface(darkMode) {

    var email by rememberSaveable { mutableStateOf("") }
    var pwd by rememberSaveable { mutableStateOf("") }
    val ui by authVm.ui.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Welcome back",
            fontFamily = InterBold,
            color = Color.White,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F2F4)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pwd,
                    onValueChange = { pwd = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontFamily = Inter) }

                Button(
                    onClick = { authVm.signIn(email, pwd, onLoginSuccess) },
                    enabled = !ui.loading,
                    colors = ButtonDefaults.buttonColors(containerColor = TranquilBlue)
                ) { Text("Log in", fontFamily = Inter, color = Color.White) }

                TextButton(onClick = onGoToRegister) { Text("Create an account", fontFamily = Inter) }
            }
        }
    }
}

@Composable
fun RegisterScreen(
    darkMode: Boolean,
    authVm: AuthViewModel,
    onRegisterSuccess: () -> Unit,
) = ScreenSurface(darkMode) {

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var pwd by rememberSaveable { mutableStateOf("") }
    val ui by authVm.ui.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Create account",
            fontFamily = InterBold,
            color = Color.White,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F2F4)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pwd,
                    onValueChange = { pwd = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontFamily = Inter) }

                Button(
                    onClick = { authVm.signUp(name, email, pwd, onRegisterSuccess) },
                    enabled = name.isNotBlank() && email.isNotBlank() && pwd.length >= 6 && !ui.loading,
                    colors = ButtonDefaults.buttonColors(containerColor = TranquilBlue)
                ) { Text("Sign up", fontFamily = Inter, color = Color.White) }
            }
        }
    }
}

@Composable
fun EditProfileScreen(
    darkMode: Boolean,
    authVm: AuthViewModel,
    onDone: () -> Unit
) = ScreenSurface(darkMode) {
    val user by authVm.user.collectAsStateWithLifecycle()
    val ui by authVm.ui.collectAsStateWithLifecycle()

    var name by remember(user) { mutableStateOf(user?.displayName.orEmpty()) }

    val canSave = name.trim().isNotEmpty() &&
            name.trim() != (user?.displayName ?: "") &&
            !ui.loading

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Edit Profile", fontFamily = InterBold, color = Color.White,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize)

        Card(colors = CardDefaults.cardColors(containerColor = TranquilSurface),
            modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (ui.error != null) {
                    Text(ui.error!!, color = MaterialTheme.colorScheme.error, fontFamily = Inter)
                }

                Button(
                    onClick = { authVm.updateDisplayName(name) { onDone() } },
                    enabled = canSave,
                    colors = ButtonDefaults.buttonColors(containerColor = TranquilBlue)
                ) {
                    Text(if (ui.loading) "Saving…" else "Save",
                        fontFamily = Inter, color = Color.White)
                }
            }
        }
    }
}

private fun AuthViewModel.updateDisplayName(
    name: String,
    function: () -> Unit
) {
}
