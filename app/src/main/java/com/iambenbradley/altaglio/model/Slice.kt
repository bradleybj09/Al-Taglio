package com.iambenbradley.altaglio.model

data class Slice(
    val pizza: Pizza,
    val weight: Weight,
) : OrderItem
