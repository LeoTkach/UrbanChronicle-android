package com.leotkach.urbanchronicle.data

/**
 * Level-4 style query cache: remember last feed results in memory
 * so repeated identical filters/search do not re-hit SQLite immediately.
 */
class FeedQueryCache(
    private val ttlMs: Long = 30_000L,
) {
    private data class Key(val categoryId: Long?, val query: String)
    private data class Entry(
        val articles: List<ArticleWithCategory>,
        val storedAt: Long,
    )

    @Volatile
    private var entry: Pair<Key, Entry>? = null

    fun get(categoryId: Long?, query: String): List<ArticleWithCategory>? {
        val current = entry ?: return null
        if (current.first != Key(categoryId, query)) return null
        if (System.currentTimeMillis() - current.second.storedAt > ttlMs) return null
        return current.second.articles
    }

    fun put(categoryId: Long?, query: String, articles: List<ArticleWithCategory>) {
        entry = Key(categoryId, query) to Entry(articles, System.currentTimeMillis())
    }

    fun invalidate() {
        entry = null
    }
}

data class CachedFeed(
    val articles: List<ArticleWithCategory>,
    val fromCache: Boolean,
)
