package io.github.paul1365972.metay.storage

interface MetayDataStore<L> {
    fun <T> get(dataKey: DataKey<T>, locationKey: L): T?
    fun <T> put(dataKey: DataKey<T>, locationKey: L, value: T)
    fun <T> remove(dataKey: DataKey<T>, locationKey: L)

    //TODO choose and rename

    fun <T> modify1(dataKey: DataKey<T>, locationKey: L, block: (T?) -> T?) {
        val modified = block(get(dataKey, locationKey))
        if (modified != null) put(dataKey, locationKey, modified) else remove(dataKey, locationKey)
    }

    fun <T> modify2(dataKey: DataKey<T>, locationKey: L, block: DataModifier<T?>.() -> Unit) {
        val modified = DataModifier(get(dataKey, locationKey))
        block(modified)
        if (modified.hasChanged()) {
            val value = modified.get()
            if (value != null) put(dataKey, locationKey, value) else remove(dataKey, locationKey)
        }
    }

    fun <T> modify3(dataKey: DataKey<T>, locationKey: L, block: DataModifier<T?>.(T?) -> Unit) {
        val modified = DataModifier(get(dataKey, locationKey))
        block(modified, modified.get())
        if (modified.hasChanged()) {
            val value = modified.get()
            if (value != null) put(dataKey, locationKey, value) else remove(dataKey, locationKey)
        }
    }

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
