package com.example.maitescalc.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.maitescalc.ui.theme.AppColorPalette
import com.example.maitescalc.ui.theme.ColorPalettes
import com.example.maitescalc.util.PreferencesManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val selectedPaletteId: String = "lime",
    val availablePalettes: List<AppColorPalette> = ColorPalettes.all,
    val userEmail: String = "",
    val displayName: String = "",
    val isAnonymous: Boolean = true
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val preferencesManager = PreferencesManager(application)
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        val user = auth.currentUser
        _uiState.value = _uiState.value.copy(
            userEmail = user?.email ?: "ללא דוא\"ל",
            displayName = user?.displayName ?: (if (user?.isAnonymous == true) "משתמש אורח" else "משתמש רשום"),
            isAnonymous = user?.isAnonymous ?: true
        )

        viewModelScope.launch {
            preferencesManager.selectedPaletteFlow.collect { paletteId ->
                _uiState.value = _uiState.value.copy(selectedPaletteId = paletteId)
            }
        }
    }

    fun selectPalette(paletteId: String) {
        viewModelScope.launch {
            preferencesManager.setSelectedPalette(paletteId)
        }
    }

    fun signOut(onComplete: () -> Unit) {
        auth.signOut()
        onComplete()
    }
}
