package com.example.maitescalc.ui.screens.ingredients

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maitescalc.data.model.Ingredient
import com.example.maitescalc.data.model.UnitType
import com.example.maitescalc.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditIngredientSheet(
    ingredientId: String?,
    viewModel: IngredientsViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToSearch: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(Ingredient.categories.first()) }
    var unitType by remember { mutableStateOf(UnitType.KG) }
    var packagePriceStr by remember { mutableStateOf("") }
    var packageSizeStr by remember { mutableStateOf("") }
    var directPriceStr by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isDirty by remember { mutableStateOf(false) }
    var showUnsavedDialog by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var unitTypeExpanded by remember { mutableStateOf(false) }

    // Load existing ingredient if editing
    LaunchedEffect(ingredientId) {
        if (!ingredientId.isNullOrBlank()) {
            val existing = viewModel.getIngredient(ingredientId)
            if (existing != null) {
                name = existing.name
                brand = existing.brand
                category = if (existing.category.isNotEmpty()) existing.category else Ingredient.categories.first()
                unitType = existing.unitType
                if (existing.packagePrice > 0) packagePriceStr = existing.packagePrice.toString()
                if (existing.packageSize > 0) packageSizeStr = existing.packageSize.toString()
                if (existing.pricePerUnit > 0) directPriceStr = existing.pricePerUnit.toString()
                barcode = existing.barcode
                notes = existing.notes
                isDirty = false
            }
        }
    }

    // Live calculated price per unit
    val packagePrice = packagePriceStr.toDoubleOrNull() ?: 0.0
    val packageSize = packageSizeStr.toDoubleOrNull() ?: 0.0
    val calculatedPricePerUnit = remember(packagePrice, packageSize, directPriceStr) {
        if (packageSize > 0 && packagePrice > 0) {
            packagePrice / packageSize
        } else {
            directPriceStr.toDoubleOrNull() ?: 0.0
        }
    }

    fun handleBack() {
        if (isDirty) {
            showUnsavedDialog = true
        } else {
            onNavigateBack()
        }
    }

    BackHandler {
        handleBack()
    }

    Scaffold(
        topBar = {
            MaitesTopBar(
                title = if (ingredientId.isNullOrBlank()) "הוספת מצרך חדש" else "עריכת מצרך",
                onBackClick = { handleBack() },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(
                            imageVector = Icons.Filled.CloudDownload,
                            contentDescription = "ייבא מ-API",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Price Preview Card
            MaitesCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "מחיר מחושב ליחידת בסיס",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "לפי: 1 ${unitType.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    PriceDisplay(
                        price = calculatedPricePerUnit,
                        large = true,
                        showBackground = true
                    )
                }
            }

            // Name
            MaitesTextField(
                value = name,
                onValueChange = {
                    name = it
                    isDirty = true
                },
                label = "שם המצרך *",
                placeholder = "למשל: קמח לבן בהיר"
            )

            // Brand
            MaitesTextField(
                value = brand,
                onValueChange = {
                    brand = it
                    isDirty = true
                },
                label = "מותג / יצרן",
                placeholder = "למשל: הטחנות הגדולות"
            )

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                MaitesTextField(
                    value = category,
                    onValueChange = {},
                    label = "קטגוריה",
                    modifier = Modifier.menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    enabled = true
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    Ingredient.categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                categoryExpanded = false
                                isDirty = true
                            }
                        )
                    }
                }
            }

            // Unit Type Dropdown
            ExposedDropdownMenuBox(
                expanded = unitTypeExpanded,
                onExpandedChange = { unitTypeExpanded = !unitTypeExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                MaitesTextField(
                    value = "${unitType.displayName} (${unitType.shortName})",
                    onValueChange = {},
                    label = "יחידת מידה בסיסית",
                    modifier = Modifier.menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitTypeExpanded) },
                    enabled = true
                )
                ExposedDropdownMenu(
                    expanded = unitTypeExpanded,
                    onDismissRequest = { unitTypeExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    UnitType.entries.forEach { u ->
                        DropdownMenuItem(
                            text = { Text("${u.displayName} (${u.shortName})") },
                            onClick = {
                                unitType = u
                                unitTypeExpanded = false
                                isDirty = true
                            }
                        )
                    }
                }
            }

            // Package Price and Size (side-by-side or stacked)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MaitesTextField(
                    value = packagePriceStr,
                    onValueChange = {
                        packagePriceStr = it
                        isDirty = true
                    },
                    label = "מחיר אריזה (₪)",
                    placeholder = "למשל: 6.90",
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f)
                )

                MaitesTextField(
                    value = packageSizeStr,
                    onValueChange = {
                        packageSizeStr = it
                        isDirty = true
                    },
                    label = "גודל אריזה (${unitType.shortName})",
                    placeholder = "למשל: 1",
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f)
                )
            }

            // Notes
            MaitesTextField(
                value = notes,
                onValueChange = {
                    notes = it
                    isDirty = true
                },
                label = "הערות / קישור לרכישה",
                placeholder = "הערות חופשיות...",
                singleLine = false,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Save Button
            MaitesPrimaryButton(
                text = if (ingredientId.isNullOrBlank()) "שמור מצרך חדש" else "עדכן מצרך",
                onClick = {
                    if (name.isBlank()) return@MaitesPrimaryButton
                    val ingredientToSave = Ingredient(
                        id = ingredientId ?: "",
                        name = name.trim(),
                        brand = brand.trim(),
                        category = category,
                        pricePerUnit = calculatedPricePerUnit,
                        unitType = unitType,
                        packageSize = packageSize,
                        packagePrice = packagePrice,
                        barcode = barcode.trim(),
                        notes = notes.trim(),
                        lastUpdated = System.currentTimeMillis()
                    )
                    viewModel.saveIngredient(ingredientToSave) {
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank(),
                icon = Icons.Filled.Save
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showUnsavedDialog) {
        UnsavedChangesDialog(
            onSave = {
                if (name.isNotBlank()) {
                    val ingredientToSave = Ingredient(
                        id = ingredientId ?: "",
                        name = name.trim(),
                        brand = brand.trim(),
                        category = category,
                        pricePerUnit = calculatedPricePerUnit,
                        unitType = unitType,
                        packageSize = packageSize,
                        packagePrice = packagePrice,
                        barcode = barcode.trim(),
                        notes = notes.trim(),
                        lastUpdated = System.currentTimeMillis()
                    )
                    viewModel.saveIngredient(ingredientToSave) {
                        showUnsavedDialog = false
                        onNavigateBack()
                    }
                } else {
                    showUnsavedDialog = false
                    onNavigateBack()
                }
            },
            onDiscard = {
                showUnsavedDialog = false
                onNavigateBack()
            },
            onDismiss = { showUnsavedDialog = false }
        )
    }
}
