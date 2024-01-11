package com.iambenbradley.altaglio.model

sealed interface OrderItem

data class Drink(
    val type: String,
    val size: DrinkSize,
)

enum class DrinkSize {
    Small,
    Medium,
    Large,
}

data class Side(
    val type: String,
)
