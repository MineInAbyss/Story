package io.github.paul1365972.story.datastore.endpoints.file

import io.github.paul1365972.story.datastore.PersistentDataStore
import io.github.paul1365972.story.key.PersistentDataKey
import java.io.*
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class FileDataStore(
        val file: File
) : PersistentDataStore<String> {

    init {
        DataInputStream(ZipInputStream(FileInputStream(file))).use { dis ->
            val entries = dis.readInt()
            repeat(entries) {
                val namespacedkey = dis.readUTF()
                val locationkey = dis.readUTF()
                val size = dis.readInt()
                map[namespacedkey to locationkey] = ByteArray(size).apply { dis.readFully(this) }
            }
        }
    }

    protected val map = ConcurrentHashMap<Pair<String, String>, ByteArray>()

    override fun <T : Any> get(dataKey: PersistentDataKey<T>, locationKey: String): T? {
        @Suppress("UNCHECKED_CAST")
        return map[dataKey.namespacedName to locationKey]?.let { dataKey.deserialize(it) }
    }

    override fun <T : Any> set(dataKey: PersistentDataKey<T>, locationKey: String, value: T?) {
        val key = dataKey.namespacedName to locationKey
        if (value != null)
            map[key] = dataKey.serialize(value)
        else
            map.remove(key)
    }

    override fun close() {
        DataOutputStream(ZipOutputStream(FileOutputStream(file))).use { dos ->
            dos.writeInt(map.size)
            map.forEach {
                dos.writeUTF(it.key.first)
                dos.writeUTF(it.key.second)
                dos.writeInt(it.value.size)
                dos.write(it.value)
            }
        }
        map.clear()
    }
}
