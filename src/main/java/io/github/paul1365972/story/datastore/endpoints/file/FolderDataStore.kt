package io.github.paul1365972.story.datastore.endpoints.file

import io.github.paul1365972.story.datastore.DataKey
import io.github.paul1365972.story.datastore.endpoints.MemoryDataStore
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

    override fun <T : Any> get(dataKey: DataKey<T>, locationKey: L): T? {
        return load(dataKey.namespacedName + ":" + transformer(locationKey))?.let { dataKey.deserializer(it) }
    }

    override fun <T : Any> set(dataKey: DataKey<T>, locationKey: L, value: T?) {
        if (value != null)
            save(toFileName(dataKey, locationKey), dataKey.serializer(value))
        else
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
