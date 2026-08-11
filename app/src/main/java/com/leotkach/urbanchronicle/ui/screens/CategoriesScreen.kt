package com.leotkach.urbanchronicle.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leotkach.urbanchronicle.data.ChronicleRepository
import com.leotkach.urbanchronicle.ui.components.AppScaffold
import com.leotkach.urbanchronicle.ui.components.SectionLabel
import kotlinx.coroutines.launch

@Composable
fun CategoriesScreen(
    repository: ChronicleRepository,
    onBack: () -> Unit,
) {
    val categories by repository.observeCategories().collectAsStateWithLifecycle(emptyList())
    var newName by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<Long?>(null) }
    var editingName by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    AppScaffold(
        title = "Категорії",
        subtitle = "Система категорій для статей блогу",
        onBack = onBack,
    ) {
        SectionLabel("Додати")
        OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text("Назва категорії") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = {
                if (newName.isBlank()) return@Button
                scope.launch {
                    repository.addCategory(newName)
                    newName = ""
                }
            },
            enabled = newName.isNotBlank(),
        ) {
            Text("Створити")
        }

        Spacer(Modifier.height(24.dp))
        SectionLabel("Список")
        categories.forEach { cat ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (editingId == cat.id) {
                        OutlinedTextField(
                            value = editingName,
                            onValueChange = { editingName = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        repository.renameCategory(cat.id, editingName)
                                        editingId = null
                                    }
                                },
                            ) { Text("Зберегти") }
                            OutlinedButton(onClick = { editingId = null }) {
                                Text("Скасувати")
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(cat.name, style = MaterialTheme.typography.titleMedium)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        editingId = cat.id
                                        editingName = cat.name
                                    },
                                ) { Text("Змінити") }
                                OutlinedButton(
                                    onClick = {
                                        scope.launch { repository.deleteCategory(cat.id) }
                                    },
                                ) { Text("Видалити") }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}
