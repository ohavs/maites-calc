package com.example.maitescalc.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maitescalc.data.model.SaleStatus
import com.example.maitescalc.data.repository.IngredientRepository
import com.example.maitescalc.data.repository.RecipeRepository
import com.example.maitescalc.data.repository.SaleRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class HomeUiState(
    val displayName: String = "שף",
    val ingredientsCount: Int = 0,
    val recipesCount: Int = 0,
    val openSalesCount: Int = 0,
    val totalProfit: Double = 0.0,
    val totalRevenue: Double = 0.0,
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val ingredientRepository: IngredientRepository = IngredientRepository(),
    private val recipeRepository: RecipeRepository = RecipeRepository(),
    private val saleRepository: SaleRepository = SaleRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val user = FirebaseAuth.getInstance().currentUser
        val name = user?.displayName ?: if (user?.isAnonymous == true) "אורח" else "שף"
        _uiState.value = _uiState.value.copy(displayName = name)

        viewModelScope.launch {
            ingredientRepository.getIngredientsFlow()
                .catch { emit(emptyList()) }
                .collect { ingredients ->
                    _uiState.value = _uiState.value.copy(ingredientsCount = ingredients.size)
                }
        }

        viewModelScope.launch {
            recipeRepository.getRecipesFlow()
                .catch { emit(emptyList()) }
                .collect { recipes ->
                    _uiState.value = _uiState.value.copy(recipesCount = recipes.size)
                }
        }

        viewModelScope.launch {
            saleRepository.getSalesFlow()
                .catch { emit(emptyList()) }
                .collect { sales ->
                    val openCount = sales.count { it.status != SaleStatus.COMPLETED && it.status != SaleStatus.CANCELLED }
                    val revenue = sales.filter { it.status != SaleStatus.CANCELLED }.sumOf { it.totalPrice }
                    val profit = sales.filter { it.status != SaleStatus.CANCELLED }.sumOf { it.profit }
                    _uiState.value = _uiState.value.copy(
                        openSalesCount = openCount,
                        totalRevenue = revenue,
                        totalProfit = profit
                    )
                }
        }
    }
}
