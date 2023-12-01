package com.example.markscalculator.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp


@Composable
fun SettingsItem(
    icon: ImageVector?,
    title: Int,
    text: Int,
    onClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Log.i("onclick", onClick.toString())
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { if (onClick != null) onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))
        if(icon == null) Spacer(Modifier.width(24.dp))
        else Icon(icon, null, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(title),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(2.dp))
            Text(
                stringResource(text),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
        if (trailingContent != null) trailingContent()
    }
}

@Composable
fun CheckSettingsItem(
    icon: ImageVector,
    title: Int,
    text: Int,
    onClick: (() -> Unit)? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    SettingsItem(
        icon,
        title,
        text,
        {
            if (onClick == null) onCheckedChange(!checked)
            else onClick()
        }
    ) {
        Spacer(Modifier.width(8.dp))
        Switch(checked, onCheckedChange)
        Spacer(Modifier.width(16.dp))
    }
}

fun LazyListScope.settingsLabel(
    text: Int
) {
    item {
        Spacer(Modifier.height(16.dp))
        Text(stringResource(text), modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
    }
}


fun <T> LazyListScope.settingsRadioItems(
    list: List<T>,
    selected: Int,
    onSelectedChange: (Int) -> Unit,
    label: @Composable (T) -> Unit
) {
    itemsIndexed(list) { index, item ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp, 8.dp)
                .clickable { onSelectedChange(index) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected == index,
                onClick = { onSelectedChange(index) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            label(item)
        }
    }
}