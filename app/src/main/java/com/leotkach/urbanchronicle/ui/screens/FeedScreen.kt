package com.leotkach.urbanchronicle.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leotkach.urbanchronicle.data.ArticleWithCategory
import com.leotkach.urbanchronicle.data.ChronicleRepository
import com.leotkach.urbanchronicle.ui.components.AppScaffold
import com.leotkach.urbanchronicle.ui.components.SectionLabel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FeedScreen(
    repository: ChronicleRepository,
    onOpenArticle: (Long) -> Unit,
    onNewArticle: () -> Unit,
    onCategories: () -> Unit,
    onLogin: () -> Unit,
) {
    var selectedCategory by rememberSaveable { mutableStateOf<Long?>(null) }
    var query by rememberSaveable { mutableStateOf("") }
    var fromCache by remember { mutableStateOf(false) }
    val session by repository.sessionUser.collectAsStateWithLifecycle()
    val categories by repository.observeCategories().collectAsStateWithLifecycle(emptyList())
    val articles by repository.observeFeed(selectedCategory, query)
        .collectAsStateWithLifecycle(emptyList())

    LaunchedEffect(selectedCategory, query) {
        val snap = repository.loadFeedCached(selectedCategory, query)
        fromCache = snap.fromCache
    }

    AppScaffold(
        title = "UrbanChronicle",
        subtitle = "Якби класики дивилися на нашу дійсність - місто, вулиці, побут.",
        scrollable = true,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (session != null) "Ви: ${session!!.name}" else "Гість (лише читання)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (session != null) {
                OutlinedButton(onClick = { repository.logout() }) {
                    Icon(Icons.Outlined.Logout, contentDescription = null)
                    Spacer(Modifier.padding(4.dp))
                    Text("Вихід")
                }
            } else {
                OutlinedButton(onClick = onLogin) {
                    Icon(Icons.Outlined.Login, contentDescription = null)
                    Spacer(Modifier.padding(4.dp))
                    Text("Вхід")
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Button(
                onClick = {
                    if (session != null) onNewArticle() else onLogin()
                },
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.padding(4.dp))
                Text("Нова стаття")
            }
            OutlinedButton(
                onClick = {
                    if (session != null) onCategories() else onLogin()
                },
            ) {
                Icon(Icons.Outlined.Category, contentDescription = null)
            }
        }

        Spacer(Modifier.height(18.dp))
        SectionLabel("Пошук")
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Заголовок, текст або автор") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        if (fromCache) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Результат підтягнуто з кешу запиту (рівень 4)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(Modifier.height(18.dp))
        SectionLabel("Категорії")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { selectedCategory = null },
                label = { Text("Усі") },
            )
            categories.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat.id,
                    onClick = { selectedCategory = cat.id },
                    label = { Text(cat.name) },
                )
            }
        }

        Spacer(Modifier.height(18.dp))
        SectionLabel("Стрічка")
        if (articles.isEmpty()) {
            Text(
                text = if (query.isBlank()) {
                    "Поки немає статей у цій категорії."
                } else {
                    "Нічого не знайдено за запитом «$query»."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            articles.forEach { article ->
                ArticleCard(article = article, onClick = { onOpenArticle(article.id) })
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ArticleCard(
    article: ArticleWithCategory,
    onClick: () -> Unit,
) {
    val date = SimpleDateFormat("d MMM yyyy", Locale("uk")).format(Date(article.createdAt))
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = article.categoryName.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(6.dp))
            Text(text = article.title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(
                text = article.body.lineSequence().firstOrNull().orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = article.author,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "$date - ${article.commentCount} ком.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
