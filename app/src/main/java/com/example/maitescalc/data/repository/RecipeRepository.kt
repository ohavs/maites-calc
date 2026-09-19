package com.example.maitescalc.data.repository

import com.example.maitescalc.data.model.Ingredient
import com.example.maitescalc.data.model.Recipe
import com.example.maitescalc.data.model.UnitType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RecipeRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserRecipesCollection() =
        db.collection("users").document(auth.currentUser?.uid ?: "").collection("recipes")

    fun getRecipesFlow(): Flow<List<Recipe>> = callbackFlow {
        val registration: ListenerRegistration = getUserRecipesCollection()
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val recipes = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.let { Recipe.fromMap(doc.id, it) }
                } ?: emptyList()
                trySend(recipes)
            }
        awaitClose { registration.remove() }
    }

    suspend fun addRecipe(recipe: Recipe): String {
        val docRef = getUserRecipesCollection().add(recipe.toMap()).await()
        return docRef.id
    }

    suspend fun updateRecipe(recipe: Recipe) {
        getUserRecipesCollection().document(recipe.id).set(recipe.toMap()).await()
    }

    suspend fun deleteRecipe(recipeId: String) {
        getUserRecipesCollection().document(recipeId).delete().await()
    }

    // חישוב מחדש של עלויות מתכון על בסיס מצרכים עדכניים
    fun calculateRecipeCost(recipe: Recipe, ingredients: List<Ingredient>): Recipe {
        var totalCost = 0.0
        for (recipeIngredient in recipe.ingredients) {
            val ingredient = ingredients.find { it.id == recipeIngredient.ingredientId } ?: continue
            val pricePerBaseUnit = ingredient.calculatedPricePerUnit // מחיר ל-unitType של המצרך
            
            // המרת כמות המתכון ליחידת המצרך
            val convertedQuantity = convertQuantity(
                recipeIngredient.quantity,
                recipeIngredient.unitType,
                ingredient.unitType
            )
            totalCost += convertedQuantity * pricePerBaseUnit
        }
        val costPerUnit = if (recipe.yield > 0) totalCost / recipe.yield else totalCost
        val suggestedPrice = costPerUnit * (1 + recipe.profitMargin / 100.0)

        return recipe.copy(
            totalCost = totalCost,
            costPerUnit = costPerUnit,
            suggestedPrice = suggestedPrice
        )
    }

    // המרת יחידות
    private fun convertQuantity(quantity: Double, from: UnitType, to: UnitType): Double {
        val quantityInGrams = when (from) {
            UnitType.KG -> quantity * 1000.0
            UnitType.GRAM -> quantity
            UnitType.LITER -> quantity * 1000.0  // approx 1ml = 1g
            UnitType.ML -> quantity
            UnitType.UNIT -> quantity
            UnitType.DOZEN -> quantity * 12.0
            UnitType.TEASPOON -> quantity * 5.0   // ~5g per tsp
            UnitType.TABLESPOON -> quantity * 15.0 // ~15g per tbsp
        }

        return when (to) {
            UnitType.KG -> quantityInGrams / 1000.0
            UnitType.GRAM -> quantityInGrams
            UnitType.LITER -> quantityInGrams / 1000.0
            UnitType.ML -> quantityInGrams
            UnitType.UNIT -> quantity  // no conversion for units
            UnitType.DOZEN -> quantity / 12.0
            UnitType.TEASPOON -> quantityInGrams / 5.0
            UnitType.TABLESPOON -> quantityInGrams / 15.0
        }
    }

    // עדכון עלויות של כל המתכונים שמשתמשים במצרך מסוים
    suspend fun recalculateAllRecipes(ingredients: List<Ingredient>) {
        val snapshot = getUserRecipesCollection().get().await()
        for (doc in snapshot.documents) {
            val recipe = doc.data?.let { Recipe.fromMap(doc.id, it) } ?: continue
            val updated = calculateRecipeCost(recipe, ingredients)
            if (updated.totalCost != recipe.totalCost) {
                updateRecipe(updated)
            }
        }
    }
}
