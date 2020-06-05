package io.github.paul1365972.metay.storage

abstract class FilterDataStore<L>(
        val underlying: MetayDataStore<L>
) : MetayDataStore<L> {
    override fun close() = underlying.close()
}