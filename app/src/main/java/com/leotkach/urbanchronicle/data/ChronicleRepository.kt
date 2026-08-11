package com.leotkach.urbanchronicle.data

import kotlinx.coroutines.flow.Flow

class ChronicleRepository(
    private val db: AppDatabase,
    private val session: SessionStore,
    private val feedCache: FeedQueryCache = FeedQueryCache(),
) {
    val sessionUser = session.user

    fun observeFeed(categoryId: Long?, query: String): Flow<List<ArticleWithCategory>> =
        db.articles().observeFeed(categoryId, query.trim())

    /** Level 4: snapshot with optional memory cache hit. */
    suspend fun loadFeedCached(categoryId: Long?, query: String): CachedFeed {
        val q = query.trim()
        feedCache.get(categoryId, q)?.let {
            return CachedFeed(it, fromCache = true)
        }
        val fresh = db.articles().loadFeed(categoryId, q)
        feedCache.put(categoryId, q, fresh)
        return CachedFeed(fresh, fromCache = false)
    }

    fun observeArticle(id: Long): Flow<ArticleWithCategory?> =
        db.articles().observeById(id)

    fun observeComments(articleId: Long): Flow<List<CommentEntity>> =
        db.comments().observeForArticle(articleId)

    fun observeCategories(): Flow<List<CategoryEntity>> =
        db.categories().observeAll()

    suspend fun categories(): List<CategoryEntity> = db.categories().getAll()

    suspend fun login(email: String, password: String): Result<SessionUser> {
        val user = db.users().findByEmail(email.trim().lowercase())
            ?: return Result.failure(IllegalArgumentException("Користувача не знайдено"))
        if (user.password != password) {
            return Result.failure(IllegalArgumentException("Невірний пароль"))
        }
        val sessionUser = SessionUser(user.id, user.name, user.email)
        session.signIn(sessionUser)
        return Result.success(sessionUser)
    }

    fun logout() {
        session.signOut()
    }

    fun requireSession(): SessionUser? = session.current()

    suspend fun addCategory(name: String): Long {
        feedCache.invalidate()
        return db.categories().insert(CategoryEntity(name = name.trim()))
    }

    suspend fun renameCategory(id: Long, name: String) {
        val current = db.categories().getById(id) ?: return
        db.categories().update(current.copy(name = name.trim()))
        feedCache.invalidate()
    }

    suspend fun deleteCategory(id: Long) {
        db.categories().delete(id)
        feedCache.invalidate()
    }

    suspend fun createArticle(
        title: String,
        body: String,
        author: String,
        categoryId: Long,
    ): Long {
        feedCache.invalidate()
        return db.articles().insert(
            ArticleEntity(
                title = title.trim(),
                body = body.trim(),
                author = author.trim().ifEmpty { session.current()?.name ?: "Редакція" },
                categoryId = categoryId,
            ),
        )
    }

    suspend fun updateArticle(
        id: Long,
        title: String,
        body: String,
        author: String,
        categoryId: Long,
    ) {
        val current = db.articles().getById(id) ?: return
        db.articles().update(
            current.copy(
                title = title.trim(),
                body = body.trim(),
                author = author.trim().ifEmpty { "Редакція" },
                categoryId = categoryId,
            ),
        )
        feedCache.invalidate()
    }

    suspend fun deleteArticle(id: Long) {
        db.articles().delete(id)
        feedCache.invalidate()
    }

    suspend fun addComment(articleId: Long, author: String, text: String): Long {
        feedCache.invalidate()
        return db.comments().insert(
            CommentEntity(
                articleId = articleId,
                author = author.trim().ifEmpty { session.current()?.name ?: "Читач" },
                text = text.trim(),
            ),
        )
    }

    suspend fun deleteComment(id: Long) {
        db.comments().delete(id)
        feedCache.invalidate()
    }
}
