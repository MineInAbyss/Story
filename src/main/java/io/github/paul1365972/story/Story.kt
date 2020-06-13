package io.github.paul1365972.story

import io.github.paul1365972.story.datastore.StoryDataStore
import io.github.paul1365972.story.datastore.caches.CacheDataStore
import io.github.paul1365972.story.datastore.caches.IdentityKey
import io.github.paul1365972.story.datastore.endpoints.file.FileChunkedDataStore
import io.github.paul1365972.story.datastore.endpoints.file.FolderDataStore
import io.github.paul1365972.story.datastore.endpoints.mc.PDCDataStore
import io.github.paul1365972.story.datastore.filters.NullableDataStore
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
import io.github.paul1365972.story.util.MCDataStoreUtil as MC

class Story : JavaPlugin(), StoryService {
    companion object Config {
        private const val PLAYERS = 128
        private const val COMPONENTS = 32
    }

    private val activeDataStores = mutableListOf<Lazy<StoryDataStore<*>>>()

    // 1_024 total (8 ~ 3x3 chunks), at 100 byte/component (and 128 special blocks/chunk) about 400 MB
    // 524_288 total
    override val blockStore: StoryDataStore<Location> by registerLazy {
        CacheDataStore<Location>(
                FileChunkedDataStore(File(dataFolder, "block"), PLAYERS * 8, MC::toChunkKey, MC::toBlockKey),
                PLAYERS * 128 * COMPONENTS, MC::toBlockKey, copyFresh = false
        )
    }

    // 1_024 total (8 ~ 3x3 super chunks), at 100 byte/component (16^2 chunks/superchunk is a given) about 800 MB
    // 1_048_576 total (256 ~ 15x15 view distance)
    override val chunkStore: StoryDataStore<Chunk> by registerLazy {
        CacheDataStore<Chunk>(
                FileChunkedDataStore(File(dataFolder, "chunk"), PLAYERS * 8, MC::toSuperChunkKey, MC::toChunkKey),
                PLAYERS * 256 * COMPONENTS, MC::toChunkKey, copyFresh = false
        )
    }

    // 128 total, we wont have more worlds
    override val worldStore: StoryDataStore<World> by registerLazy {
        CacheDataStore<World>(
                FolderDataStore(File(dataFolder, "world"), MC::toWorldKey),
                128, World::getUID, copyFresh = false
        )
    }

    // 262_144 total
    override val tileEntityStore: StoryDataStore<BlockState> by registerLazy {
        CacheDataStore<BlockState>(
                NullableDataStore(PDCDataStore(), { it as? TileState }),
                PLAYERS * 64 * COMPONENTS, { it }, copyFresh = false
        )
    }

    // Do we need to make the entity object the key ("identity" keys) or the uuid?
    // 524_288 total (more components expected)
    override val entityStore: StoryDataStore<Entity> by registerLazy {
        CacheDataStore<Entity>(
                PDCDataStore(),
                PLAYERS * 128 * COMPONENTS, { it.uniqueId }, copyFresh = false
        )
    }

    // 524_288 total (less components expected)
    override val itemStore: StoryDataStore<ItemStack> by registerLazy {
        CacheDataStore<ItemStack>(
                NullableDataStore(PDCDataStore(), { it.itemMeta }),
                PLAYERS * 128 * COMPONENTS, { IdentityKey(it) }, copyFresh = false, writeThrough = true
        )
    }

    override fun onLoad() {
        server.servicesManager.register(StoryService::class.java, this, this, ServicePriority.Normal)
    }

    override fun onDisable() {
        activeDataStores.filter { it.isInitialized() }.forEach { it.value.close() }
    }

    private fun <T : StoryDataStore<*>> registerLazy(initializer: () -> T): Lazy<T> {
        return lazy { initializer() }.also { activeDataStores += it }
    }
}
