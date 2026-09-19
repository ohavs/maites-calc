package com.example.maitescalc.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.maitescalc.ui.theme.AppColorPalette
import com.example.maitescalc.ui.theme.ColorPalettes
import com.example.maitescalc.util.PreferencesManager
import com.example.maitescalc.util.UpdateInfo
import com.example.maitescalc.util.UpdateManager
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
    val isAnonymous: Boolean = true,
    val currentVersion: String = UpdateManager.CURRENT_VERSION,
    // Update states
    val isCheckingUpdate: Boolean = false,
    val updateInfo: UpdateInfo? = null,
    val isDownloadingUpdate: Boolean = false,
    val downloadPercent: Int = 0,
    val downloadProgressText: String = "",
    val updateStatusMessage: String? = null
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val preferencesManager = PreferencesManager(application)
    private val updateManager = UpdateManager(application)
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

    fun checkForUpdates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isCheckingUpdate = true,
                updateStatusMessage = null
            )
            val result = updateManager.checkForUpdate()
            result.onSuccess { info ->
                _uiState.value = _uiState.value.copy(
                    isCheckingUpdate = false,
                    updateInfo = info,
                    updateStatusMessage = if (info.isUpdateAvailable) {
                        "נמצא עדכון חדש: ${info.latestVersion}"
                    } else {
                        "האפליקציה מעודכנת לגרסה האחרונה (${info.latestVersion})!"
                    }
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isCheckingUpdate = false,
                    updateStatusMessage = "שגיאה בבדיקת עדכונים: ${error.localizedMessage ?: "בדוק חיבור אינטרנט"}"
                )
            }
        }
    }

    fun downloadAndInstallUpdate(downloadUrl: String) {
        if (downloadUrl.isBlank()) {
            _uiState.value = _uiState.value.copy(updateStatusMessage = "לא נמצא קישור תקין לקובץ APK")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isDownloadingUpdate = true,
                downloadPercent = 0,
                downloadProgressText = "מתחיל בהורדה..."
            )

            val result = updateManager.downloadAndInstallApk(downloadUrl) { percent, bytesRead, totalBytes ->
                val mbRead = bytesRead.toDouble() / (1024 * 1024)
                val mbTotal = totalBytes.toDouble() / (1024 * 1024)
                _uiState.value = _uiState.value.copy(
                    downloadPercent = percent,
                    downloadProgressText = "${String.format("%.1f", mbRead)}MB / ${String.format("%.1f", mbTotal)}MB ($percent%)"
                )
            }

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isDownloadingUpdate = false,
                    updateStatusMessage = "ההורדה הושלמה! חלון ההתקנה נפתח."
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isDownloadingUpdate = false,
                    updateStatusMessage = "שגיאה בהורדת העדכון: ${error.localizedMessage}"
                )
            }
        }
    }

    fun signOut(onComplete: () -> Unit) {
        auth.signOut()
        onComplete()
    }
}
