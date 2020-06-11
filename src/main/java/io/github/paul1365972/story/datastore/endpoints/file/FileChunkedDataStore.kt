package io.github.paul1365972.story.datastore.endpoints.file

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import io.github.paul1365972.story.datastore.StoryDataStore
import io.github.paul1365972.story.key.DataKey
import java.io.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutionException
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

    @Suppress("UNCHECKED_CAST")
    private val cache: LoadingCache<String, Chunk<String>> = CacheBuilder.newBuilder()
            .maximumSize(chunkCacheSize.toLong())
            .removalListener<String, Chunk<String>> {
                if (!it.value.dirty) return@removalListener
                val fileName = Base64.getUrlEncoder().encodeToString(it.key.toByteArray())
                val file = File(folder, fileName)
                DataOutputStream(ZipOutputStream(FileOutputStream(file))).use { dos ->
                    dos.writeInt(it.value.data.size)
                    it.value.data.forEach { (k, v) ->
                        dos.writeUTF(k.first)
                        dos.writeUTF(k.second)
                        dos.writeInt(v.size)
                        dos.write(v)
                    }
                }
            }.build(object : CacheLoader<String, Chunk<String>>() {
                override fun load(key: String): Chunk<String> {
                    val fileName = Base64.getUrlEncoder().encodeToString(key.toByteArray())
                    val file = File(folder, fileName)
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
            })

    override fun <T : Any> get(dataKey: DataKey<T>, locationKey: L): T? {
        val chunkKey = chunkingFunction(locationKey)
        val chunk = try {
            cache.get(chunkKey)
        } catch (e: ExecutionException) {
            null
        }
        return chunk?.let {
            it.data[dataKey.namespacedName to transformer(locationKey)]?.let { datum ->
                dataKey.deserialize(datum)
            }
        }
    }

    override fun <T : Any> set(dataKey: DataKey<T>, locationKey: L, value: T?) {
        val chunkKey = chunkingFunction(locationKey)
        val chunk = try {
            cache.get(chunkKey)
        } catch (e: ExecutionException) {
            null
        }
                ?: Chunk<String>(true).also { cache.put(chunkKey, it) }
        chunk.dirty = true
        val key = dataKey.namespacedName to transformer(locationKey)
        if (value != null) {
            chunk.data[key] = dataKey.serialize(value)
        } else {
            chunk.data.remove(key)
        }
    }

    override fun close() {
        cache.invalidateAll()
    }

    protected class Chunk<L>(
            var dirty: Boolean = false,
            val data: ConcurrentHashMap<Pair<String, L>, ByteArray> = ConcurrentHashMap()
    )
}
