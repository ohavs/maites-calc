package com.example.maitescalc.data.model

data class SaleItem(
    val recipeId: String = "",
    val recipeName: String = "",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val costPerUnit: Double = 0.0,
    val totalPrice: Double = 0.0
) {
    val totalCost: Double get() = costPerUnit * quantity
    val profit: Double get() = totalPrice - totalCost

    fun toMap(): Map<String, Any?> = mapOf(
        "recipeId" to recipeId,
        "recipeName" to recipeName,
        "quantity" to quantity,
        "unitPrice" to unitPrice,
        "costPerUnit" to costPerUnit,
        "totalPrice" to totalPrice
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): SaleItem {
            return SaleItem(
                recipeId = map["recipeId"] as? String ?: "",
                recipeName = map["recipeName"] as? String ?: "",
                quantity = (map["quantity"] as? Number)?.toInt() ?: 1,
                unitPrice = (map["unitPrice"] as? Number)?.toDouble() ?: 0.0,
                costPerUnit = (map["costPerUnit"] as? Number)?.toDouble() ?: 0.0,
                totalPrice = (map["totalPrice"] as? Number)?.toDouble() ?: 0.0
            )
        }
    }
}
