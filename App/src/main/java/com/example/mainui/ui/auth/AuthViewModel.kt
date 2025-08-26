package com.example.mainui.ui.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    // --- UI state for login/register screens ---
    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui.asStateFlow()

    // --- Current user state we control (so we can refresh after profile updates) ---
    private val _user = MutableStateFlow<UserProfile?>(null)
    val user: StateFlow<UserProfile?> = _user.asStateFlow()

    // --- Save state for Edit Profile ---
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError.asStateFlow()

    private val _saveDone = MutableStateFlow(false)
    val saveDone: StateFlow<Boolean> = _saveDone.asStateFlow()

    private val _authReady = MutableStateFlow(false)
    val authReady: StateFlow<Boolean> = _authReady.asStateFlow()

    init {
        // Seed quickly so UI can avoid flashing Login if already signed in
        auth.currentUser?.let { u ->
            viewModelScope.launch {
                _user.value = UserProfile(
                    uid = u.uid,
                    displayName = u.displayName,
                    email = u.email,
                    createdAt = null
                )
            }
        }

        val listener = com.google.firebase.auth.FirebaseAuth.AuthStateListener { fa ->
            val u = fa.currentUser
            if (u == null) {
                _user.value = null
            } else {
                // Load Firestore-createdAt and make sure displayName is fresh
                viewModelScope.launch { refreshUser() }
            }
            // We’ve now determined auth state at least once
            _authReady.value = true
        }
        auth.addAuthStateListener(listener)
    }

    /**
     * Reload the FirebaseAuth user and fetch Firestore fields, then emit to _user.
     */
    suspend fun refreshUser() = withContext(Dispatchers.IO) {
        val u = auth.currentUser ?: run {
            _user.value = null
            return@withContext
        }
        // Reload auth profile to get the latest displayName/photo
        u.reload().await()

        // Get createdAt from Firestore, but don't fail the whole thing if missing
        val createdAt = try {
            db.collection("users").document(u.uid).get().await().getLong("createdAt")
        } catch (_: Throwable) {
            null
        }

        _user.value = UserProfile(
            uid = u.uid,
            displayName = u.displayName,
            email = u.email,
            createdAt = createdAt
        )
    }

    // ----------------- Auth actions (unchanged except for imports) -----------------

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
                    viewModelScope.launch { refreshUser() }
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
                                    viewModelScope.launch { refreshUser() }
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

    // ----------------- Edit Profile save flow -----------------

    /** Reset flags so the screen won't auto-pop when reopened. */
    fun resetSaveState() {
        _isSaving.value = false
        _saveError.value = null
        _saveDone.value = false
    }

    /** Called by the Save button. */
    fun onSaveProfile(displayName: String) {
        if (_isSaving.value) return
        _saveError.value = null
        _saveDone.value = false
        _isSaving.value = true

        viewModelScope.launch {
            Log.d("AuthVM", "onSaveProfile start")
            val result = saveProfile(displayName)
            Log.d("AuthVM", "onSaveProfile end: $result")
            _isSaving.value = false
            result
                .onSuccess {
                    // Refresh our user snapshot so all screens see the new name
                    viewModelScope.launch { refreshUser() }
                    _saveDone.value = true
                }
                .onFailure { _saveError.value = it.message ?: "Save failed" }
        }
    }

    /** Two-step save with per-step retries and timeouts. */
    private suspend fun saveProfile(displayName: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val user = Firebase.auth.currentUser ?: error("No signed in user")
                val newName = displayName.trim()
                val currentName = user.displayName.orEmpty()

                if (newName == currentName) return@runCatching Unit

                // Step 1: Auth update (2 tries)
                retry(times = 2) { attempt ->
                    Log.d("AuthVM", "updateProfile try=$attempt")
                    withTimeout(8_000) {
                        val req = UserProfileChangeRequest.Builder()
                            .setDisplayName(newName)
                            .build()
                        user.updateProfile(req).await()
                    }
                }

                // Step 2: Firestore mirror (2 tries)
                retry(times = 2) { attempt ->
                    Log.d("AuthVM", "firestore set try=$attempt")
                    withTimeout(12_000) {
                        val data = mapOf(
                            "displayName" to newName,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                        db.collection("users").document(user.uid)
                            .set(data, SetOptions.merge())
                            .await()
                    }
                }

                Unit
            }
        }

    // Simple retry helper
    private suspend fun <T> retry(times: Int, block: suspend (attempt: Int) -> T): T {
        var last: Throwable? = null
        repeat(times) { i ->
            try { return block(i + 1) } catch (t: Throwable) { last = t }
        }
        throw last ?: IllegalStateException("Unknown failure")
    }

    fun signOut() {
        auth.signOut()
        _user.value = null
    }
}