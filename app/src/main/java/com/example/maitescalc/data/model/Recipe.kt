package com.example.maitescalc.data.model

data class Recipe(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val ingredients: List<RecipeIngredient> = emptyList(),
    val yield: Int = 1,
    val yieldUnit: String = "יחידות",
    val totalCost: Double = 0.0,
    val costPerUnit: Double = 0.0,
    val suggestedPrice: Double = 0.0,
    val profitMargin: Double = 30.0,
    val notes: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "category" to category,
        "description" to description,
        "ingredients" to ingredients.map { it.toMap() },
        "yield" to yield,
        "yieldUnit" to yieldUnit,
        "totalCost" to totalCost,
        "costPerUnit" to costPerUnit,
        "suggestedPrice" to suggestedPrice,
        "profitMargin" to profitMargin,
        "notes" to notes,
        "lastUpdated" to System.currentTimeMillis()
    )

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(id: String, map: Map<String, Any?>): Recipe {
            val ingredientsList = (map["ingredients"] as? List<Map<String, Any?>>)?.map {
                RecipeIngredient.fromMap(it)
            } ?: emptyList()

            return Recipe(
                id = id,
                name = map["name"] as? String ?: "",
                category = map["category"] as? String ?: "",
                description = map["description"] as? String ?: "",
                ingredients = ingredientsList,
                yield = (map["yield"] as? Number)?.toInt() ?: 1,
                yieldUnit = map["yieldUnit"] as? String ?: "יחידות",
                totalCost = (map["totalCost"] as? Number)?.toDouble() ?: 0.0,
                costPerUnit = (map["costPerUnit"] as? Number)?.toDouble() ?: 0.0,
                suggestedPrice = (map["suggestedPrice"] as? Number)?.toDouble() ?: 0.0,
                profitMargin = (map["profitMargin"] as? Number)?.toDouble() ?: 30.0,
                notes = map["notes"] as? String ?: "",
                lastUpdated = (map["lastUpdated"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }

        val categories = listOf("עוגות", "עוגיות", "לחמים", "מאפים מלוחים", "קינוחים", "טארטים", "קרמים", "אחר")
    }
}
