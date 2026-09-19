package com.example.maitescalc.ui.screens.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.maitescalc.util.CostCalculator

@Composable
fun RecipeDetailScreen(
    recipeId: String,
    viewModel: RecipesViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val recipe = uiState.recipes.find { it.id == recipeId }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MaitesTopBar(
                title = recipe?.name ?: "פרטי מתכון",
                onBackClick = onNavigateBack,
                actions = {
                    if (recipe != null) {
                        IconButton(onClick = onNavigateToEdit) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "ערוך מתכון",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "מחק מתכון",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (recipe == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "מתכון לא נמצא",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Header Card
                MaitesCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = recipe.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (recipe.category.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.small)
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = recipe.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        Text(
                            text = "תפוקת מתכון: ${recipe.yield} ${recipe.yieldUnit}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (recipe.description.isNotEmpty()) {
                            Text(
                                text = recipe.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Financial Overview 2x2 Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricBox(
                        title = "עלות למנה",
                        price = recipe.costPerUnit,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "מחיר מומלץ",
                        price = recipe.suggestedPrice,
                        modifier = Modifier.weight(1f),
                        highlight = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricBox(
                        title = "סה\"כ עלות",
                        price = recipe.totalCost,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "רווח למנה (${recipe.profitMargin.toInt()}%)",
                        price = (recipe.suggestedPrice - recipe.costPerUnit).coerceAtLeast(0.0),
                        modifier = Modifier.weight(1f),
                        highlight = true
                    )
                }

                // Ingredients Breakdown
                Text(
                    text = "פירוט חומרי הגלם (${recipe.ingredients.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                MaitesCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        recipe.ingredients.forEachIndexed { index, item ->
                            val matching = uiState.ingredients.find { it.id == item.ingredientId }
                            val cost = if (matching != null) CostCalculator.calculateIngredientCost(item, matching) else 0.0

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = item.ingredientName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${item.quantity} ${item.unitType.shortName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                PriceDisplay(price = cost, large = false)
                            }

                            if (index < recipe.ingredients.size - 1) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            }
                        }
                    }
                }

                if (recipe.notes.isNotEmpty()) {
                    Text(
                        text = "הערות",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    MaitesCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = recipe.notes,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showDeleteDialog && recipe != null) {
        ConfirmDialog(
            title = "מחיקת מתכון",
            message = "האם אתה בטוח שברצונך למחוק את '${recipe.name}'?",
            confirmText = "מחק",
            dismissText = "ביטול",
            isDestructive = true,
            onConfirm = {
                viewModel.deleteRecipe(recipe.id)
                showDeleteDialog = false
                onNavigateBack()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun MetricBox(
    title: String,
    price: Double,
    modifier: Modifier = Modifier,
    highlight: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .then(
                if (highlight) Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                else Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        MaterialTheme.shapes.medium
                    )
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = if (highlight) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            PriceDisplay(
                price = price,
                large = false,
                showBackground = false
            )
        }
    }
}
