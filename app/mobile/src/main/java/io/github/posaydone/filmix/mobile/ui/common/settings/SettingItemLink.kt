package io.github.posaydone.filmix.mobile.ui.common.settings

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingItemLink(
    title: String,
    currentValue: String?,
    onClick: () -> Unit,
    description: String? = null,
    modifier: Modifier = Modifier,
) {
    SettingItem(
        title = title, description = description, trailingContent = {
            if (!currentValue.isNullOrBlank()) Text(
                text = currentValue,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                contentDescription = "Open settings",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )

        }, onClick = onClick, modifier = modifier
    )
}