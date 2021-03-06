package io.github.paul1365972.story.util

import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World

object MinecraftKeyUtil {

    @JvmStatic
    fun normalizeLocation(loc: Location) = Location(loc.world, loc.blockX.toDouble(), loc.blockY.toDouble(), loc.blockZ.toDouble())

    @JvmStatic
    fun toBlockKey(loc: Location) = "${loc.world?.uid}:${loc.blockX}:${loc.blockY}:${loc.blockZ}"

    @JvmStatic
    fun toChunkKey(chunk: Chunk) = "${chunk.world.uid}:${chunk.x}:${chunk.z}"

    @JvmStatic
    fun toChunkKey(loc: Location) = toChunkKey(loc.chunk)

    @JvmStatic
    fun toSuperChunkKey(chunk: Chunk) = "${chunk.world.uid}:${chunk.x / 16}:${chunk.z / 16}"

    @JvmStatic
    fun toWorldKey(world: World) = world.uid.toString()
}
