package io.github.paul1365972.story

import io.github.paul1365972.story.datastore.ObjectDataStore
import io.github.paul1365972.story.datastore.caches.CacheDataStore
import io.github.paul1365972.story.datastore.endpoints.file.FileChunkedDataStore
import io.github.paul1365972.story.datastore.endpoints.file.FolderDataStore
import io.github.paul1365972.story.datastore.endpoints.mc.ItemStackDataStore
import io.github.paul1365972.story.datastore.endpoints.mc.PDCDataStore
import io.github.paul1365972.story.datastore.filters.NullableDataStore
import io.github.paul1365972.story.tracking.TrackingListener
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.BlockState
import org.bukkit.block.TileState
import org.bukkit.entity.Entity
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import io.github.paul1365972.story.util.MinecraftKeyUtil as MC

class Story : JavaPlugin(), StoryService, Listener {
    companion object Config {
        private const val PLAYERS = 128
        private const val COMPONENTS = 32
    }

    // 1_024 total (8 ~ 3x3 chunks), at 100 byte/component (and 128 special blocks/chunk) about 400 MB
    // 524_288 total
    override val defaultBlockStore: ObjectDataStore<Location> by lazy {
        CacheDataStore<Location>(
                FileChunkedDataStore(File(dataFolder, "block"), PLAYERS * 8, MC::toChunkKey, MC::toBlockKey),
                PLAYERS * 128 * COMPONENTS, MC::toBlockKey, copyFresh = false
        ).register()
    }

    // 1_024 total (8 ~ 3x3 super chunks), at 100 byte/component (16^2 chunks/superchunk is a given) about 800 MB
    // 1_048_576 total (256 ~ 15x15 view distance)
    override val defaultChunkStore: ObjectDataStore<Chunk> by lazy {
        CacheDataStore<Chunk>(
                FileChunkedDataStore(File(dataFolder, "chunk"), PLAYERS * 8, MC::toSuperChunkKey, MC::toChunkKey),
                PLAYERS * 256 * COMPONENTS, MC::toChunkKey, copyFresh = false
        ).register()
    }

    // 128 total, we wont have more worlds
    override val defaultWorldStore: ObjectDataStore<World> by lazy {
        CacheDataStore<World>(
                FolderDataStore(File(dataFolder, "world"), MC::toWorldKey),
                128 * COMPONENTS, World::getUID, copyFresh = false
        ).register()
    }

    // 262_144 total
    override val defaultTileEntityStore: ObjectDataStore<BlockState> by lazy {
        CacheDataStore<BlockState>(
                NullableDataStore(PDCDataStore(), { it.blockData as? TileState }),
                PLAYERS * 64 * COMPONENTS, { it }, copyFresh = true
        ).register()
    }

    // Do we need to make the entity object the key ("identity" keys) or the uuid?
    // 524_288 total (more components expected)
    override val defaultEntityStore: ObjectDataStore<Entity> by lazy {
        CacheDataStore<Entity>(
                PDCDataStore(),
                PLAYERS * 128 * COMPONENTS, { it.uniqueId }, copyFresh = true
        ).register()
    }

    // 524_288 total (less components expected)
    override val defaultItemStore: ObjectDataStore<ItemStack> by lazy {
        CacheDataStore<ItemStack>(
                ItemStackDataStore(),
                PLAYERS * 128 * COMPONENTS, { it.itemMeta?.persistentDataContainer },
                copyFresh = true, writeThrough = true
        ).register()
    }

    override fun onLoad() {
        server.servicesManager.register(StoryService::class.java, this, this, ServicePriority.Normal)
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(TrackingListener, this)
        server.pluginManager.registerEvents(DataStoreManager, this)
        server.scheduler.runTaskTimer(this, DataStoreManager, 1, 1)
    }

    override fun onDisable() {
        DataStoreManager.close()
    }
}
