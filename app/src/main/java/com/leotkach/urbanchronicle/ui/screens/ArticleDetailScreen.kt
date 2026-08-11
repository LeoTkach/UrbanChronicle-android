package com.leotkach.urbanchronicle.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leotkach.urbanchronicle.data.CommentEntity
import com.leotkach.urbanchronicle.data.ChronicleRepository
import com.leotkach.urbanchronicle.ui.components.AppScaffold
import com.leotkach.urbanchronicle.ui.components.SectionLabel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun ArticleDetailScreen(
    articleId: Long,
    repository: ChronicleRepository,
    canEdit: Boolean,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onLogin: () -> Unit,
    onDeleted: () -> Unit,
) {
    val article by repository.observeArticle(articleId).collectAsStateWithLifecycle(null)
    val comments by repository.observeComments(articleId).collectAsStateWithLifecycle(emptyList())
    val scope = rememberCoroutineScope()
    var commentAuthor by remember { mutableStateOf("") }
    var commentText by remember { mutableStateOf("") }
    var confirmDelete by remember { mutableStateOf(false) }

    val current = article
    if (current == null) {
        AppScaffold(title = "Стаття", onBack = onBack) {
            Text("Статтю не знайдено.")
        }
        return
    }

    val date = SimpleDateFormat("d MMMM yyyy, HH:mm", Locale("uk")).format(Date(current.createdAt))

    AppScaffold(
        title = current.title,
        subtitle = "${current.author} - ${current.categoryName} - $date",
        onBack = onBack,
    ) {
        Text(
            text = current.body,
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(Modifier.height(20.dp))
        if (canEdit) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onEdit) { Text("Редагувати") }
                OutlinedButton(onClick = { confirmDelete = true }) { Text("Видалити") }
            }
        } else {
            OutlinedButton(onClick = onLogin) { Text("Увійти, щоб редагувати") }
        }

        Spacer(Modifier.height(28.dp))
        SectionLabel("Коментарі (${comments.size})")
        if (comments.isEmpty()) {
            Text(
                text = "Поки немає коментарів - будьте першими.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
        } else {
            comments.forEach { comment ->
                CommentAccountRow(
                    comment = comment,
                    onDelete = {
                        scope.launch { repository.deleteComment(comment.id) }
                    },
                )
                Spacer(Modifier.height(14.dp))
            }
        }

        SectionLabel("Новий коментар")
        OutlinedTextField(
            value = commentAuthor,
            onValueChange = { commentAuthor = it },
            label = { Text("Автор") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            label = { Text("Текст") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {
                if (commentText.isBlank()) return@Button
                scope.launch {
                    repository.addComment(articleId, commentAuthor, commentText)
                    commentAuthor = ""
                    commentText = ""
                }
            },
            enabled = commentText.isNotBlank(),
        ) {
            Text("Додати коментар")
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Видалити статтю?") },
            text = { Text("Разом із нею зникнуть і коментарі.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmDelete = false
                        scope.launch {
                            repository.deleteArticle(articleId)
                            onDeleted()
                        }
                    },
                ) { Text("Видалити") }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("Скасувати") }
            },
        )
    }
}

@Composable
private fun CommentAccountRow(
    comment: CommentEntity,
    onDelete: () -> Unit,
) {
    val displayName = comment.author.substringBefore(" -").trim().ifEmpty { comment.author }
    val roleLine = comment.author.substringAfter(" - ", missingDelimiterValue = "").trim()
    val time = SimpleDateFormat("d MMM, HH:mm", Locale("uk")).format(Date(comment.createdAt))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        AccountAvatar(name = displayName)
        Spacer(Modifier.width(12.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.weight(1f),
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        if (roleLine.isNotEmpty()) {
                            Text(
                                text = roleLine,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(comment.text, style = MaterialTheme.typography.bodyLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDelete) { Text("Видалити") }
                }
            }
        }
    }
}

@Composable
private fun AccountAvatar(name: String) {
    val initials = remember(name) {
        name.split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
            .ifEmpty { "?" }
    }
    val bg = remember(name) { avatarColor(name) }
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun avatarColor(seed: String): Color {
    val palette = listOf(
        Color(0xFF2F5D50),
        Color(0xFF1D4E89),
        Color(0xFF7A3E2E),
        Color(0xFF4A5568),
        Color(0xFF5B4B8A),
        Color(0xFF0F766E),
    )
    val index = seed.fold(0) { acc, c -> acc * 31 + c.code } .mod(palette.size).let {
        if (it < 0) it + palette.size else it
    }
    return palette[index]
}
