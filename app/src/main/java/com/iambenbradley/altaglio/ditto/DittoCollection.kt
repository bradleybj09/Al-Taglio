package com.iambenbradley.altaglio.ditto

abstract class DittoDocumentType {
    abstract val id: String
}

data class OrdersDocument(override val id: String) : DittoDocumentType()
data class CookingDocument(override val id: String) : DittoDocumentType()

sealed class DittoCollection<D: DittoDocumentType> {

    companion object {
        val allCollections get() = listOf(Orders, Cooking)
    }

    abstract val collectionName: String
    abstract val subscriptionQuery: SubscriptionQuery


    data object Orders: DittoCollection<OrdersDocument>() {
        override val collectionName = "Rooms"
        override val subscriptionQuery: SubscriptionQuery = SubscriptionQuery.FindAll
    }

    data object Cooking: DittoCollection<CookingDocument>() {
        override val collectionName = "Games"
        override val subscriptionQuery: SubscriptionQuery = SubscriptionQuery.FindAll
    }
}

sealed class SubscriptionQuery {

    data object FindAll : SubscriptionQuery()

    data class Query(
        val queryString: String,
    ) : SubscriptionQuery()
}