package com.example.maitescalc.ui.screens.ingredients

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maitescalc.data.model.Ingredient
import com.example.maitescalc.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientsScreen(
    viewModel: IngredientsViewModel = viewModel(),
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var ingredientToDelete by remember { mutableStateOf<Ingredient?>(null) }

    // Categories list for filtering
    val categories = remember { listOf("הכל") + Ingredient.categories }

    Scaffold(
        topBar = {
            MaitesTopBar(
                title = "מצרכים",
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(
                            imageVector = Icons.Filled.CloudDownload,
                            contentDescription = "חיפוש ב-API",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Filled.Add, contentDescription = "הוסף מצרך חדש")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            MaitesSearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::setSearchQuery,
                placeholder = "חיפוש מצרך לפי שם, מותג או ברקוד...",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val selected = uiState.selectedCategory == category
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.setSelectedCategory(category) },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = MaterialTheme.shapes.small
                    )
                }
            }

            // Content List or Empty State
            if (uiState.isLoading) {
                LoadingState(message = "טוען מצרכים...")
            } else if (uiState.filteredIngredients.isEmpty()) {
                EmptyState(
                    icon = Icons.Filled.Inventory2,
                    title = if (uiState.searchQuery.isNotEmpty()) "לא נמצאו תוצאות" else "אין מצרכים במערכת",
                    subtitle = if (uiState.searchQuery.isNotEmpty())
                        "נסה לשנות את מילות החיפוש"
                    else
                        "התחל להוסיף מצרכים כדי לחשב עלויות של מתכונים",
                    action = {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            MaitesPrimaryButton(
                                text = "הוסף מצרך",
                                onClick = onNavigateToAdd,
                                icon = Icons.Filled.Add
                            )
                            MaitesSecondaryButton(
                                text = "ייבא מ-API",
                                onClick = onNavigateToSearch,
                                icon = Icons.Filled.CloudDownload
                            )
                        }
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        items = uiState.filteredIngredients,
                        key = { it.id.ifEmpty { it.name + it.lastUpdated } }
                    ) { ingredient ->
                        IngredientCard(
                            ingredient = ingredient,
                            onClick = { onNavigateToEdit(ingredient.id) },
                            onDelete = { ingredientToDelete = ingredient }
                        )
                    }
                }
            }
        }
    }

    // Confirmation Dialog for Delete
    ingredientToDelete?.let { ingredient ->
        ConfirmDialog(
            title = "מחיקת מצרך",
            message = "האם אתה בטוח שברצונך למחוק את '${ingredient.name}'? שים לב: מתכונים שמבוססים על מצרך זה יושפעו.",
            confirmText = "מחק",
            dismissText = "ביטול",
            isDestructive = true,
            onConfirm = {
                viewModel.deleteIngredient(ingredient.id)
                ingredientToDelete = null
            },
            onDismiss = { ingredientToDelete = null }
        )
    }
}

@Composable
private fun IngredientCard(
    ingredient: Ingredient,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    MaitesCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ingredient.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (ingredient.brand.isNotEmpty()) {
                        Text(
                            text = ingredient.brand,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (ingredient.category.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = ingredient.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Price display per base unit
                Column(horizontalAlignment = Alignment.End) {
                    PriceDisplay(
                        price = ingredient.calculatedPricePerUnit,
                        large = false,
                        showBackground = true
                    )
                    Text(
                        text = "ל-${ingredient.unitType.displayName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Package info and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (ingredient.packageSize > 0 && ingredient.packagePrice > 0) {
                    Text(
                        text = "אריזה: ${ingredient.packageSize} ${ingredient.unitType.shortName} במחיר ₪${String.format("%.2f", ingredient.packagePrice)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "ערוך",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "מחק",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
