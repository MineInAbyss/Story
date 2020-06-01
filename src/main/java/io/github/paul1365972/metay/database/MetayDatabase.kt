package io.github.paul1365972.metay.database

interface MetayDatabase {
    fun get(key: String): ByteArray?
    fun put(key: String, data: ByteArray)
    fun onClose() {}
}

interface BatchDatabase : MetayDatabase {
    override fun put(key: String, data: ByteArray) = putBatch(mapOf(key to data))
    fun putBatch(data: Map<String, ByteArray>)
}
