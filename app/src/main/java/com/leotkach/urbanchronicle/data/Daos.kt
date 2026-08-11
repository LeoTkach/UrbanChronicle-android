package com.leotkach.urbanchronicle.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

    @Insert
    suspend fun insert(user: UserEntity): Long
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY name COLLATE NOCASE")
    suspend fun getAll(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): CategoryEntity?

    @Insert
    suspend fun insert(category: CategoryEntity): Long

    @Update
    suspend fun update(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int
}

@Dao
interface ArticleDao {
    @Query(
        """
        SELECT a.id AS id, a.title AS title, a.body AS body, a.author AS author,
               a.categoryId AS categoryId, c.name AS categoryName, a.createdAt AS createdAt,
               (SELECT COUNT(*) FROM comments cm WHERE cm.articleId = a.id) AS commentCount
        FROM articles a
        INNER JOIN categories c ON c.id = a.categoryId
        WHERE (:categoryId IS NULL OR a.categoryId = :categoryId)
          AND (
            :query = '' OR
            a.title LIKE '%' || :query || '%' COLLATE NOCASE OR
            a.body LIKE '%' || :query || '%' COLLATE NOCASE OR
            a.author LIKE '%' || :query || '%' COLLATE NOCASE
          )
        ORDER BY a.createdAt DESC
        """,
    )
    fun observeFeed(categoryId: Long?, query: String): Flow<List<ArticleWithCategory>>

    @Query(
        """
        SELECT a.id AS id, a.title AS title, a.body AS body, a.author AS author,
               a.categoryId AS categoryId, c.name AS categoryName, a.createdAt AS createdAt,
               (SELECT COUNT(*) FROM comments cm WHERE cm.articleId = a.id) AS commentCount
        FROM articles a
        INNER JOIN categories c ON c.id = a.categoryId
        WHERE (:categoryId IS NULL OR a.categoryId = :categoryId)
          AND (
            :query = '' OR
            a.title LIKE '%' || :query || '%' COLLATE NOCASE OR
            a.body LIKE '%' || :query || '%' COLLATE NOCASE OR
            a.author LIKE '%' || :query || '%' COLLATE NOCASE
          )
        ORDER BY a.createdAt DESC
        """,
    )
    suspend fun loadFeed(categoryId: Long?, query: String): List<ArticleWithCategory>

    @Query(
        """
        SELECT a.id AS id, a.title AS title, a.body AS body, a.author AS author,
               a.categoryId AS categoryId, c.name AS categoryName, a.createdAt AS createdAt,
               (SELECT COUNT(*) FROM comments cm WHERE cm.articleId = a.id) AS commentCount
        FROM articles a
        INNER JOIN categories c ON c.id = a.categoryId
        WHERE a.id = :id
        """,
    )
    fun observeById(id: Long): Flow<ArticleWithCategory?>

    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getById(id: Long): ArticleEntity?

    @Insert
    suspend fun insert(article: ArticleEntity): Long

    @Update
    suspend fun update(article: ArticleEntity)

    @Query("DELETE FROM articles WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE articleId = :articleId ORDER BY createdAt ASC")
    fun observeForArticle(articleId: Long): Flow<List<CommentEntity>>

    @Insert
    suspend fun insert(comment: CommentEntity): Long

    @Query("DELETE FROM comments WHERE id = :id")
    suspend fun delete(id: Long)
}
