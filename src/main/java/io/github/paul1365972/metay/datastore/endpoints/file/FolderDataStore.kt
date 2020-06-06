package io.github.paul1365972.metay.datastore.endpoints.file

import io.github.paul1365972.metay.datastore.DataKey
import io.github.paul1365972.metay.datastore.endpoints.MemoryDataStore
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.util.*

class FolderDataStore<L>(
        val folder: File,
        val transformer: (L) -> String
) : MemoryDataStore<L>() {

    init {
        if (!folder.exists() && !folder.mkdirs())
            throw FileNotFoundException("Could not find or create data folder '${folder.path}'")
    }

    override fun <T> get(dataKey: DataKey<T>, locationKey: L): T? {
        return load(dataKey.namespacedName + ":" + transformer(locationKey))?.let { dataKey.deserializer(it) }
    }

    override fun <T> put(dataKey: DataKey<T>, locationKey: L, value: T) {
        return save(toFileName(dataKey, locationKey), dataKey.serializer(value))
    }

    override fun <T> remove(dataKey: DataKey<T>, locationKey: L) {
        Files.deleteIfExists(File(folder, toFileName(dataKey, locationKey)).toPath())
    }

    protected fun toFileName(dataKey: DataKey<*>, locationKey: L): String {
        val stringKey = dataKey.namespacedName + ":" + transformer(locationKey)
        return Base64.getUrlEncoder().encodeToString(stringKey.toByteArray())
    }

    protected fun load(fileName: String): ByteArray? {
        val file = File(folder, fileName)
        if (!file.exists())
            return null
        return Files.readAllBytes(file.toPath())
    }

    protected fun save(fileName: String, data: ByteArray) {
        val file = File(folder, fileName)
        Files.write(file.toPath(), data)
    }
}
