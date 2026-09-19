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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.maitescalc.data.model.Ingredient
import com.example.maitescalc.data.model.UnitType
import com.example.maitescalc.data.remote.OpenFoodFactsApi
import com.example.maitescalc.data.remote.OpenFoodFactsProduct
import com.example.maitescalc.ui.components.*
import kotlinx.coroutines.launch

@Composable
fun IngredientSearchScreen(
    viewModel: IngredientsViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onProductSelected: ((Ingredient) -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<OpenFoodFactsProduct>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var importedSuccessMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    val quickKeywords = remember {
        listOf("קמח", "סוכר", "חמאה", "שוקולד", "שמרים", "קקאו", "וניל", "אבקת סוכר")
    }

    fun performSearch(query: String) {
        if (query.isBlank()) return
        isLoading = true
        errorMessage = null
        coroutineScope.launch {
            try {
                val response = OpenFoodFactsApi.service.searchProducts(searchTerms = query)
                searchResults = response.products.filter { it.product_name.isNotBlank() }
                if (searchResults.isEmpty()) {
                    errorMessage = "לא נמצאו מוצרים תואמים לחיפוש '$query'"
                }
            } catch (e: Exception) {
                errorMessage = "שגיאה בחיפוש ב-API: ${e.localizedMessage ?: "בדוק חיבור אינטרנט"}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            MaitesTopBar(
                title = "חיפוש מוצרים ב-API",
                onBackClick = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search input
            MaitesSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "חפש מוצר (למשל: קמח מילניום, שוקולד מריר)...",
                modifier = Modifier.padding(16.dp),
                onSearch = { performSearch(searchQuery) }
            )

            // Quick suggestion chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickKeywords.forEach { keyword ->
                    SuggestionChip(
                        onClick = {
                            searchQuery = keyword
                            performSearch(keyword)
                        },
                        label = { Text(keyword) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            importedSuccessMessage?.let { success ->
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(12.dp)
                ) {
                    Text(
                        text = success,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // State & Results
            if (isLoading) {
                LoadingState(message = "מחפש מוצרים ב-Open Food Facts...")
            } else if (searchResults.isEmpty()) {
                EmptyState(
                    icon = Icons.Filled.CloudDownload,
                    title = if (errorMessage != null) "שגיאה או אין תוצאות" else "חיפוש מוצרים מהיר",
                    subtitle = errorMessage ?: "הקלד שם מצרך או בחר מהמילים הנפוצות למעלה כדי למשוך נתונים",
                    action = {
                        MaitesPrimaryButton(
                            text = "חפש קמח",
                            onClick = {
                                searchQuery = "קמח"
                                performSearch("קמח")
                            },
                            icon = Icons.Filled.Search
                        )
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = searchResults,
                        key = { it.code.ifEmpty { it.product_name + it.brands } }
                    ) { product ->
                        ProductSearchResultCard(
                            product = product,
                            onImport = {
                                val newIngredient = Ingredient(
                                    name = product.product_name.ifBlank { "מוצר מיובא" },
                                    brand = product.brands,
                                    category = if (product.categories.contains("flour", true) || product.categories.contains("קמח", true)) "קמחים"
                                    else if (product.categories.contains("sugar", true) || product.categories.contains("סוכר", true)) "סוכרים וממתיקים"
                                    else if (product.categories.contains("chocolate", true) || product.categories.contains("שוקולד", true)) "שוקולד וקקאו"
                                    else "אחר",
                                    unitType = UnitType.KG,
                                    packageSize = 1.0,
                                    packagePrice = 0.0,
                                    barcode = product.code,
                                    imageUrl = product.image_url.ifBlank { product.image_small_url },
                                    notes = "מיובא מ-Open Food Facts: ${product.quantity}"
                                )
                                viewModel.saveIngredient(newIngredient) {
                                    importedSuccessMessage = "המוצר '${newIngredient.name}' נוסף בהצלחה למצרכים שלך!"
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductSearchResultCard(
    product: OpenFoodFactsProduct,
    onImport: () -> Unit
) {
    MaitesCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imgUrl = product.image_small_url.ifBlank { product.image_url }
            if (imgUrl.isNotBlank()) {
                AsyncImage(
                    model = imgUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CloudDownload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.product_name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (product.brands.isNotBlank()) {
                    Text(
                        text = product.brands,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (product.quantity.isNotBlank()) {
                    Text(
                        text = "כמות: ${product.quantity}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Button(
                onClick = onImport,
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "ייבא", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
