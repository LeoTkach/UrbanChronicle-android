package com.leotkach.urbanchronicle.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leotkach.urbanchronicle.data.ChronicleRepository
import com.leotkach.urbanchronicle.ui.components.AppScaffold
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleEditScreen(
    articleId: Long?,
    repository: ChronicleRepository,
    onBack: () -> Unit,
    onSaved: (Long) -> Unit,
) {
    val categories by repository.observeCategories().collectAsStateWithLifecycle(emptyList())
    val articleFlow = remember(articleId) {
        if (articleId != null) repository.observeArticle(articleId) else flowOf(null)
    }
    val existing by articleFlow.collectAsStateWithLifecycle(null)

    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("Редакція") }
    var categoryId by remember { mutableStateOf<Long?>(null) }
    var menuOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(existing?.id, categories) {
        val article = existing
        if (article != null) {
            title = article.title
            body = article.body
            author = article.author
            categoryId = article.categoryId
        } else if (categoryId == null && categories.isNotEmpty()) {
            categoryId = categories.first().id
        }
    }

    val selectedName = categories.firstOrNull { it.id == categoryId }?.name ?: "Оберіть категорію"
    val isEdit = articleId != null

    AppScaffold(
        title = if (isEdit) "Редагувати статтю" else "Нова стаття",
        subtitle = "Рівень 2: CRUD статей із категоріями",
        onBack = onBack,
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Заголовок") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Автор") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(10.dp))

        ExposedDropdownMenuBox(
            expanded = menuOpen,
            onExpandedChange = { menuOpen = it },
        ) {
            OutlinedTextField(
                value = selectedName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Категорія") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuOpen) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = menuOpen,
                onDismissRequest = { menuOpen = false },
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat.name) },
                        onClick = {
                            categoryId = cat.id
                            menuOpen = false
                        },
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            label = { Text("Текст") },
            minLines = 8,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                val cat = categoryId ?: return@Button
                if (title.isBlank() || body.isBlank()) return@Button
                scope.launch {
                    val id = if (isEdit && articleId != null) {
                        repository.updateArticle(articleId, title, body, author, cat)
                        articleId
                    } else {
                        repository.createArticle(title, body, author, cat)
                    }
                    onSaved(id)
                }
            },
            enabled = title.isNotBlank() && body.isNotBlank() && categoryId != null,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (isEdit) "Зберегти" else "Опублікувати")
        }
    }
}
