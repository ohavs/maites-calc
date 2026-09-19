package com.example.maitescalc.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false,
    val errorMessage: String? = null
)

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        checkIfSignedIn()
    }

    private fun checkIfSignedIn() {
        val user = auth.currentUser
        _uiState.value = LoginUiState(isSignedIn = user != null)
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
                _uiState.value = LoginUiState(isLoading = false, isSignedIn = true)
            } catch (e: Exception) {
                _uiState.value = LoginUiState(
                    isLoading = false,
                    isSignedIn = false,
                    errorMessage = "שגיאה בהתחברות עם Google: ${e.localizedMessage}"
                )
            }
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                auth.signInAnonymously().await()
                _uiState.value = LoginUiState(isLoading = false, isSignedIn = true)
            } catch (e: Exception) {
                // If anonymous auth fails on Firebase project, allow continuing in local mode
                _uiState.value = LoginUiState(isLoading = false, isSignedIn = true)
            }
        }
    }
}
