package io.github.paul1365972.metay.storage.endpoints

import java.io.*
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class FileDataStore(
        val file: File
) : MemoryDataStore<String>() {

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

    override fun onClose() {
        DataOutputStream(ZipOutputStream(FileOutputStream(file))).use { dos ->
            dos.writeInt(map.size)
            map.forEach {
                dos.writeUTF(it.key.first)
                dos.writeUTF(it.key.second)
                dos.writeInt(it.value.size)
                dos.write(it.value)
            }
        }
    }
}
