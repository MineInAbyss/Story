package io.github.paul1365972.story.datastore.endpoints.file

import io.github.paul1365972.story.datastore.DataKey
import io.github.paul1365972.story.datastore.StoryDataStore
import io.github.paul1365972.story.util.SizedHashMap
import java.io.*
import java.util.*
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class FileChunkedDataStore<L>(
        val folder: File,
        val chunkCacheSize: Int,
        val transformer: (L) -> String,
        val chunkingFunction: (L) -> String
) : StoryDataStore<L> {

    init {
        if (!folder.exists() && !folder.mkdirs())
            throw FileNotFoundException("Could not find or create data folder '${folder.path}'")
    }

    protected val chunks = SizedHashMap<String, Chunk<String>>(chunkCacheSize) { k, v -> unload(k, v) }

    override fun <T : Any> get(dataKey: DataKey<T>, locationKey: L): T? {
        val chunkKey = chunkingFunction(locationKey)
        val chunk = chunks[chunkKey] ?: load(chunkKey)?.also { chunks[chunkKey] = it }
        return chunk?.let {
            it.data[dataKey.namespacedName to transformer(locationKey)]?.let { datum ->
                dataKey.deserializer(datum)
            }
        }
    }

    override fun <T : Any> set(dataKey: DataKey<T>, locationKey: L, value: T?) {
        val chunkName = chunkingFunction(locationKey)
        val chunk = chunks.computeIfAbsent(chunkName) { k -> load(k) ?: Chunk() }
        chunk.dirty = true
        if (value != null) {
            chunk.data[dataKey.namespacedName to transformer(locationKey)] = dataKey.serializer(value)
        } else {
            chunk.data.remove(dataKey.namespacedName to transformer(locationKey))
        }
    }

    override fun close() {
        chunks.forEach { (k, v) -> unload(k, v) }
        chunks.clear()
    }

    protected fun unload(chunkName: String, chunk: Chunk<String>) {
        if (chunk.dirty)
            save(chunkName, chunk)
    }

    protected fun load(chunkName: String): Chunk<String>? {
        val fileName = Base64.getUrlEncoder().encodeToString(chunkName.toByteArray())
        val file = File(folder, fileName)
        if (!file.exists())
            return null
        val chunk = Chunk<String>()
        DataInputStream(ZipInputStream(FileInputStream(file))).use { dis ->
            val entries = dis.readInt()
            repeat(entries) {
                val namespacedName = dis.readUTF()
                val locationkey = dis.readUTF()
                val size = dis.readInt()
                chunk.data[namespacedName to locationkey] = ByteArray(size).apply { dis.readFully(this) }
            }
        }
        return chunk
    }

    protected fun save(chunkName: String, chunk: Chunk<String>) {
        val fileName = Base64.getUrlEncoder().encodeToString(chunkName.toByteArray())
        val file = File(folder, fileName)
        DataOutputStream(ZipOutputStream(FileOutputStream(file))).use { dos ->
            dos.writeInt(chunk.data.size)
            chunk.data.forEach { (k, v) ->
                dos.writeUTF(k.first)
                dos.writeUTF(k.second)
                dos.writeInt(v.size)
                dos.write(v)
            }
        }
    }

    protected class Chunk<L>(
            val data: MutableMap<Pair<String, L>, ByteArray> = mutableMapOf(),
            var dirty: Boolean = false
    )
}