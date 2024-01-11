package com.iambenbradley.altaglio.model

data class Order(
    val name: String,
    val items: List<OrderItem>,
)
