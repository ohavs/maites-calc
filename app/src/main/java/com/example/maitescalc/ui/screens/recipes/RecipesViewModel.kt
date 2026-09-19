package com.example.maitescalc.ui.screens.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maitescalc.data.model.Ingredient
import com.example.maitescalc.data.model.Recipe
import com.example.maitescalc.data.repository.IngredientRepository
import com.example.maitescalc.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class RecipesUiState(
    val recipes: List<Recipe> = emptyList(),
    val filteredRecipes: List<Recipe> = emptyList(),
    val ingredients: List<Ingredient> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "הכל",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class RecipesViewModel(
    private val recipeRepository: RecipeRepository = RecipeRepository(),
    private val ingredientRepository: IngredientRepository = IngredientRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipesUiState())
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Collect ingredients first or simultaneously
            launch {
                ingredientRepository.getIngredientsFlow()
                    .catch { emit(emptyList()) }
                    .collect { ingredients ->
                        _uiState.value = _uiState.value.copy(ingredients = ingredients)
                    }
            }

            // Collect recipes
            launch {
                recipeRepository.getRecipesFlow()
                    .catch { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "שגיאה בטעינת מתכונים: ${error.localizedMessage}"
                        )
                    }
                    .collect { list ->
                        _uiState.value = _uiState.value.copy(
                            recipes = list,
                            isLoading = false,
                            filteredRecipes = filterList(list, _uiState.value.searchQuery, _uiState.value.selectedCategory)
                        )
                    }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredRecipes = filterList(_uiState.value.recipes, query, _uiState.value.selectedCategory)
        )
    }

    fun setSelectedCategory(category: String) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            filteredRecipes = filterList(_uiState.value.recipes, _uiState.value.searchQuery, category)
        )
    }

    private fun filterList(list: List<Recipe>, query: String, category: String): List<Recipe> {
        return list.filter { recipe ->
            val matchesQuery = query.isBlank() ||
                    recipe.name.contains(query, ignoreCase = true) ||
                    recipe.description.contains(query, ignoreCase = true)
            val matchesCategory = category == "הכל" || recipe.category == category
            matchesQuery && matchesCategory
        }
    }

    fun deleteRecipe(recipeId: String) {
        viewModelScope.launch {
            try {
                recipeRepository.deleteRecipe(recipeId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "שגיאה במחיקת מתכון: ${e.localizedMessage}")
            }
        }
    }

    suspend fun getRecipe(id: String): Recipe? {
        return _uiState.value.recipes.find { it.id == id }
    }

    fun calculateCost(recipe: Recipe): Recipe {
        return recipeRepository.calculateRecipeCost(recipe, _uiState.value.ingredients)
    }

    fun saveRecipe(recipe: Recipe, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                // Ensure cost is accurate before saving
                val calculated = calculateCost(recipe)
                if (calculated.id.isBlank()) {
                    recipeRepository.addRecipe(calculated)
                } else {
                    recipeRepository.updateRecipe(calculated)
                }
                onComplete()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "שגיאה בשמירת מתכון: ${e.localizedMessage}")
            }
        }
    }
}
