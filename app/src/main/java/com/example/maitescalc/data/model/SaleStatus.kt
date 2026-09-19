package com.example.maitescalc.data.model

enum class SaleStatus(val displayName: String) {
    DRAFT("טיוטה"),
    CONFIRMED("מאושר"),
    IN_PROGRESS("בהכנה"),
    COMPLETED("הושלם"),
    CANCELLED("בוטל");

    companion object {
        fun fromString(value: String): SaleStatus {
            return entries.find { it.name == value } ?: DRAFT
        }
    }
}
