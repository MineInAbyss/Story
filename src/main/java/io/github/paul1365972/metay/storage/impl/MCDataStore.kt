package io.github.paul1365972.metay.storage.impl

import io.github.paul1365972.metay.storage.MetayDataStore
import io.github.paul1365972.metay.storage.TransformingDataStore
import org.bukkit.Chunk
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataHolder
import java.util.*

//TODO this is a bit experimental, probably dont need to convert the <location> to string so aggressively

class UUIDDataStore(
        underlying: MetayDataStore<String>
) : TransformingDataStore<UUID, String>(underlying, { "$it" })

class ChunkDataStore(
        underlying: MetayDataStore<String>
) : TransformingDataStore<Chunk, String>(underlying, { "${it.world.uid}:${it.x}:${it.z}" })

class LocationDataStore(
        underlying: MetayDataStore<String>
) : TransformingDataStore<Block, String>(underlying, { "${it.world.uid}:${it.x}:${it.y}:${it.z}" })

class EntityDataStore(
        underlying: MetayDataStore<PersistentDataHolder>
) : TransformingDataStore<Entity, PersistentDataHolder>(underlying, { it })

class ItemDataStore(
        underlying: MetayDataStore<PersistentDataHolder>
) : TransformingDataStore<ItemStack, PersistentDataHolder>(underlying, { it.itemMeta })
