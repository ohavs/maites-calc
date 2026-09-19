package com.example.maitescalc.data.model

data class Ingredient(
    val id: String = "",
    val name: String = "",
    val brand: String = "",
    val category: String = "",
    val pricePerUnit: Double = 0.0,
    val unitType: UnitType = UnitType.KG,
    val packageSize: Double = 0.0,
    val packagePrice: Double = 0.0,
    val barcode: String = "",
    val notes: String = "",
    val imageUrl: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
) {
    // מחיר ליחידה מחושב: מחיר אריזה / גודל אריזה
    val calculatedPricePerUnit: Double
        get() = if (packageSize > 0) packagePrice / packageSize else pricePerUnit

    fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "brand" to brand,
        "category" to category,
        "pricePerUnit" to calculatedPricePerUnit,
        "unitType" to unitType.name,
        "packageSize" to packageSize,
        "packagePrice" to packagePrice,
        "barcode" to barcode,
        "notes" to notes,
        "imageUrl" to imageUrl,
        "lastUpdated" to System.currentTimeMillis()
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): Ingredient {
            return Ingredient(
                id = id,
                name = map["name"] as? String ?: "",
                brand = map["brand"] as? String ?: "",
                category = map["category"] as? String ?: "",
                pricePerUnit = (map["pricePerUnit"] as? Number)?.toDouble() ?: 0.0,
                unitType = UnitType.fromString(map["unitType"] as? String ?: "KG"),
                packageSize = (map["packageSize"] as? Number)?.toDouble() ?: 0.0,
                packagePrice = (map["packagePrice"] as? Number)?.toDouble() ?: 0.0,
                barcode = map["barcode"] as? String ?: "",
                notes = map["notes"] as? String ?: "",
                imageUrl = map["imageUrl"] as? String ?: "",
                lastUpdated = (map["lastUpdated"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }

        val categories = listOf("קמחים", "סוכרים וממתיקים", "חלב ומוצריו", "שומנים ושמנים", "ביצים", "שוקולד וקקאו", "תמציות וטעמים", "אגוזים ופירות יבשים", "פירות", "קרמים ומילויים", "קישוטים", "אבקות אפייה", "אחר")
    }
}
