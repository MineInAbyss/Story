package io.github.paul1365972.story.datastore.endpoints.file

import io.github.paul1365972.story.datastore.DataStore
import io.github.paul1365972.story.key.DataKeyI
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.util.*

class FolderDataStore<L>(
        val folder: File,
        val transformer: (L) -> String
) : DataStore<L> {

    init {
        if (!folder.exists() && !folder.mkdirs())
            throw FileNotFoundException("Could not find or create data folder '${folder.path}'")
    }

    override fun get(dataKey: DataKeyI, locationKey: L): ByteArray? {
        return load(dataKey.namespacedName + ":" + transformer(locationKey))
    }

    override fun set(dataKey: DataKeyI, locationKey: L, data: ByteArray?) {
        if (data != null)
            save(toFileName(dataKey, locationKey), data)
        else
            Files.deleteIfExists(File(folder, toFileName(dataKey, locationKey)).toPath())
    }

    protected fun toFileName(dataKey: DataKeyI, locationKey: L): String {
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
