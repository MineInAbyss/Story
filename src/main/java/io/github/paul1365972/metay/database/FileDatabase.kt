package io.github.paul1365972.metay.database

import java.io.*
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class FileDatabase(
        val file: File
) : MemoryDatabase() {

    init {
        DataInputStream(ZipInputStream(FileInputStream(file))).use { dis ->
            val entries = dis.readInt()
            repeat(entries) {
                val key = dis.readUTF()
                val size = dis.readInt()
                map[key] = ByteArray(size).apply { dis.readFully(this) }
            }
        }
    }

    override fun onClose() {
        DataOutputStream(ZipOutputStream(FileOutputStream(file))).use { dos ->
            dos.writeInt(map.size)
            map.forEach {
                dos.writeUTF(it.key)
                dos.writeInt(it.value.size)
                dos.write(it.value)
            }
        }
    }
}
