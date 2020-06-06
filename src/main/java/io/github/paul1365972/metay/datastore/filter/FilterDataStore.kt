package io.github.paul1365972.metay.datastore.filter

import io.github.paul1365972.metay.datastore.MetayDataStore

abstract class FilterDataStore<L>(
        val underlying: MetayDataStore<L>
) : MetayDataStore<L> {
    override fun close() = underlying.close()
}
