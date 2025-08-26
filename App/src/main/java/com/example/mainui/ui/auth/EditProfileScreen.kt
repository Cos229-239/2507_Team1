package com.example.mainui.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.example.mainui.ScreenSurface
import com.example.mainui.Inter
import com.example.mainui.InterBold
import com.example.mainui.TranquilBlue
import com.example.mainui.TranquilSurface
import com.example.mainui.TranquilText

@Composable
fun EditProfileScreen(
    darkMode: Boolean,
    authVm: AuthViewModel,
    onDone: () -> Unit
) = ScreenSurface(darkMode) {
    // Make sure we can re-enter the screen after a previous save
    LaunchedEffect(Unit) { authVm.resetSaveState() }

    // Observe VM state
    val user by authVm.user.collectAsStateWithLifecycle()
    val isSaving by authVm.isSaving.collectAsStateWithLifecycle()
    val saveError by authVm.saveError.collectAsStateWithLifecycle()
    val saveDone by authVm.saveDone.collectAsStateWithLifecycle()

    // Local field state, seeded from latest user snapshot
    var name by remember(user) { mutableStateOf(user?.displayName.orEmpty()) }

    // Pop the screen when save completes
    LaunchedEffect(saveDone) { if (saveDone) onDone() }

    val trimmed = name.trim()
    val canSave = trimmed.isNotEmpty() &&
            trimmed != (user?.displayName ?: "") &&
            !isSaving

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Edit Profile",
            fontFamily = InterBold,
            color = Color.White,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = TranquilSurface),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display name", fontFamily = Inter) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isSaving
                )

                if (saveError != null) {
                    Text(
                        text = saveError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontFamily = Inter
                    )
                }

                Spacer(Modifier.height(4.dp))
                HorizontalDivider(thickness = 1.dp, color = TranquilBlue.copy(alpha = 0.35f))

                Button(
                    onClick = { authVm.onSaveProfile(trimmed) },
                    enabled = canSave,
                    colors = ButtonDefaults.buttonColors(containerColor = TranquilBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isSaving) "Saving…" else "Save",
                        fontFamily = Inter,
                        color = Color.White
                    )
                }
            }
        }
    }
}