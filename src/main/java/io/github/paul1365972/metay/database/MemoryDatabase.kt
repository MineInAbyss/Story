package io.github.paul1365972.metay.database

open class MemoryDatabase : MetayDatabase {

    protected val map = mutableMapOf<String, ByteArray>()

    override fun get(key: String): ByteArray? {
        return map[key]
    }

    override fun put(key: String, data: ByteArray) {
        map[key] = data
    }
}
