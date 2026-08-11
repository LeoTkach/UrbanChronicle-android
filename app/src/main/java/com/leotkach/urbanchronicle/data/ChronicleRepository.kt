package com.leotkach.urbanchronicle.data

import kotlinx.coroutines.flow.Flow

class ChronicleRepository(private val db: AppDatabase) {
    fun observeFeed(categoryId: Long?): Flow<List<ArticleWithCategory>> =
        db.articles().observeFeed(categoryId)

    fun observeArticle(id: Long): Flow<ArticleWithCategory?> =
        db.articles().observeById(id)

    fun observeComments(articleId: Long): Flow<List<CommentEntity>> =
        db.comments().observeForArticle(articleId)

    fun observeCategories(): Flow<List<CategoryEntity>> =
        db.categories().observeAll()

    suspend fun categories(): List<CategoryEntity> = db.categories().getAll()

    suspend fun addCategory(name: String): Long =
        db.categories().insert(CategoryEntity(name = name.trim()))

    suspend fun renameCategory(id: Long, name: String) {
        val current = db.categories().getById(id) ?: return
        db.categories().update(current.copy(name = name.trim()))
    }

    suspend fun deleteCategory(id: Long) {
        db.categories().delete(id)
    }

    suspend fun createArticle(
        title: String,
        body: String,
        author: String,
        categoryId: Long,
    ): Long =
        db.articles().insert(
            ArticleEntity(
                title = title.trim(),
                body = body.trim(),
                author = author.trim().ifEmpty { "Редакція" },
                categoryId = categoryId,
            ),
        )

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
    }

    suspend fun deleteArticle(id: Long) {
        db.articles().delete(id)
    }

    suspend fun addComment(articleId: Long, author: String, text: String): Long =
        db.comments().insert(
            CommentEntity(
                articleId = articleId,
                author = author.trim().ifEmpty { "Читач" },
                text = text.trim(),
            ),
        )

    suspend fun deleteComment(id: Long) {
        db.comments().delete(id)
    }
}
