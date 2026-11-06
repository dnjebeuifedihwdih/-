package com.example.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AetherLogRepository {
    private const val MAX_LOGS = 250
    private val _logs = MutableStateFlow<List<String>>(
        listOf(
            "[INIT] AETHER PAPER TERMINAL READY",
            "[SYSTEM] MASQUE HTTP/3 + HTTP/2 ENGINE INITIALIZED",
            "[SYSTEM] V2RAY CORE ENGINE READY"
        )
    )
    val logs: StateFlow<List<String>> = _logs.asStateFlow()

    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)

    fun log(tag: String, message: String) {
        val timestamp = timeFormat.format(Date())
        val entry = "[$timestamp] [$tag] $message"
        val current = _logs.value.toMutableList()
        current.add(entry)
        if (current.size > MAX_LOGS) {
            current.removeAt(0)
        }
        _logs.value = current
    }

    fun clear() {
        _logs.value = listOf("[CLEARED] TERMINAL BUFFER RESET")
    }
}
