package com.example.maitescalc.util

import com.example.maitescalc.data.model.Ingredient
import com.example.maitescalc.data.model.Recipe
import com.example.maitescalc.data.model.RecipeIngredient
import com.example.maitescalc.data.model.UnitType
import java.text.NumberFormat
import java.util.Locale

object CostCalculator {

    // חישוב עלות מצרך בודד במתכון
    fun calculateIngredientCost(
        recipeIngredient: RecipeIngredient,
        ingredient: Ingredient
    ): Double {
        val pricePerBaseUnit = ingredient.calculatedPricePerUnit
        val convertedQuantity = convertQuantity(
            recipeIngredient.quantity,
            recipeIngredient.unitType,
            ingredient.unitType
        )
        return convertedQuantity * pricePerBaseUnit
    }

    // חישוב עלות כוללת של מתכון
    fun calculateTotalCost(
        recipeIngredients: List<RecipeIngredient>,
        ingredients: List<Ingredient>
    ): Double {
        return recipeIngredients.sumOf { ri ->
            val ingredient = ingredients.find { it.id == ri.ingredientId }
            if (ingredient != null) calculateIngredientCost(ri, ingredient) else 0.0
        }
    }

    // חישוב מחיר מומלץ עם אחוז רווח
    fun calculateSuggestedPrice(costPerUnit: Double, profitMargin: Double): Double {
        return costPerUnit * (1 + profitMargin / 100.0)
    }

    // פורמט מחיר בשקלים
    fun formatPrice(price: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("he", "IL"))
        return format.format(price)
    }

    // פורמט מחיר קצר (ללא סמל מטבע)
    fun formatPriceShort(price: Double): String {
        return String.format(Locale.US, "%.2f", price)
    }

    private fun convertQuantity(quantity: Double, from: UnitType, to: UnitType): Double {
        if (from == to) return quantity

        val quantityInGrams = when (from) {
            UnitType.KG -> quantity * 1000.0
            UnitType.GRAM -> quantity
            UnitType.LITER -> quantity * 1000.0
            UnitType.ML -> quantity
            UnitType.UNIT -> quantity
            UnitType.DOZEN -> quantity * 12.0
            UnitType.TEASPOON -> quantity * 5.0
            UnitType.TABLESPOON -> quantity * 15.0
        }

        return when (to) {
            UnitType.KG -> quantityInGrams / 1000.0
            UnitType.GRAM -> quantityInGrams
            UnitType.LITER -> quantityInGrams / 1000.0
            UnitType.ML -> quantityInGrams
            UnitType.UNIT -> quantity
            UnitType.DOZEN -> quantity / 12.0
            UnitType.TEASPOON -> quantityInGrams / 5.0
            UnitType.TABLESPOON -> quantityInGrams / 15.0
        }
    }
}
