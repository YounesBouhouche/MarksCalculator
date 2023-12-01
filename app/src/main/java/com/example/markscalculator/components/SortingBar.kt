package com.example.markscalculator.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Grid4x4
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun SortingBar(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    descending: Boolean,
    descendingToggleListener: () -> Unit,
    items: List<@Composable () -> Unit>,
    text: String,
    trailingContent: @Composable (RowScope.() -> Unit) = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(
            onClick = { onExpandedChange(true) },
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(
                if (descending) Icons.Default.ArrowDownward
                else Icons.Default.ArrowUpward,
                null
            )
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            Text(text)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            DropdownMenuItem(
                text = { Text(
                    if (descending) "Descending"
                    else "Ascending"
                )
                },
                leadingIcon = {
                    Icon(
                        if (descending) Icons.Default.ArrowDownward
                        else Icons.Default.ArrowUpward,
                        null
                    )
                },
                onClick = {
                    descendingToggleListener()
                    onExpandedChange(false)
                }
            )
            HorizontalDivider(
                Modifier.padding(horizontal = 4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            items.forEach { it() }
        }
        Row(content = trailingContent)
    }
}

@Composable
fun SortingAndGridBar(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    descending: Boolean,
    descendingToggleListener: () -> Unit,
    items: List<@Composable () -> Unit>,
    text: String,
    gridSpan: Int,
    gridChangeClickListener: () -> Unit,
    trailingContent: (@Composable (RowScope.() -> Unit))? = null
) {
    SortingBar(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        descending = descending,
        descendingToggleListener = descendingToggleListener,
        items = items,
        text = text
    ) {
        if(trailingContent != null) Row(content = trailingContent)
        IconButton(
            onClick = gridChangeClickListener,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(
                when (gridSpan) {
                    2 -> Icons.Default.GridView
                    3 -> Icons.Default.ViewModule
                    4 -> Icons.Default.Grid4x4
                    else -> Icons.AutoMirrored.Filled.ViewList
                },
                null
            )
        }
        Spacer(Modifier.width(6.dp))
    }
}