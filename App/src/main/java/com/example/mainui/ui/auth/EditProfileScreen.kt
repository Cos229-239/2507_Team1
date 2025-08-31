package com.example.mainui.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.navigation.NavHostController
import com.example.mainui.NeonButton

import com.example.mainui.ScreenSurface
import com.example.mainui.ui.theme.Inter
import com.example.mainui.ui.theme.InterBold
import com.example.mainui.ui.theme.TranquilBlue
import com.example.mainui.ui.theme.TranquilText
import com.example.mainui.ui.theme.TranquilSurface
import com.example.mainui.ui.theme.PanelSurface
import com.example.mainui.ui.theme.PanelStroke
import com.example.mainui.ScreenWithHeader

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mainui.BackBar
import com.example.mainui.FrostedCard
import com.example.mainui.NeonButton
import com.example.mainui.ScreenWithHeader
import com.example.mainui.ui.theme.*

@Composable
fun EditProfileScreen(
    navController: NavHostController,
    darkMode: Boolean,
    authVm: AuthViewModel,
    onDone: () -> Unit
) = ScreenWithHeader(
    navController = navController,
    darkMode = darkMode,
    screenLabel = "Edit Profile"
) {
    // Ensure we don't auto-pop if returning to this screen
    LaunchedEffect(Unit) { authVm.resetSaveState() }

    // VM state
    val user by authVm.user.collectAsStateWithLifecycle()
    val isSaving by authVm.isSaving.collectAsStateWithLifecycle()
    val saveError by authVm.saveError.collectAsStateWithLifecycle()
    val saveDone by authVm.saveDone.collectAsStateWithLifecycle()

    // Local field state from latest user snapshot
    var name by remember(user) { mutableStateOf(user?.displayName.orEmpty()) }
    val trimmed = name.trim()
    val canSave = trimmed.isNotEmpty() &&
            trimmed != (user?.displayName ?: "") &&
            !isSaving

    // Pop when save completes
    LaunchedEffect(saveDone) { if (saveDone) onDone() }

    // -------- panel (no extra Card/Surface!) --------
    FrostedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = {
                    Text(
                        "Display name",
                        fontFamily = Inter,
                        fontStyle = FontStyle.Italic,
                        color = TranquilBlue
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isSaving,
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

            if (saveError != null) {
                Text(
                    text = saveError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = Inter
                )
            }

            Spacer(Modifier.height(6.dp))

            NeonButton(
                onClick = { authVm.onSaveProfile(trimmed) },
                label = if (isSaving) "Saving…" else "Save",
                modifier = Modifier.fillMaxWidth(0.6f),
                enabled = canSave
            )
        }
    }

    // Optional back button to match other screens
    BackBar(navController, label = "Back")
}