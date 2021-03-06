package io.github.paul1365972.story.util

class IdentityKey<T>(
        val ref: T
) {
    override fun equals(other: Any?): Boolean {
        return ref === other
    }

    override fun hashCode(): Int {
        return System.identityHashCode(ref)
    }

    override fun toString() = "IdentityKey(ref=$ref)"
}
