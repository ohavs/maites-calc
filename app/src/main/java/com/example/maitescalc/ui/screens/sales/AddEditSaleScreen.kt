package com.example.maitescalc.ui.screens.sales

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.example.maitescalc.data.model.Recipe
import com.example.maitescalc.data.model.Sale
import com.example.maitescalc.data.model.SaleItem
import com.example.maitescalc.data.model.SaleStatus
import com.example.maitescalc.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSaleScreen(
    saleId: String?,
    viewModel: SalesViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var name by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(SaleStatus.DRAFT) }
    var notes by remember { mutableStateOf("") }
    var saleItems by remember { mutableStateOf<List<SaleItem>>(emptyList()) }

    var isDirty by remember { mutableStateOf(false) }
    var showUnsavedDialog by remember { mutableStateOf(false) }
    var showAddRecipeDialog by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    // Load existing sale if editing
    LaunchedEffect(saleId, uiState.sales) {
        if (!saleId.isNullOrBlank()) {
            val existing = viewModel.getSale(saleId)
            if (existing != null) {
                name = existing.name
                customerName = existing.customerName
                customerPhone = existing.customerPhone
                status = existing.status
                notes = existing.notes
                saleItems = existing.items
                isDirty = false
            }
        }
    }

    // Dynamic totals calculation
    val totalCost = remember(saleItems) {
        saleItems.sumOf { it.costPerUnit * it.quantity }
    }
    val totalPrice = remember(saleItems) {
        saleItems.sumOf { it.unitPrice * it.quantity }
    }
    val totalProfit = remember(totalPrice, totalCost) {
        (totalPrice - totalCost).coerceAtLeast(0.0)
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
                title = if (saleId.isNullOrBlank()) "יצירת מכירה / הזמנה" else "עריכת מכירה",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Financial Summary
            MaitesCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "סיכום פיננסי למכירה",
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
                                text = "סה\"כ הכנסה",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            PriceDisplay(price = totalPrice, large = true)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "רווח נקי",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            PriceDisplay(price = totalProfit, large = true, showBackground = true)
                        }
                    }

                    Text(
                        text = "עלות חומרי גלם כוללת: ₪${String.format("%.2f", totalCost)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // General Fields
            MaitesTextField(
                value = name,
                onValueChange = {
                    name = it
                    isDirty = true
                },
                label = "כותרת המכירה / אירוע *",
                placeholder = "למשל: הזמנת סופ\"ש - משפחת לוי"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MaitesTextField(
                    value = customerName,
                    onValueChange = {
                        customerName = it
                        isDirty = true
                    },
                    label = "שם הלקוח",
                    placeholder = "שם פרטי / משפחה",
                    modifier = Modifier.weight(1f)
                )

                MaitesTextField(
                    value = customerPhone,
                    onValueChange = {
                        customerPhone = it
                        isDirty = true
                    },
                    label = "טלפון לקוח",
                    placeholder = "050-0000000",
                    keyboardType = KeyboardType.Phone,
                    modifier = Modifier.weight(1f)
                )
            }

            // Status Dropdown
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = !statusExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                MaitesTextField(
                    value = status.displayName,
                    onValueChange = {},
                    label = "סטטוס הזמנה",
                    modifier = Modifier.menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    enabled = true
                )
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    SaleStatus.entries.forEach { s ->
                        DropdownMenuItem(
                            text = { Text(s.displayName) },
                            onClick = {
                                status = s
                                statusExpanded = false
                                isDirty = true
                            }
                        )
                    }
                }
            }

            // Recipe Items Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "פריטים ומנות בהזמנה (${saleItems.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Button(
                    onClick = { showAddRecipeDialog = true },
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
                    Text(text = "הוסף מנה", style = MaterialTheme.typography.labelSmall)
                }
            }

            // Items List
            if (saleItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "טרם נוספו פריטים למכירה זו. לחץ על 'הוסף מנה' כדי לבחור מתכונים.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    saleItems.forEachIndexed { index, item ->
                        SaleItemRow(
                            item = item,
                            onQuantityChange = { newQ ->
                                val updated = saleItems.toMutableList()
                                updated[index] = item.copy(
                                    quantity = newQ,
                                    totalPrice = newQ * item.unitPrice
                                )
                                saleItems = updated
                                isDirty = true
                            },
                            onPriceChange = { newPrice ->
                                val updated = saleItems.toMutableList()
                                updated[index] = item.copy(
                                    unitPrice = newPrice,
                                    totalPrice = item.quantity * newPrice
                                )
                                saleItems = updated
                                isDirty = true
                            },
                            onDelete = {
                                val updated = saleItems.toMutableList()
                                updated.removeAt(index)
                                saleItems = updated
                                isDirty = true
                            }
                        )
                    }
                }
            }

            // Notes
            MaitesTextField(
                value = notes,
                onValueChange = {
                    notes = it
                    isDirty = true
                },
                label = "הערות להזמנה",
                placeholder = "בקשות מיוחדות, כתובת משלוח וכו'...",
                singleLine = false,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Save Button
            MaitesPrimaryButton(
                text = if (saleId.isNullOrBlank()) "שמור מכירה חדשה" else "עדכן מכירה",
                onClick = {
                    if (name.isBlank()) return@MaitesPrimaryButton
                    val saleToSave = Sale(
                        id = saleId ?: "",
                        name = name.trim(),
                        items = saleItems,
                        totalCost = totalCost,
                        totalPrice = totalPrice,
                        profit = totalProfit,
                        status = status,
                        customerName = customerName.trim(),
                        customerPhone = customerPhone.trim(),
                        dueDate = 0L,
                        notes = notes.trim(),
                        createdAt = System.currentTimeMillis()
                    )
                    viewModel.saveSale(saleToSave) {
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

    // Dialog to pick a recipe to add to the sale
    if (showAddRecipeDialog) {
        SelectRecipeDialog(
            allRecipes = uiState.recipes,
            onSelect = { recipe ->
                val newItem = SaleItem(
                    recipeId = recipe.id,
                    recipeName = recipe.name,
                    quantity = 1,
                    unitPrice = recipe.suggestedPrice,
                    costPerUnit = recipe.costPerUnit,
                    totalPrice = recipe.suggestedPrice
                )
                saleItems = saleItems + newItem
                isDirty = true
                showAddRecipeDialog = false
            },
            onDismiss = { showAddRecipeDialog = false }
        )
    }

    // Unsaved Changes Dialog
    if (showUnsavedDialog) {
        UnsavedChangesDialog(
            onSave = {
                if (name.isNotBlank()) {
                    val saleToSave = Sale(
                        id = saleId ?: "",
                        name = name.trim(),
                        items = saleItems,
                        totalCost = totalCost,
                        totalPrice = totalPrice,
                        profit = totalProfit,
                        status = status,
                        customerName = customerName.trim(),
                        customerPhone = customerPhone.trim(),
                        dueDate = 0L,
                        notes = notes.trim(),
                        createdAt = System.currentTimeMillis()
                    )
                    viewModel.saveSale(saleToSave) {
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

@Composable
private fun SaleItemRow(
    item: SaleItem,
    onQuantityChange: (Int) -> Unit,
    onPriceChange: (Double) -> Unit,
    onDelete: () -> Unit
) {
    var quantityStr by remember(item.quantity) { mutableStateOf(item.quantity.toString()) }
    var priceStr by remember(item.unitPrice) { mutableStateOf(String.format("%.2f", item.unitPrice)) }

    val itemTotal = item.quantity * item.unitPrice
    val itemCost = item.quantity * item.costPerUnit
    val itemProfit = itemTotal - itemCost

    MaitesCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.recipeName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PriceDisplay(price = itemTotal, large = false, showBackground = true)
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "הסר",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = {
                        quantityStr = it
                        val q = it.toIntOrNull() ?: 1
                        onQuantityChange(q)
                    },
                    label = { Text("כמות") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = MaterialTheme.shapes.small
                )

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = {
                        priceStr = it
                        val p = it.toDoubleOrNull() ?: 0.0
                        onPriceChange(p)
                    },
                    label = { Text("מחיר ליח' (₪)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = MaterialTheme.shapes.small
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "עלות: ₪${String.format("%.2f", itemCost)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "רווח לשורה: ₪${String.format("%.2f", itemProfit)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun SelectRecipeDialog(
    allRecipes: List<Recipe>,
    onSelect: (Recipe) -> Unit,
    onDismiss: () -> Unit
) {
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
                    text = "בחר מנה מהמתכונים",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (allRecipes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "טרם יצרת מתכונים. הוסף מתכונים תחילה.",
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
                        allRecipes.forEach { recipe ->
                            Card(
                                onClick = { onSelect(recipe) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.small,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = recipe.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "עלות למנה: ₪${String.format("%.2f", recipe.costPerUnit)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    PriceDisplay(price = recipe.suggestedPrice, large = false)
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    MaitesSecondaryButton(text = "ביטול", onClick = onDismiss)
                }
            }
        }
    }
}
