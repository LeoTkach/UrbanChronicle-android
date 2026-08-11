package com.leotkach.urbanchronicle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Step indicator (stepper): filled check for done steps, accent for current, outline for next.
 */
@Composable
fun FormStepper(
    steps: List<String>,
    currentStep: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            steps.forEachIndexed { index, _ ->
                val done = index < currentStep
                val active = index == currentStep
                StepCircle(number = index + 1, done = done, active = active)
                if (index < steps.lastIndex) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(
                                if (done) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline,
                            ),
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Крок ${currentStep + 1} з ${steps.size}: ${steps[currentStep]}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StepCircle(number: Int, done: Boolean, active: Boolean) {
    val bg = when {
        done || active -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val content = when {
        done || active -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        if (done) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(18.dp),
            )
        } else {
            Text(
                text = number.toString(),
                color = content,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
