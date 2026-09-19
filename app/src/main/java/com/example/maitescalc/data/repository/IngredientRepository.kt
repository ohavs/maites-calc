package com.example.maitescalc.data.repository

import com.example.maitescalc.data.model.Ingredient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class IngredientRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserIngredientsCollection() =
        db.collection("users").document(auth.currentUser?.uid ?: "").collection("ingredients")

    fun getIngredientsFlow(): Flow<List<Ingredient>> = callbackFlow {
        val registration: ListenerRegistration = getUserIngredientsCollection()
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val ingredients = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.let { Ingredient.fromMap(doc.id, it) }
                } ?: emptyList()
                trySend(ingredients)
            }
        awaitClose { registration.remove() }
    }

    suspend fun addIngredient(ingredient: Ingredient): String {
        val docRef = getUserIngredientsCollection().add(ingredient.toMap()).await()
        return docRef.id
    }

    suspend fun updateIngredient(ingredient: Ingredient) {
        getUserIngredientsCollection().document(ingredient.id).set(ingredient.toMap()).await()
    }

    suspend fun deleteIngredient(ingredientId: String) {
        getUserIngredientsCollection().document(ingredientId).delete().await()
    }

    suspend fun getIngredient(ingredientId: String): Ingredient? {
        val doc = getUserIngredientsCollection().document(ingredientId).get().await()
        return doc.data?.let { Ingredient.fromMap(doc.id, it) }
    }
}
