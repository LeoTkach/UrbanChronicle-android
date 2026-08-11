package com.leotkach.urbanchronicle.ui

object Routes {
    const val Feed = "feed"
    const val Login = "login"
    const val Categories = "categories"
    const val ArticleNew = "article/new"
    const val ArticleDetail = "article/{id}"
    const val ArticleEdit = "article/{id}/edit"

    fun article(id: Long) = "article/$id"
    fun edit(id: Long) = "article/$id/edit"
}
