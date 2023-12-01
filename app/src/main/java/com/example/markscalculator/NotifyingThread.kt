package com.example.markscalculator

import androidx.compose.runtime.Composable
import java.util.concurrent.CopyOnWriteArrayList

abstract class NotifyingThread : Thread() {
    final var listeners = CopyOnWriteArrayList<ThreadCompletedListener>()
    final fun addListener(listener: ThreadCompletedListener) : Thread {
        listeners.add(listener)
        return this
    }
    final fun removeListener(listener: ThreadCompletedListener) {
        listeners.remove(listener)
    }
    @Composable
    private fun NotifyListeners() {
        for (listener in listeners) {
            listener.NotifyOfThreadComplete(this)
        }
    }
    @Composable
    fun Run() {
        try {
            doRun()
        } finally {
            NotifyListeners()
        }
        super.run()
    }
    abstract fun doRun()
}