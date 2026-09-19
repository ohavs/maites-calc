package com.example.maitescalc.ui.screens.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maitescalc.data.model.Recipe
import com.example.maitescalc.ui.components.*

@Composable
fun RecipesScreen(
    viewModel: RecipesViewModel = viewModel(),
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var recipeToDelete by remember { mutableStateOf<Recipe?>(null) }

    val categories = remember { listOf("הכל") + Recipe.categories }

    Scaffold(
        topBar = {
            MaitesTopBar(
                title = "מתכונים ומנות",
                actions = {}
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Filled.Add, contentDescription = "הוסף מתכון חדש")
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
                placeholder = "חיפוש מתכון לפי שם...",
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

            // Content List
            if (uiState.isLoading) {
                LoadingState(message = "טוען מתכונים...")
            } else if (uiState.filteredRecipes.isEmpty()) {
                EmptyState(
                    icon = Icons.Filled.MenuBook,
                    title = if (uiState.searchQuery.isNotEmpty()) "לא נמצאו תוצאות" else "אין מתכונים עדיין",
                    subtitle = if (uiState.searchQuery.isNotEmpty())
                        "נסה לשנות את מילות החיפוש"
                    else
                        "הרכב את המתכון הראשון שלך וחשב עלות מדויקת לכל מנה",
                    action = {
                        MaitesPrimaryButton(
                            text = "צור מתכון ראשון",
                            onClick = onNavigateToAdd,
                            icon = Icons.Filled.Add
                        )
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        items = uiState.filteredRecipes,
                        key = { it.id.ifEmpty { it.name + it.lastUpdated } }
                    ) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = { onNavigateToDetail(recipe.id) },
                            onEdit = { onNavigateToEdit(recipe.id) },
                            onDelete = { recipeToDelete = recipe }
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    recipeToDelete?.let { recipe ->
        ConfirmDialog(
            title = "מחיקת מתכון",
            message = "האם אתה בטוח שברצונך למחוק את המתכון '${recipe.name}'?",
            confirmText = "מחק",
            dismissText = "ביטול",
            isDestructive = true,
            onConfirm = {
                viewModel.deleteRecipe(recipe.id)
                recipeToDelete = null
            },
            onDismiss = { recipeToDelete = null }
        )
    }
}

@Composable
private fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    MaitesCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header Row: Name & Yield
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (recipe.category.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = recipe.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            text = "${recipe.ingredients.size} מצרכים • תפוקה: ${recipe.yield} ${recipe.yieldUnit}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Profit margin chip
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "+${String.format("%.0f", recipe.profitMargin)}% רווח",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            // Price Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "עלות למנה",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    PriceDisplay(
                        price = recipe.costPerUnit,
                        large = false
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "סה\"כ עלות מתכון",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    PriceDisplay(
                        price = recipe.totalCost,
                        large = false
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "מחיר מכירה מומלץ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    PriceDisplay(
                        price = recipe.suggestedPrice,
                        large = false,
                        showBackground = true
                    )
                }
            }

            // Actions row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "ערוך מתכון",
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
                        contentDescription = "מחק מתכון",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
