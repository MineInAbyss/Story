package io.github.paul1365972.story

import io.github.paul1365972.story.datastore.PersistentDataStore
import io.github.paul1365972.story.datastore.caches.CachePersistentDataStore
import io.github.paul1365972.story.datastore.endpoints.file.FileChunkedPersistentDataStore
import io.github.paul1365972.story.datastore.endpoints.file.FolderPersistentDataStore
import io.github.paul1365972.story.datastore.endpoints.mc.ItemStackPersistentDataStore
import io.github.paul1365972.story.datastore.endpoints.mc.PDCPersistentDataStore
import io.github.paul1365972.story.datastore.filters.NullablePersistentDataStore
import io.github.paul1365972.story.tracking.TrackingListener
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.BlockState
import org.bukkit.block.TileState
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import io.github.paul1365972.story.util.MinecraftKeyUtil as MC

class Story : JavaPlugin(), StoryService {
    companion object Config {
        private const val PLAYERS = 128
        private const val COMPONENTS = 32
    }

    private val activeDataStores = mutableListOf<Lazy<PersistentDataStore<*>>>()

    // 1_024 total (8 ~ 3x3 chunks), at 100 byte/component (and 128 special blocks/chunk) about 400 MB
    // 524_288 total
    override val defaultBlockStore: PersistentDataStore<Location> by registerLazy {
        CachePersistentDataStore<Location>(
                FileChunkedPersistentDataStore(File(dataFolder, "block"), PLAYERS * 8, MC::toChunkKey, MC::toBlockKey),
                PLAYERS * 128 * COMPONENTS, MC::toBlockKey, copyFresh = false
        )
    }

    // 1_024 total (8 ~ 3x3 super chunks), at 100 byte/component (16^2 chunks/superchunk is a given) about 800 MB
    // 1_048_576 total (256 ~ 15x15 view distance)
    override val defaultChunkStore: PersistentDataStore<Chunk> by registerLazy {
        CachePersistentDataStore<Chunk>(
                FileChunkedPersistentDataStore(File(dataFolder, "chunk"), PLAYERS * 8, MC::toSuperChunkKey, MC::toChunkKey),
                PLAYERS * 256 * COMPONENTS, MC::toChunkKey, copyFresh = false
        )
    }

    // 128 total, we wont have more worlds
    override val defaultWorldStore: PersistentDataStore<World> by registerLazy {
        CachePersistentDataStore<World>(
                FolderPersistentDataStore(File(dataFolder, "world"), MC::toWorldKey),
                128 * COMPONENTS, World::getUID, copyFresh = false
        )
    }

    // 262_144 total
    override val defaultTileEntityStore: PersistentDataStore<BlockState> by registerLazy {
        CachePersistentDataStore<BlockState>(
                NullablePersistentDataStore(PDCPersistentDataStore(), { it.blockData as? TileState }),
                PLAYERS * 64 * COMPONENTS, { it }, copyFresh = true
        )
    }

    // Do we need to make the entity object the key ("identity" keys) or the uuid?
    // 524_288 total (more components expected)
    override val defaultEntityStore: PersistentDataStore<Entity> by registerLazy {
        CachePersistentDataStore<Entity>(
                PDCPersistentDataStore(),
                PLAYERS * 128 * COMPONENTS, { it.uniqueId }, copyFresh = true
        )
    }

    // 524_288 total (less components expected)
    override val defaultItemStore: PersistentDataStore<ItemStack> by registerLazy {
        CachePersistentDataStore<ItemStack>(
                ItemStackPersistentDataStore(),
                PLAYERS * 128 * COMPONENTS, { it.itemMeta?.persistentDataContainer },
                copyFresh = true, writeThrough = true
        )
    }

    override fun onLoad() {
        server.servicesManager.register(StoryService::class.java, this, this, ServicePriority.Normal)
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(TrackingListener, this)
        server.scheduler.runTaskTimer(this, { _ ->
            activeDataStores.filter { it.isInitialized() }.forEach { it.value.tick() }
        }, 1, 1)
    }

    override fun onDisable() {
        activeDataStores.filter { it.isInitialized() }.forEach { it.value.close() }
    }

    private fun <T : PersistentDataStore<*>> registerLazy(initializer: () -> T): Lazy<T> {
        return lazy { initializer() }.also { activeDataStores.add(it) }
    }
}
