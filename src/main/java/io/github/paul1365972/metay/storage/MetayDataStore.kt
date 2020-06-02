package io.github.paul1365972.metay.storage

interface MetayDataStore<L> {
    fun <T> get(dataKey: DataKey<T>, locationKey: L): T?
    fun <T> put(dataKey: DataKey<T>, locationKey: L, value: T)
    fun <T> remove(dataKey: DataKey<T>, locationKey: L)
    fun onClose() {}
}

abstract class FilterDataStore<L>(
        val underlying: MetayDataStore<L>
) : MetayDataStore<L> {
    override fun onClose() = underlying.onClose()
}

open class TransformingDataStore<L, K>(
        val underlying: MetayDataStore<K>,
        val transformer: (L) -> K
) : MetayDataStore<L> {
    override fun <T> get(dataKey: DataKey<T>, locationKey: L): T? {
        return underlying.get(dataKey, transformer(locationKey))
    }

    override fun <T> put(dataKey: DataKey<T>, locationKey: L, value: T) {
        underlying.put(dataKey, transformer(locationKey), value)
    }

    override fun <T> remove(dataKey: DataKey<T>, locationKey: L) {
        underlying.remove(dataKey, transformer(locationKey))
    }

    override fun onClose() = underlying.onClose()
}
