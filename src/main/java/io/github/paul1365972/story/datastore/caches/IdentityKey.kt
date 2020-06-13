package io.github.paul1365972.story.datastore.caches

class IdentityKey<T>(
        val ref: T
) {
    override fun equals(other: Any?): Boolean {
        return ref === other
    }

    override fun hashCode(): Int {
        return ref?.hashCode() ?: 0
    }
}
