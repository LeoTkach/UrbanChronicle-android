package com.leotkach.urbanchronicle.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
)

@Entity(
    tableName = "articles",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("categoryId")],
)
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val body: String,
    val author: String,
    val categoryId: Long,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = ArticleEntity::class,
            parentColumns = ["id"],
            childColumns = ["articleId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("articleId")],
)
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val articleId: Long,
    val author: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis(),
)

data class ArticleWithCategory(
    val id: Long,
    val title: String,
    val body: String,
    val author: String,
    val categoryId: Long,
    val categoryName: String,
    val createdAt: Long,
    val commentCount: Int,
)
