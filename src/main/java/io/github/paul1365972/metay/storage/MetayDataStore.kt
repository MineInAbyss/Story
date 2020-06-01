package io.github.paul1365972.metay.storage

interface MetayDataStore<L> {
    fun <T> get(dataKey: DataKey<T>, locationKey: L): T?
    fun <T> put(dataKey: DataKey<T>, locationKey: L, value: T)
    fun onClose()

    fun toKey(dataKey: DataKey<*>, locationKey: L): String {
        return "${dataKey.namespacedKey}:${locationKey}"
    }
}

abstract class FilterDataStore<L>(
        val underlying: MetayDataStore<L>
) : MetayDataStore<L> {
    override fun onClose() = underlying.onClose()
}
