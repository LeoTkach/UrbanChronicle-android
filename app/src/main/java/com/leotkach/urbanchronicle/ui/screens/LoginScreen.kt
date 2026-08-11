package com.leotkach.urbanchronicle.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.leotkach.urbanchronicle.data.ChronicleRepository
import com.leotkach.urbanchronicle.ui.components.AppScaffold
import com.leotkach.urbanchronicle.ui.components.SectionLabel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    repository: ChronicleRepository,
    onBack: () -> Unit,
    onLoggedIn: () -> Unit,
) {
    var email by remember { mutableStateOf("leo@urban.local") }
    var password by remember { mutableStateOf("chronicle123") }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    AppScaffold(
        title = "Вхід",
        subtitle = "Рівень 3: авторизація редакції",
        onBack = onBack,
    ) {
        SectionLabel("Демо-акаунт")
        Text(
            text = "leo@urban.local / chronicle123 (Редакція)\n" +
                "arhiv@urban.local / chronicle123 (Архів)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; error = null },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; error = null },
            label = { Text("Пароль") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
        )
        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(error!!, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                scope.launch {
                    repository.login(email, password)
                        .onSuccess { onLoggedIn() }
                        .onFailure { error = it.message ?: "Помилка входу" }
                }
            },
            enabled = email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Увійти")
        }
    }
}
