package com.example.maitescalc.data.repository

import com.example.maitescalc.data.model.Sale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SaleRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserSalesCollection() =
        db.collection("users").document(auth.currentUser?.uid ?: "").collection("sales")

    fun getSalesFlow(): Flow<List<Sale>> = callbackFlow {
        val registration: ListenerRegistration = getUserSalesCollection()
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val sales = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.let { Sale.fromMap(doc.id, it) }
                } ?: emptyList()
                trySend(sales)
            }
        awaitClose { registration.remove() }
    }

    suspend fun addSale(sale: Sale): String {
        val docRef = getUserSalesCollection().add(sale.toMap()).await()
        return docRef.id
    }

    suspend fun updateSale(sale: Sale) {
        getUserSalesCollection().document(sale.id).set(sale.toMap()).await()
    }

    suspend fun deleteSale(saleId: String) {
        getUserSalesCollection().document(saleId).delete().await()
    }
}
