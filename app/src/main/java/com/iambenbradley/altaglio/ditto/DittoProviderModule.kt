package com.iambenbradley.altaglio.ditto

import android.content.Context
import com.iambenbradley.altaglio.BuildConfig
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import live.ditto.Ditto
import live.ditto.DittoIdentity
import live.ditto.DittoLogLevel
import live.ditto.DittoLogger
import live.ditto.DittoSubscription
import live.ditto.android.DefaultAndroidDittoDependencies
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DittoProviderModule {
    @Singleton
    @Binds
    fun bindDittoProvider(impl: DittoProviderImpl): DittoProvider
}

interface DittoProvider {
    val ditto: Flow<Ditto>
}

class DittoProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : DittoProvider {

    override val ditto: Flow<Ditto> = flow {
        val androidDependencies = DefaultAndroidDittoDependencies(context)
        val identity = DittoIdentity.OnlinePlayground(
            androidDependencies,
            BuildConfig.DITTO_APP_ID,
            BuildConfig.DITTO_TOKEN
        )
        DittoLogger.minimumLogLevel = DittoLogLevel.DEBUG
        val ditto = Ditto(androidDependencies, identity)
        ditto.startSync()
        initCollections(ditto)
        emit(ditto)
    }.shareIn(
        CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        replay = 1,
    )


    private val subscriptions: MutableMap<DittoCollection<*>, DittoSubscription> = mutableMapOf()

    private fun initCollections(ditto: Ditto) {
        val collections = DittoCollection.allCollections
        collections.forEach { collection ->
            subscriptions[collection] = ditto
                .store
                .collection(collection.collectionName)
                .let { dittoCollection ->
                    when (val sub = collection.subscriptionQuery) {
                        SubscriptionQuery.FindAll -> dittoCollection.findAll()
                        is SubscriptionQuery.Query -> dittoCollection.find(sub.queryString)
                    }
                }
                .subscribe()
        }
    }
}