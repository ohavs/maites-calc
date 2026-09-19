package com.example.maitescalc.data.model

enum class UnitType(val displayName: String, val shortName: String) {
    KG("קילוגרם", "ק\"ג"),
    GRAM("גרם", "גר'"),
    LITER("ליטר", "ל'"),
    ML("מיליליטר", "מ\"ל"),
    UNIT("יחידה", "יח'"),
    DOZEN("תריסר", "תריסר"),
    TEASPOON("כפית", "כפית"),
    TABLESPOON("כף", "כף");

    companion object {
        fun fromString(value: String): UnitType {
            return entries.find { it.name == value } ?: GRAM
        }
    }
}
