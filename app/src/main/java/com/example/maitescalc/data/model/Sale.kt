package com.example.maitescalc.data.model

data class Sale(
    val id: String = "",
    val name: String = "",
    val items: List<SaleItem> = emptyList(),
    val totalCost: Double = 0.0,
    val totalPrice: Double = 0.0,
    val profit: Double = 0.0,
    val status: SaleStatus = SaleStatus.DRAFT,
    val customerName: String = "",
    val customerPhone: String = "",
    val dueDate: Long = 0L,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "items" to items.map { it.toMap() },
        "totalCost" to totalCost,
        "totalPrice" to totalPrice,
        "profit" to profit,
        "status" to status.name,
        "customerName" to customerName,
        "customerPhone" to customerPhone,
        "dueDate" to dueDate,
        "notes" to notes,
        "createdAt" to createdAt
    )

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(id: String, map: Map<String, Any?>): Sale {
            val itemsList = (map["items"] as? List<Map<String, Any?>>)?.map {
                SaleItem.fromMap(it)
            } ?: emptyList()

            return Sale(
                id = id,
                name = map["name"] as? String ?: "",
                items = itemsList,
                totalCost = (map["totalCost"] as? Number)?.toDouble() ?: 0.0,
                totalPrice = (map["totalPrice"] as? Number)?.toDouble() ?: 0.0,
                profit = (map["profit"] as? Number)?.toDouble() ?: 0.0,
                status = SaleStatus.fromString(map["status"] as? String ?: "DRAFT"),
                customerName = map["customerName"] as? String ?: "",
                customerPhone = map["customerPhone"] as? String ?: "",
                dueDate = (map["dueDate"] as? Number)?.toLong() ?: 0L,
                notes = map["notes"] as? String ?: "",
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }
    }
}
