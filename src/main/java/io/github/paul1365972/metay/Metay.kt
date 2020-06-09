package io.github.paul1365972.metay

import io.github.paul1365972.metay.datastore.MetayDataStore
import io.github.paul1365972.metay.datastore.caches.CacheDataStore
import io.github.paul1365972.metay.datastore.endpoints.file.FileChunkedDataStore
import io.github.paul1365972.metay.datastore.endpoints.file.FolderDataStore
import io.github.paul1365972.metay.datastore.endpoints.mc.PDCDataStore
import io.github.paul1365972.metay.datastore.filters.NullableDataStore
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import io.github.paul1365972.metay.util.MCDataStoreUtil as MC

class Metay : JavaPlugin(), MetayService {
    // TODO Estimate good cache sizes

    private val activeDataStores = mutableListOf<Lazy<MetayDataStore<*>>>()

    // Chunk cache size of 1024 seems reasonable if we expect each player to interact with 5 chunks at a time
    // Cache size just guesstimated, as i am still not sure how this will be used
    override val blockStore: MetayDataStore<Location> by registerLazy {
        CacheDataStore<Location>(
                FileChunkedDataStore(File(dataFolder, "block"), 256, MC::toChunkKey, MC::toBlockKey),
                1024, MC::toBlockKey)
    }

    // We load max 512 super chunks at a time, just guessing
    // 10x10 view distance at 50 plays leads to 5000 chunks, so we cache about double that amount
    override val chunkStore: MetayDataStore<Chunk> by registerLazy {
        CacheDataStore<Chunk>(
                FileChunkedDataStore(File(dataFolder, "chunk"), 512, MC::toSuperChunkKey, MC::toChunkKey),
                8192, MC::toChunkKey)
    }

    // We wont have more than 128 worlds
    override val worldStore: MetayDataStore<World> by registerLazy {
        CacheDataStore<World>(
                FolderDataStore(File(dataFolder, "world"), MC::toWorldKey),
                128, World::getUID)
    }

    // Not sure how this will be used either, so just going with 50 player with 20 blocks each seems reasonable
    override val tileEntityStore: MetayDataStore<Block> by registerLazy {
        CacheDataStore<Block>(
                NullableDataStore(PDCDataStore(), { it.state as? PersistentDataHolder }),
                1024, { it })
    }

    // Do we need to make the entity object the key ("identity" keys) or the uuid?
    // Cache size just guesstimated, 50 players with ~100 mobs each expected
    override val entityStore: MetayDataStore<Entity> by registerLazy {
        CacheDataStore<Entity>(
                PDCDataStore(),
                8124, { it.uniqueId })
    }

    // 4096 should be enough when we expect ~50 players with their inventories
    override val itemStore: MetayDataStore<ItemStack> by registerLazy {
        CacheDataStore<ItemStack>(
                NullableDataStore(PDCDataStore(), { it.itemMeta }),
                4096)
    }

    override fun onLoad() {
        server.servicesManager.register(MetayService::class.java, this, this, ServicePriority.Normal)
    }

    override fun onDisable() {
        activeDataStores.filter { it.isInitialized() }.forEach { it.value.close() }
    }

    private fun <T : MetayDataStore<*>> registerLazy(initializer: () -> T): Lazy<T> {
        return lazy { initializer() }.also { activeDataStores += it }
    }
}
