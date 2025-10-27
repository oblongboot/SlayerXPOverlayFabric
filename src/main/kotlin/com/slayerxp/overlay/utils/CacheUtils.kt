package com.slayerxp.overlay.utils

import java.util.LinkedHashMap
import kotlin.time.Duration

/**
* beep boop
*/
class CacheUtils<K, V>(
    private val maxSize: Int,
    private val ttl: Duration
) {

    private data class CacheEntry<V>(val value: V, val expiryTimeMs: Long)

    private val cache = object : LinkedHashMap<K, CacheEntry<V>>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, CacheEntry<V>>?): Boolean {
            return size > maxSize
        }
    }

    private val lock = Any()

    fun getOrPut(key: K, defaultValue: () -> V): V {
        val now = System.currentTimeMillis()
        synchronized(lock) {
            val entry = cache[key]

            if (entry != null && entry.expiryTimeMs > now) {
                // Valid cached entry
                return entry.value
            }

            // Expired or missing, compute and cache
            val newValue = defaultValue()
            cache[key] = CacheEntry(newValue, now + ttl.inWholeMilliseconds)
            return newValue
        }
    }

    fun clear() {
        synchronized(lock) {
            cache.clear()
        }
    }

    fun size(): Int {
        synchronized(lock) {
            return cache.size
        }
    }
}
