package com.example.maitescalc.ui.screens.ingredients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maitescalc.data.model.Ingredient
import com.example.maitescalc.data.repository.IngredientRepository
import com.example.maitescalc.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class IngredientsUiState(
    val ingredients: List<Ingredient> = emptyList(),
    val filteredIngredients: List<Ingredient> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "הכל",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class IngredientsViewModel(
    private val ingredientRepository: IngredientRepository = IngredientRepository(),
    private val recipeRepository: RecipeRepository = RecipeRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(IngredientsUiState())
    val uiState: StateFlow<IngredientsUiState> = _uiState.asStateFlow()

    init {
        loadIngredients()
    }

    private fun loadIngredients() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            ingredientRepository.getIngredientsFlow()
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "שגיאה בטעינת מצרכים: ${error.localizedMessage}"
                    )
                }
                .collect { list ->
                    _uiState.value = _uiState.value.copy(
                        ingredients = list,
                        isLoading = false,
                        filteredIngredients = filterList(list, _uiState.value.searchQuery, _uiState.value.selectedCategory)
                    )
                }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredIngredients = filterList(_uiState.value.ingredients, query, _uiState.value.selectedCategory)
        )
    }

    fun setSelectedCategory(category: String) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            filteredIngredients = filterList(_uiState.value.ingredients, _uiState.value.searchQuery, category)
        )
    }

    private fun filterList(list: List<Ingredient>, query: String, category: String): List<Ingredient> {
        return list.filter { ingredient ->
            val matchesQuery = query.isBlank() ||
                    ingredient.name.contains(query, ignoreCase = true) ||
                    ingredient.brand.contains(query, ignoreCase = true) ||
                    ingredient.barcode.contains(query, ignoreCase = true)
            val matchesCategory = category == "הכל" || ingredient.category == category
            matchesQuery && matchesCategory
        }
    }

    fun deleteIngredient(ingredientId: String) {
        viewModelScope.launch {
            try {
                ingredientRepository.deleteIngredient(ingredientId)
                // Recalculate recipes
                val updatedList = _uiState.value.ingredients.filter { it.id != ingredientId }
                recipeRepository.recalculateAllRecipes(updatedList)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "שגיאה במחיקת מצרך: ${e.localizedMessage}")
            }
        }
    }

    suspend fun getIngredient(id: String): Ingredient? {
        return _uiState.value.ingredients.find { it.id == id }
            ?: ingredientRepository.getIngredient(id)
    }

    fun saveIngredient(ingredient: Ingredient, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                if (ingredient.id.isBlank()) {
                    ingredientRepository.addIngredient(ingredient)
                } else {
                    ingredientRepository.updateIngredient(ingredient)
                }
                // Recalculate all recipes using updated ingredients
                val currentIngredients = _uiState.value.ingredients.toMutableList()
                val idx = currentIngredients.indexOfFirst { it.id == ingredient.id }
                if (idx >= 0) {
                    currentIngredients[idx] = ingredient
                } else {
                    currentIngredients.add(ingredient)
                }
                recipeRepository.recalculateAllRecipes(currentIngredients)
                onComplete()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "שגיאה בשמירת מצרך: ${e.localizedMessage}")
            }
        }
    }
}
