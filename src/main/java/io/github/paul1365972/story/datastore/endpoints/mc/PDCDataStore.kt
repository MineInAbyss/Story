package io.github.paul1365972.story.datastore.endpoints.mc

import io.github.paul1365972.story.datastore.StoryDataStore
import io.github.paul1365972.story.key.DataKey
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class PDCDataStore : StoryDataStore<PersistentDataHolder> {

    private val lock = ReentrantReadWriteLock()

    override fun <T : Any> get(dataKey: DataKey<T>, locationKey: PersistentDataHolder): T? {
        lock.read {
            return locationKey.persistentDataContainer[dataKey.namespacedKey, PersistentDataType.BYTE_ARRAY]?.let {
                dataKey.deserialize(it)
            }
        }
    }

    override fun <T : Any> set(dataKey: DataKey<T>, locationKey: PersistentDataHolder, value: T?) {
        lock.write {
            if (value != null)
                locationKey.persistentDataContainer[dataKey.namespacedKey, PersistentDataType.BYTE_ARRAY] = dataKey.serialize(value)
            else
                locationKey.persistentDataContainer.remove(dataKey.namespacedKey)
        }
    }
}
