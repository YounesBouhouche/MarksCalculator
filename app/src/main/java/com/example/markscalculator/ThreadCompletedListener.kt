package com.example.markscalculator

import androidx.compose.runtime.Composable

interface ThreadCompletedListener {
    @Composable
    fun NotifyOfThreadComplete(thread: Thread?)
}