package com.leotkach.urbanchronicle.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.leotkach.urbanchronicle.data.ChronicleRepository
import com.leotkach.urbanchronicle.ui.screens.ArticleDetailScreen
import com.leotkach.urbanchronicle.ui.screens.ArticleEditScreen
import com.leotkach.urbanchronicle.ui.screens.CategoriesScreen
import com.leotkach.urbanchronicle.ui.screens.FeedScreen
import com.leotkach.urbanchronicle.ui.theme.UrbanChronicleTheme

@Composable
fun UrbanChronicleApp(repository: ChronicleRepository) {
    UrbanChronicleTheme {
        val nav = rememberNavController()
        NavHost(navController = nav, startDestination = Routes.Feed) {
            composable(Routes.Feed) {
                FeedScreen(
                    repository = repository,
                    onOpenArticle = { nav.navigate(Routes.article(it)) },
                    onNewArticle = { nav.navigate(Routes.ArticleNew) },
                    onCategories = { nav.navigate(Routes.Categories) },
                )
            }
            composable(Routes.Categories) {
                CategoriesScreen(
                    repository = repository,
                    onBack = { nav.popBackStack() },
                )
            }
            composable(Routes.ArticleNew) {
                ArticleEditScreen(
                    articleId = null,
                    repository = repository,
                    onBack = { nav.popBackStack() },
                    onSaved = { id ->
                        nav.popBackStack()
                        nav.navigate(Routes.article(id))
                    },
                )
            }
            composable(
                route = Routes.ArticleDetail,
                arguments = listOf(navArgument("id") { type = NavType.LongType }),
            ) { entry ->
                val id = entry.arguments?.getLong("id") ?: return@composable
                ArticleDetailScreen(
                    articleId = id,
                    repository = repository,
                    onBack = { nav.popBackStack() },
                    onEdit = { nav.navigate(Routes.edit(id)) },
                    onDeleted = {
                        nav.popBackStack(Routes.Feed, inclusive = false)
                    },
                )
            }
            composable(
                route = Routes.ArticleEdit,
                arguments = listOf(navArgument("id") { type = NavType.LongType }),
            ) { entry ->
                val id = entry.arguments?.getLong("id") ?: return@composable
                ArticleEditScreen(
                    articleId = id,
                    repository = repository,
                    onBack = { nav.popBackStack() },
                    onSaved = {
                        nav.popBackStack()
                    },
                )
            }
        }
    }
}
