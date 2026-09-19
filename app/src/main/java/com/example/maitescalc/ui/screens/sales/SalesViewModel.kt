package com.example.maitescalc.ui.screens.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maitescalc.data.model.Recipe
import com.example.maitescalc.data.model.Sale
import com.example.maitescalc.data.model.SaleStatus
import com.example.maitescalc.data.repository.RecipeRepository
import com.example.maitescalc.data.repository.SaleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class SalesUiState(
    val sales: List<Sale> = emptyList(),
    val filteredSales: List<Sale> = emptyList(),
    val recipes: List<Recipe> = emptyList(),
    val selectedStatus: String = "הכל",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class SalesViewModel(
    private val saleRepository: SaleRepository = SaleRepository(),
    private val recipeRepository: RecipeRepository = RecipeRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SalesUiState())
    val uiState: StateFlow<SalesUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Collect recipes
            launch {
                recipeRepository.getRecipesFlow()
                    .catch { emit(emptyList()) }
                    .collect { recipes ->
                        _uiState.value = _uiState.value.copy(recipes = recipes)
                    }
            }

            // Collect sales
            launch {
                saleRepository.getSalesFlow()
                    .catch { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "שגיאה בטעינת מכירות: ${error.localizedMessage}"
                        )
                    }
                    .collect { list ->
                        _uiState.value = _uiState.value.copy(
                            sales = list,
                            isLoading = false,
                            filteredSales = filterList(list, _uiState.value.selectedStatus)
                        )
                    }
            }
        }
    }

    fun setSelectedStatus(status: String) {
        _uiState.value = _uiState.value.copy(
            selectedStatus = status,
            filteredSales = filterList(_uiState.value.sales, status)
        )
    }

    private fun filterList(list: List<Sale>, status: String): List<Sale> {
        if (status == "הכל") return list
        return list.filter { it.status.displayName == status }
    }

    fun deleteSale(saleId: String) {
        viewModelScope.launch {
            try {
                saleRepository.deleteSale(saleId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "שגיאה במחיקת מכירה: ${e.localizedMessage}")
            }
        }
    }

    fun updateSaleStatus(sale: Sale, newStatus: SaleStatus) {
        viewModelScope.launch {
            try {
                val updated = sale.copy(status = newStatus)
                saleRepository.updateSale(updated)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "שגיאה בעדכון סטטוס: ${e.localizedMessage}")
            }
        }
    }

    suspend fun getSale(id: String): Sale? {
        return _uiState.value.sales.find { it.id == id }
    }

    fun saveSale(sale: Sale, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                if (sale.id.isBlank()) {
                    saleRepository.addSale(sale)
                } else {
                    saleRepository.updateSale(sale)
                }
                onComplete()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "שגיאה בשמירת מכירה: ${e.localizedMessage}")
            }
        }
    }
}
