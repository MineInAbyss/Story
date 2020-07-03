package io.github.paul1365972.story.accessors

import io.github.paul1365972.story.access.DataStoreInstanceAccess
import io.github.paul1365972.story.access.InstanceAccess
import io.github.paul1365972.story.access.ItemStackInstanceAccess
import io.github.paul1365972.story.datastore.ObjectDataStore
import io.github.paul1365972.story.key.PersistentDataKey
import org.bukkit.inventory.ItemStack

interface DataAccessor<A, in L> {
    fun access(locationKey: L): A
}

operator fun <A, L> L.get(
        accessor: DataAccessor<A, L>
) = accessor.access(this)

class PersistentDataStoreAccessor<T : Any, L>(
        val dataStore: ObjectDataStore<L>,
        val dataKey: PersistentDataKey<T>
) : DataAccessor<InstanceAccess<T, L>, L> {
    override fun access(locationKey: L) = DataStoreInstanceAccess(dataStore, dataKey, locationKey)
}

class ItemStackDataAccessor<T : Any>(
        val dataKey: PersistentDataKey<T>
) : DataAccessor<InstanceAccess<T, ItemStack>, ItemStack> {
    override fun access(locationKey: ItemStack) = ItemStackInstanceAccess(dataKey, locationKey)
}
