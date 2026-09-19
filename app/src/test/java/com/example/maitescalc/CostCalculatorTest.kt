package com.example.maitescalc

import com.example.maitescalc.data.model.Ingredient
import com.example.maitescalc.data.model.RecipeIngredient
import com.example.maitescalc.data.model.UnitType
import com.example.maitescalc.util.CostCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class CostCalculatorTest {

    @Test
    fun calculateIngredientCost_gramsToKg_isAccurate() {
        // קמח: מחיר 6 ש"ח לק"ג. המתכון דורש 500 גרם -> עלות צריכה להיות 3 ש"ח
        val flour = Ingredient(
            id = "ing_flour",
            name = "קמח לבן",
            pricePerUnit = 6.0,
            unitType = UnitType.KG
        )
        val recipeIngredient = RecipeIngredient(
            ingredientId = "ing_flour",
            ingredientName = "קמח לבן",
            quantity = 500.0,
            unitType = UnitType.GRAM
        )

        val cost = CostCalculator.calculateIngredientCost(recipeIngredient, flour)
        assertEquals(3.0, cost, 0.001)
    }

    @Test
    fun calculateTotalCost_multipleIngredients_isAccurate() {
        val flour = Ingredient(id = "1", name = "קמח", pricePerUnit = 6.0, unitType = UnitType.KG)
        val sugar = Ingredient(id = "2", name = "סוכר", pricePerUnit = 4.0, unitType = UnitType.KG)
        val eggs = Ingredient(id = "3", name = "ביצים", pricePerUnit = 1.0, unitType = UnitType.UNIT)

        val recipeItems = listOf(
            RecipeIngredient(ingredientId = "1", quantity = 250.0, unitType = UnitType.GRAM), // 0.25 * 6 = 1.50
            RecipeIngredient(ingredientId = "2", quantity = 200.0, unitType = UnitType.GRAM), // 0.20 * 4 = 0.80
            RecipeIngredient(ingredientId = "3", quantity = 3.0, unitType = UnitType.UNIT)     // 3 * 1 = 3.00
        )

        val totalCost = CostCalculator.calculateTotalCost(recipeItems, listOf(flour, sugar, eggs))
        // 1.50 + 0.80 + 3.00 = 5.30
        assertEquals(5.30, totalCost, 0.001)
    }

    @Test
    fun calculateSuggestedPrice_withProfitMargin_isAccurate() {
        val costPerUnit = 10.0
        val profitMargin = 50.0 // 50%
        val suggestedPrice = CostCalculator.calculateSuggestedPrice(costPerUnit, profitMargin)
        // 10.0 * 1.5 = 15.0
        assertEquals(15.0, suggestedPrice, 0.001)
    }
}
