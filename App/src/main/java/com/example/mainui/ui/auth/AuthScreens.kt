package com.example.mainui.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController

// Input & IME
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

// Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

// Shared tokens/components
import com.example.mainui.FrostedCard
import com.example.mainui.NeonButton
import com.example.mainui.ScreenSurface
import com.example.mainui.ScreenWithHeader
import com.example.mainui.ui.theme.Inter
import com.example.mainui.ui.theme.InterBold
import com.example.mainui.ui.theme.PanelStroke
import com.example.mainui.ui.theme.PanelSurface
import com.example.mainui.ui.theme.TranquilBlue
import com.example.mainui.ui.theme.TranquilText

@Composable
fun LoginScreen(
    navController: NavHostController,
    darkMode: Boolean,
    authVm: AuthViewModel
) = ScreenWithHeader(
    navController = navController,
    darkMode = darkMode,
    screenLabel = "Sign In"
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var pwVisible by remember { mutableStateOf(false) }
    val ui by authVm.ui.collectAsStateWithLifecycle()

    FrostedCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", fontFamily = Inter) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TranquilBlue,
                    unfocusedBorderColor = PanelStroke,
                    focusedTextColor = TranquilText,
                    unfocusedTextColor = TranquilText,
                    focusedLabelColor = TranquilBlue,
                    unfocusedLabelColor = TranquilText.copy(alpha = 0.8f),
                    cursorColor = TranquilBlue
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", fontFamily = Inter) },
                singleLine = true,
                visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    val icon = if (pwVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    IconButton(onClick = { pwVisible = !pwVisible }) {
                        Icon(icon, contentDescription = if (pwVisible) "Hide password" else "Show password")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TranquilBlue,
                    unfocusedBorderColor = PanelStroke,
                    focusedTextColor = TranquilText,
                    unfocusedTextColor = TranquilText,
                    focusedLabelColor = TranquilBlue,
                    unfocusedLabelColor = TranquilText.copy(alpha = 0.8f),
                    cursorColor = TranquilBlue
                )
            )

            if (ui.error != null) {
                Text(
                    text = ui.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = Inter
                )
            }

            Spacer(Modifier.height(8.dp))

            NeonButton(
                onClick = {
                    authVm.signIn(email.trim(), password) {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                label = if (ui.loading) "Signing in…" else "Log in",
                modifier = Modifier.fillMaxWidth(0.6f),
                enabled = !ui.loading
            )
        }
    }

    TextButton(onClick = { navController.navigate("register") }) {
        Text("Create an account", fontFamily = InterBold, color = TranquilBlue)
    }
}

@Composable
fun RegisterScreen(
    navController: NavHostController,
    darkMode: Boolean,
    authVm: AuthViewModel,
    onRegisterSuccess: () -> Unit
) = ScreenWithHeader(
    navController = navController,
    darkMode = darkMode,
    screenLabel = "Register"
) {
    // When a user appears, leave this screen
    val user by authVm.user.collectAsStateWithLifecycle(initialValue = null)
    LaunchedEffect(user) { if (user != null) onRegisterSuccess() }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var pwVisible by remember { mutableStateOf(false) }
    var cpwVisible by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    val nameOk = name.trim().isNotEmpty()
    val emailOk = email.isNotBlank()
    val pwOk = password.length >= 6
    val match = password == confirm
    val canSubmit = nameOk && emailOk && pwOk && match && !isSubmitting

    FrostedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Display name", fontFamily = Inter, fontStyle = FontStyle.Italic, color = TranquilBlue) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TranquilBlue,
                    unfocusedBorderColor = PanelStroke,
                    focusedTextColor = TranquilText,
                    unfocusedTextColor = TranquilText,
                    focusedLabelColor = TranquilBlue,
                    unfocusedLabelColor = TranquilBlue,
                    cursorColor = TranquilBlue
                )
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", fontFamily = Inter, fontStyle = FontStyle.Italic, color = TranquilBlue) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TranquilBlue,
                    unfocusedBorderColor = PanelStroke,
                    focusedTextColor = TranquilText,
                    unfocusedTextColor = TranquilText,
                    focusedLabelColor = TranquilBlue,
                    unfocusedLabelColor = TranquilBlue,
                    cursorColor = TranquilBlue
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password (min 6 chars)", fontFamily = Inter, fontStyle = FontStyle.Italic, color = TranquilBlue) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    val icon = if (pwVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    IconButton(onClick = { pwVisible = !pwVisible }) {
                        Icon(icon, contentDescription = if (pwVisible) "Hide password" else "Show password")
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TranquilBlue,
                    unfocusedBorderColor = PanelStroke,
                    focusedTextColor = TranquilText,
                    unfocusedTextColor = TranquilText,
                    focusedLabelColor = TranquilBlue,
                    unfocusedLabelColor = TranquilBlue,
                    cursorColor = TranquilBlue
                )
            )

            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it },
                label = { Text("Confirm password", fontFamily = Inter, fontStyle = FontStyle.Italic, color = TranquilBlue) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (cpwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    val icon = if (cpwVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    IconButton(onClick = { cpwVisible = !cpwVisible }) {
                        Icon(icon, contentDescription = if (cpwVisible) "Hide password" else "Show password")
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TranquilBlue,
                    unfocusedBorderColor = PanelStroke,
                    focusedTextColor = TranquilText,
                    unfocusedTextColor = TranquilText,
                    focusedLabelColor = TranquilBlue,
                    unfocusedLabelColor = TranquilBlue,
                    cursorColor = TranquilBlue
                )
            )

            if (!match && confirm.isNotEmpty()) {
                Text("Passwords don’t match", color = MaterialTheme.colorScheme.error, fontFamily = Inter)
            }

            Spacer(Modifier.height(6.dp))

            NeonButton(
                onClick = {
                    if (!canSubmit) return@NeonButton
                    isSubmitting = true
                    authVm.signUp(
                        displayName = name.trim(),
                        email = email.trim(),
                        password = password
                    ) {
                        isSubmitting = false
                        onRegisterSuccess()
                    }
                },
                label = if (isSubmitting) "Creating…" else "Create Account",
                modifier = Modifier.fillMaxWidth(0.6f),
                enabled = canSubmit
            )
        }
    }
}