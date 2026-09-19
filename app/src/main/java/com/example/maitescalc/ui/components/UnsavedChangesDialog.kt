package com.example.maitescalc.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable

@Composable
fun UnsavedChangesDialog(
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmDialog(
        title = "שינויים לא נשמרו",
        message = "יש לך שינויים שלא נשמרו. האם ברצונך לשמור אותם לפני היציאה?",
        confirmText = "שמור",
        dismissText = "מחק שינויים",
        icon = Icons.Default.Edit,
        isDestructive = false,
        onConfirm = onSave,
        onDismiss = {
            onDiscard()
        }
    )
}
