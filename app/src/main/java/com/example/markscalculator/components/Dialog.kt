package com.example.markscalculator.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.markscalculator.R

@Composable
fun DialogButtons(
    cancelListener: (() -> Unit)? = null,
    cancelText: String? = null,
    okListener: (() -> Unit)? = null,
    neutral: (@Composable () -> Unit)? = null
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        val modifier = Modifier
            .weight(1f)
            .apply { if (neutral == null) fillMaxWidth() }
        val buttons = @Composable {
            if (cancelListener != null)
                OutlinedButton(
                    onClick = cancelListener,
                    modifier = modifier
                ) {
                    Text(cancelText ?: stringResource(R.string.cancel))
                }
            if ((okListener != null) and (cancelListener != null)) Spacer(Modifier.width(12.dp))
            if (okListener != null)
                Button(
                    onClick = okListener,
                    modifier = modifier
                ) {
                    Text("OK")
                }
        }
        if (neutral == null) buttons()
        else {
            neutral()
            Row {
                buttons()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    title: String?,
    centerTitle: Boolean = false,
    content: @Composable (() -> Unit)
) {
    if (visible)
        AlertDialog(
            onDismissRequest = onDismissRequest,
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.background,
                    RoundedCornerShape(24.dp)
                )
                .clipToBounds()
        ) {
            Surface(contentColor = MaterialTheme.colorScheme.onBackground, color = Color.Transparent) {
                Column(Modifier.fillMaxWidth()) {
                    Spacer(Modifier.height(24.dp))
                    title?.run {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            textAlign = if (centerTitle) TextAlign.Center else null
                        )
                        Spacer(Modifier.height(24.dp))
                    }
                    content()
                }
            }
        }
}