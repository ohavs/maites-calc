package com.example.maitescalc.ui.screens.recipes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maitescalc.data.model.Ingredient
import com.example.maitescalc.data.model.Recipe
import com.example.maitescalc.data.model.RecipeIngredient
import com.example.maitescalc.data.model.UnitType
import com.example.maitescalc.ui.components.*
import com.example.maitescalc.util.CostCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecipeScreen(
    recipeId: String?,
    viewModel: RecipesViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(Recipe.categories.first()) }
    var description by remember { mutableStateOf("") }
    var yieldStr by remember { mutableStateOf("1") }
    var yieldUnit by remember { mutableStateOf("יחידות") }
    var profitMargin by remember { mutableFloatStateOf(40f) }
    var recipeIngredients by remember { mutableStateOf<List<RecipeIngredient>>(emptyList()) }
    var notes by remember { mutableStateOf("") }

    var isDirty by remember { mutableStateOf(false) }
    var showUnsavedDialog by remember { mutableStateOf(false) }
    var showAddIngredientDialog by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    // Load existing recipe if editing
    LaunchedEffect(recipeId, uiState.recipes) {
        if (!recipeId.isNullOrBlank()) {
            val existing = viewModel.getRecipe(recipeId)
            if (existing != null) {
                name = existing.name
                category = if (existing.category.isNotEmpty()) existing.category else Recipe.categories.first()
                description = existing.description
                yieldStr = existing.yield.toString()
                yieldUnit = existing.yieldUnit
                profitMargin = existing.profitMargin.toFloat()
                recipeIngredients = existing.ingredients
                notes = existing.notes
                isDirty = false
            }
        }
    }

    // Dynamic cost calculation based on current ingredients in real time
    val yieldAmount = (yieldStr.toIntOrNull() ?: 1).coerceAtLeast(1)
    val totalCost = remember(recipeIngredients, uiState.ingredients) {
        CostCalculator.calculateTotalCost(recipeIngredients, uiState.ingredients)
    }
    val costPerUnit = remember(totalCost, yieldAmount) {
        totalCost / yieldAmount
    }
    val suggestedPrice = remember(costPerUnit, profitMargin) {
        CostCalculator.calculateSuggestedPrice(costPerUnit, profitMargin.toDouble())
    }
    val profitPerUnit = remember(suggestedPrice, costPerUnit) {
        (suggestedPrice - costPerUnit).coerceAtLeast(0.0)
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
                title = if (recipeId.isNullOrBlank()) "יצירת מתכון חדש" else "עריכת מתכון",
                onBackClick = { handleBack() }
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
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Real-Time Cost & Profit Summary Card
            MaitesCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "תמחור ורווחיות בזמן אמת",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "עלות למנה / יחידה",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            PriceDisplay(price = costPerUnit, large = true)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "מחיר מכירה מומלץ",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            PriceDisplay(price = suggestedPrice, large = true, showBackground = true)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "סה\"כ עלות מתכון: ₪${String.format("%.2f", totalCost)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "רווח למנה: ₪${String.format("%.2f", profitPerUnit)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Profit margin slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "אחוז רווח מבוקש:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${profitMargin.toInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Slider(
                            value = profitMargin,
                            onValueChange = {
                                profitMargin = it
                                isDirty = true
                            },
                            valueRange = 0f..200f,
                            steps = 39,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }
            }

            // Recipe General Details
            MaitesTextField(
                value = name,
                onValueChange = {
                    name = it
                    isDirty = true
                },
                label = "שם המתכון / מנה *",
                placeholder = "למשל: עוגת שמרים שוקולד"
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
                    Recipe.categories.forEach { cat ->
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

            // Yield (Quantity & Unit)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MaitesTextField(
                    value = yieldStr,
                    onValueChange = {
                        yieldStr = it
                        isDirty = true
                    },
                    label = "תפוקת המתכון (כמות)",
                    placeholder = "למשל: 12",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )

                MaitesTextField(
                    value = yieldUnit,
                    onValueChange = {
                        yieldUnit = it
                        isDirty = true
                    },
                    label = "יחידת תפוקה",
                    placeholder = "יחידות / מנות / פרוסות",
                    modifier = Modifier.weight(1f)
                )
            }

            // Ingredients Section Header & Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "מצרכים במתכון (${recipeIngredients.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Button(
                    onClick = { showAddIngredientDialog = true },
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "הוסף מצרך", style = MaterialTheme.typography.labelSmall)
                }
            }

            // Ingredients List inside Recipe
            if (recipeIngredients.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "טרם נוספו מצרכים למתכון. לחץ על 'הוסף מצרך' כדי לבחור מצרכים מהמלאי שלך.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    recipeIngredients.forEachIndexed { index, recipeIngredient ->
                        val matchingIngredient = uiState.ingredients.find { it.id == recipeIngredient.ingredientId }
                        val itemCost = if (matchingIngredient != null) {
                            CostCalculator.calculateIngredientCost(recipeIngredient, matchingIngredient)
                        } else 0.0

                        RecipeIngredientRow(
                            recipeIngredient = recipeIngredient,
                            itemCost = itemCost,
                            onQuantityChange = { newQuantity ->
                                val updated = recipeIngredients.toMutableList()
                                updated[index] = recipeIngredient.copy(quantity = newQuantity)
                                recipeIngredients = updated
                                isDirty = true
                            },
                            onUnitChange = { newUnit ->
                                val updated = recipeIngredients.toMutableList()
                                updated[index] = recipeIngredient.copy(unitType = newUnit)
                                recipeIngredients = updated
                                isDirty = true
                            },
                            onDelete = {
                                val updated = recipeIngredients.toMutableList()
                                updated.removeAt(index)
                                recipeIngredients = updated
                                isDirty = true
                            }
                        )
                    }
                }
            }

            // Description / Instructions
            MaitesTextField(
                value = description,
                onValueChange = {
                    description = it
                    isDirty = true
                },
                label = "הוראות הכנה / הערות",
                placeholder = "שלבי הכנה, זמני אפייה, טיפים...",
                singleLine = false,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Save Recipe Button
            MaitesPrimaryButton(
                text = if (recipeId.isNullOrBlank()) "שמור מתכון חדש" else "עדכן מתכון",
                onClick = {
                    if (name.isBlank()) return@MaitesPrimaryButton
                    val recipeToSave = Recipe(
                        id = recipeId ?: "",
                        name = name.trim(),
                        category = category,
                        description = description.trim(),
                        ingredients = recipeIngredients,
                        yield = yieldAmount,
                        yieldUnit = yieldUnit.trim(),
                        totalCost = totalCost,
                        costPerUnit = costPerUnit,
                        suggestedPrice = suggestedPrice,
                        profitMargin = profitMargin.toDouble(),
                        notes = notes.trim(),
                        lastUpdated = System.currentTimeMillis()
                    )
                    viewModel.saveRecipe(recipeToSave) {
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

    // Dialog to pick an ingredient from user's inventory
    if (showAddIngredientDialog) {
        SelectIngredientDialog(
            allIngredients = uiState.ingredients,
            alreadySelectedIds = recipeIngredients.map { it.ingredientId }.toSet(),
            onSelect = { selectedIngredient ->
                val newRecipeIngredient = RecipeIngredient(
                    ingredientId = selectedIngredient.id,
                    ingredientName = selectedIngredient.name,
                    quantity = if (selectedIngredient.unitType == UnitType.KG) 250.0 else 1.0,
                    unitType = if (selectedIngredient.unitType == UnitType.KG) UnitType.GRAM else selectedIngredient.unitType
                )
                recipeIngredients = recipeIngredients + newRecipeIngredient
                isDirty = true
                showAddIngredientDialog = false
            },
            onDismiss = { showAddIngredientDialog = false }
        )
    }

    // Unsaved Changes Dialog
    if (showUnsavedDialog) {
        UnsavedChangesDialog(
            onSave = {
                if (name.isNotBlank()) {
                    val recipeToSave = Recipe(
                        id = recipeId ?: "",
                        name = name.trim(),
                        category = category,
                        description = description.trim(),
                        ingredients = recipeIngredients,
                        yield = yieldAmount,
                        yieldUnit = yieldUnit.trim(),
                        totalCost = totalCost,
                        costPerUnit = costPerUnit,
                        suggestedPrice = suggestedPrice,
                        profitMargin = profitMargin.toDouble(),
                        notes = notes.trim(),
                        lastUpdated = System.currentTimeMillis()
                    )
                    viewModel.saveRecipe(recipeToSave) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeIngredientRow(
    recipeIngredient: RecipeIngredient,
    itemCost: Double,
    onQuantityChange: (Double) -> Unit,
    onUnitChange: (UnitType) -> Unit,
    onDelete: () -> Unit
) {
    var unitMenuExpanded by remember { mutableStateOf(false) }
    var quantityStr by remember(recipeIngredient.quantity) {
        mutableStateOf(if (recipeIngredient.quantity % 1.0 == 0.0) recipeIngredient.quantity.toInt().toString() else recipeIngredient.quantity.toString())
    }

    MaitesCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recipeIngredient.ingredientName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PriceDisplay(price = itemCost, large = false, showBackground = true)
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "הסר",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Quantity and Unit inputs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = {
                        quantityStr = it
                        val q = it.toDoubleOrNull() ?: 0.0
                        onQuantityChange(q)
                    },
                    label = { Text("כמות") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = MaterialTheme.shapes.small
                )

                // Unit Type Selector
                ExposedDropdownMenuBox(
                    expanded = unitMenuExpanded,
                    onExpandedChange = { unitMenuExpanded = !unitMenuExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = recipeIngredient.unitType.shortName,
                        onValueChange = {},
                        label = { Text("יחידה") },
                        readOnly = true,
                        modifier = Modifier.menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitMenuExpanded) },
                        shape = MaterialTheme.shapes.small
                    )
                    ExposedDropdownMenu(
                        expanded = unitMenuExpanded,
                        onDismissRequest = { unitMenuExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        UnitType.entries.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text("${unit.displayName} (${unit.shortName})") },
                                onClick = {
                                    onUnitChange(unit)
                                    unitMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectIngredientDialog(
    allIngredients: List<Ingredient>,
    alreadySelectedIds: Set<String>,
    onSelect: (Ingredient) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val available = remember(allIngredients, alreadySelectedIds, searchQuery) {
        allIngredients.filter {
            !alreadySelectedIds.contains(it.id) &&
                    (searchQuery.isBlank() || it.name.contains(searchQuery, true) || it.brand.contains(searchQuery, true))
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "בחר מצרך מהמלאי",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                MaitesSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "חיפוש מצרך..."
                )

                if (available.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (allIngredients.isEmpty()) "אין מצרכים במלאי. הוסף מצרכים תחילה."
                            else "כל המצרכים כבר נוספו או לא נמצאו תוצאות",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        available.forEach { ing ->
                            Card(
                                onClick = { onSelect(ing) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.small,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = ing.name,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (ing.brand.isNotEmpty()) {
                                            Text(
                                                text = ing.brand,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    PriceDisplay(price = ing.calculatedPricePerUnit, large = false)
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    MaitesSecondaryButton(
                        text = "ביטול",
                        onClick = onDismiss
                    )
                }
            }
        }
    }
}
