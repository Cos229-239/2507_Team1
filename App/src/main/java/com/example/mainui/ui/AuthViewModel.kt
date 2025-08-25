package com.example.mainui.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

data class UserProfile(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val createdAt: Long?        // epoch millis; stored in Firestore on sign up
)

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    // --- UI state (loading/error) ---
    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui.asStateFlow()

    // --- Dedicated Save state for Edit Profile ---
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError.asStateFlow()

    private val _saveDone = MutableStateFlow(false)
    val saveDone: StateFlow<Boolean> = _saveDone.asStateFlow()

    // --- Current user as StateFlow<UserProfile?> ---
    val user: StateFlow<UserProfile?> = authStateFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private fun authStateFlow(): Flow<UserProfile?> = callbackFlow {
        val listener = com.google.firebase.auth.FirebaseAuth.AuthStateListener { fa ->
            val u = fa.currentUser
            if (u == null) {
                trySend(null)
            } else {
                val uid = u.uid
                db.collection("users").document(uid).get()
                    .addOnSuccessListener { snap ->
                        val createdAt = snap.getLong("createdAt")
                        trySend(
                            UserProfile(
                                uid = uid,
                                displayName = u.displayName,
                                email = u.email,
                                createdAt = createdAt
                            )
                        )
                    }
                    .addOnFailureListener {
                        trySend(
                            UserProfile(
                                uid = uid,
                                displayName = u.displayName,
                                email = u.email,
                                createdAt = null
                            )
                        )
                    }
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    // --- Auth actions ---

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _ui.update { it.copy(error = "Email and password required") }
            return
        }
        _ui.update { it.copy(loading = true, error = null) }

        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _ui.update { it.copy(loading = false, error = null) }
                    onSuccess()
                } else {
                    _ui.update {
                        it.copy(
                            loading = false,
                            error = task.exception?.localizedMessage ?: "Login failed"
                        )
                    }
                }
            }
    }

    fun signUp(displayName: String, email: String, password: String, onSuccess: () -> Unit) {
        val name = displayName.trim()
        if (name.isBlank() || email.isBlank() || password.length < 6) {
            _ui.update { it.copy(error = "Enter name, email, and 6+ char password") }
            return
        }
        _ui.update { it.copy(loading = true, error = null) }

        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { createTask ->
                if (!createTask.isSuccessful) {
                    _ui.update {
                        it.copy(
                            loading = false,
                            error = createTask.exception?.localizedMessage ?: "Sign up failed"
                        )
                    }
                    return@addOnCompleteListener
                }

                val u = auth.currentUser ?: run {
                    _ui.update { it.copy(loading = false, error = "User not available after sign up") }
                    return@addOnCompleteListener
                }

                val now = System.currentTimeMillis()
                val req = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()

                u.updateProfile(req)
                    .addOnCompleteListener { upTask ->
                        if (!upTask.isSuccessful) {
                            _ui.update {
                                it.copy(
                                    loading = false,
                                    error = upTask.exception?.localizedMessage ?: "Failed to set display name"
                                )
                            }
                            return@addOnCompleteListener
                        }

                        val doc = mapOf(
                            "uid" to u.uid,
                            "displayName" to name,
                            "email" to u.email,
                            "createdAt" to now
                        )
                        db.collection("users").document(u.uid).set(doc)
                            .addOnCompleteListener { setTask ->
                                if (setTask.isSuccessful) {
                                    _ui.update { it.copy(loading = false, error = null) }
                                    onSuccess()
                                } else {
                                    _ui.update {
                                        it.copy(
                                            loading = false,
                                            error = setTask.exception?.localizedMessage
                                                ?: "Saved account but failed writing profile"
                                        )
                                    }
                                }
                            }
                    }
                    .addOnFailureListener { e ->
                        _ui.update {
                            it.copy(
                                loading = false,
                                error = e.localizedMessage ?: "Failed to set name"
                            )
                        }
                    }
            }
    }

    /***********************************************************
     *                 Path for Edit Profile:                  *
     * Updates displayName in both Firebase Auth and Firestore.*
     ***********************************************************/
    fun onSaveProfile(displayName: String) {
        if (_isSaving.value) return
        _saveError.value = null
        _saveDone.value = false
        _isSaving.value = true

        viewModelScope.launch {
            val result = saveProfile(displayName)
            _isSaving.value = false
            result
                .onSuccess { _saveDone.value = true }
                .onFailure { _saveError.value = it.message ?: "Save failed" }
        }
    }

    private suspend fun saveProfile(displayName: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                withTimeout(15_000) {
                    val user = Firebase.auth.currentUser ?: error("No signed in user")
                    val uid = user.uid

                    // Update Firebase Auth profile
                    val req = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName.trim())
                        .build()
                    user.updateProfile(req).await()

                    // Mirror to Firestore
                    val data = mapOf(
                        "displayName" to displayName.trim(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    db.collection("users").document(uid)
                        .set(data, SetOptions.merge()).await()

                    Unit
                }
            }
        }

    fun signOut() {
        auth.signOut()
    }
}