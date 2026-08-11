package com.leotkach.urbanchronicle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leotkach.urbanchronicle.data.SessionUser

@Composable
fun AccountAvatar(
    name: String,
    modifier: Modifier = Modifier,
    sizeDp: Int = 40,
    onClick: (() -> Unit)? = null,
) {
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
        modifier = modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(bg)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
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

@Composable
fun GuestAvatar(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = "Акаунт",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun AccountMenu(
    session: SessionUser?,
    onLogin: () -> Unit,
    onLogout: () -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    Box {
        if (session != null) {
            AccountAvatar(name = session.name, onClick = { open = true })
        } else {
            GuestAvatar(onClick = { open = true })
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            if (session != null) {
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(session.name, fontWeight = FontWeight.SemiBold)
                            Text(
                                session.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                    onClick = { open = false },
                    enabled = false,
                )
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Вийти") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Logout, contentDescription = null)
                    },
                    onClick = {
                        open = false
                        onLogout()
                    },
                )
            } else {
                DropdownMenuItem(
                    text = {
                        Column {
                            Text("Гість", fontWeight = FontWeight.SemiBold)
                            Text(
                                "Лише перегляд стрічки",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                    onClick = { open = false },
                    enabled = false,
                )
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Увійти") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Login, contentDescription = null)
                    },
                    onClick = {
                        open = false
                        onLogin()
                    },
                )
            }
        }
    }
}

fun avatarColor(seed: String): Color {
    val palette = listOf(
        Color(0xFF2F5D50),
        Color(0xFF1D4E89),
        Color(0xFF7A3E2E),
        Color(0xFF4A5568),
        Color(0xFF5B4B8A),
        Color(0xFF0F766E),
    )
    val index = seed.fold(0) { acc, c -> acc * 31 + c.code }.mod(palette.size).let {
        if (it < 0) it + palette.size else it
    }
    return palette[index]
}
