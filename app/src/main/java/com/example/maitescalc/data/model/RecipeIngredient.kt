package com.example.maitescalc.data.model

data class RecipeIngredient(
    val ingredientId: String = "",
    val ingredientName: String = "",
    val quantity: Double = 0.0,
    val unitType: UnitType = UnitType.GRAM
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "ingredientId" to ingredientId,
        "ingredientName" to ingredientName,
        "quantity" to quantity,
        "unitType" to unitType.name
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): RecipeIngredient {
            return RecipeIngredient(
                ingredientId = map["ingredientId"] as? String ?: "",
                ingredientName = map["ingredientName"] as? String ?: "",
                quantity = (map["quantity"] as? Number)?.toDouble() ?: 0.0,
                unitType = UnitType.fromString(map["unitType"] as? String ?: "GRAM")
            )
        }
    }
}
