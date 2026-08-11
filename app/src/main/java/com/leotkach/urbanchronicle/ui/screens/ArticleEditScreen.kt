package com.leotkach.urbanchronicle.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leotkach.urbanchronicle.data.ChronicleRepository
import com.leotkach.urbanchronicle.ui.components.AppScaffold
import com.leotkach.urbanchronicle.ui.components.FormStepper
import com.leotkach.urbanchronicle.ui.components.SectionLabel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

private val EditSteps = listOf("Мета", "Розділ", "Текст", "Огляд")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleEditScreen(
    articleId: Long?,
    repository: ChronicleRepository,
    defaultAuthor: String = "Редакція",
    onBack: () -> Unit,
    onSaved: (Long) -> Unit,
) {
    val categories by repository.observeCategories().collectAsStateWithLifecycle(emptyList())
    val articleFlow = remember(articleId) {
        if (articleId != null) repository.observeArticle(articleId) else flowOf(null)
    }
    val existing by articleFlow.collectAsStateWithLifecycle(null)

    var step by remember { mutableIntStateOf(0) }
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var author by remember { mutableStateOf(defaultAuthor) }
    var categoryId by remember { mutableStateOf<Long?>(null) }
    var menuOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(existing?.id, categories, defaultAuthor) {
        val article = existing
        if (article != null) {
            title = article.title
            body = article.body
            author = article.author
            categoryId = article.categoryId
        } else {
            if (author.isBlank()) author = defaultAuthor
            if (categoryId == null && categories.isNotEmpty()) {
                categoryId = categories.first().id
            }
        }
    }

    val selectedName = categories.firstOrNull { it.id == categoryId }?.name ?: "Оберіть категорію"
    val isEdit = articleId != null
    val step0Ok = title.isNotBlank() && author.isNotBlank()
    val step1Ok = categoryId != null
    val step2Ok = body.isNotBlank()
    val canPublish = step0Ok && step1Ok && step2Ok

    AppScaffold(
        title = if (isEdit) "Редагувати статтю" else "Нова стаття",
        subtitle = "Індикатор кроків: заповнили - галочка, далі наступний",
        onBack = onBack,
    ) {
        FormStepper(steps = EditSteps, currentStep = step)
        Spacer(Modifier.height(22.dp))

        when (step) {
            0 -> {
                SectionLabel("Крок 1 - заголовок і автор")
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
            }
            1 -> {
                SectionLabel("Крок 2 - категорія")
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
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Категорія потрапить у фільтр на стрічці й у картку статті.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            2 -> {
                SectionLabel("Крок 3 - текст запису")
                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text("Текст") },
                    minLines = 10,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            else -> {
                SectionLabel("Крок 4 - перевірка перед публікацією")
                Text(title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "$author - $selectedName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
                Text(body, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (step > 0) {
                OutlinedButton(
                    onClick = { step -= 1 },
                    modifier = Modifier.weight(1f),
                ) { Text("Назад") }
            }
            if (step < EditSteps.lastIndex) {
                Button(
                    onClick = { step += 1 },
                    enabled = when (step) {
                        0 -> step0Ok
                        1 -> step1Ok
                        2 -> step2Ok
                        else -> true
                    },
                    modifier = Modifier.weight(1f),
                ) { Text("Далі") }
            } else {
                Button(
                    onClick = {
                        val cat = categoryId ?: return@Button
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
                    enabled = canPublish,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(if (isEdit) "Зберегти" else "Опублікувати")
                }
            }
        }
    }
}
