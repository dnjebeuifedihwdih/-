package com.example.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class LogCategory(val tagFa: String) {
    ALL("همه"),
    TUNNEL("تونل"),
    NETWORK("شبکه"),
    DNS("دی‌ان‌اس"),
    ERROR("خطاها"),
    SYSTEM("سیستم")
}

data class LogEntry(
    val timestamp: String,
    val tag: String,
    val category: LogCategory,
    val message: String
)

object AetherLogRepository {
    private const val MAX_LOGS = 300
    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs.asStateFlow()

    private val _logEntries = MutableStateFlow<List<LogEntry>>(emptyList())
    val logEntries: StateFlow<List<LogEntry>> = _logEntries.asStateFlow()

    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)

    init {
        logSystem("AETHER ADVANCED TERMINAL LOGGING INITIALIZED")
        logSystem("MASQUE HTTP/3 + HTTP/2 CORE READY")
        logSystem("VLESS REALITY & FRAGMENTATION MATRIX LOADED")
    }

    fun log(tag: String, message: String) {
        val category = when {
            tag.contains("ERR") || tag.contains("ERROR") || tag.contains("FAIL") -> LogCategory.ERROR
            tag.contains("DNS") || tag.contains("RESOLVE") -> LogCategory.DNS
            tag.contains("NET") || tag.contains("SOCKET") || tag.contains("PING") || tag.contains("IP") -> LogCategory.NETWORK
            tag.contains("VPN") || tag.contains("TUN") || tag.contains("AETHER") || tag.contains("VPS") -> LogCategory.TUNNEL
            else -> LogCategory.SYSTEM
        }
        val timestamp = timeFormat.format(Date())
        val formatted = "[$timestamp] [$tag] $message"

        val entry = LogEntry(timestamp, tag, category, message)

        val currentStr = _logs.value.toMutableList()
        currentStr.add(formatted)
        if (currentStr.size > MAX_LOGS) currentStr.removeAt(0)
        _logs.value = currentStr

        val currentEntries = _logEntries.value.toMutableList()
        currentEntries.add(entry)
        if (currentEntries.size > MAX_LOGS) currentEntries.removeAt(0)
        _logEntries.value = currentEntries
    }

    fun logTunnel(msg: String) = log("TUNNEL", msg)
    fun logNetwork(msg: String) = log("NET", msg)
    fun logDns(msg: String) = log("DNS", msg)
    fun logError(msg: String) = log("ERR", msg)
    fun logSystem(msg: String) = log("SYS", msg)

    fun clear() {
        _logs.value = listOf("[${timeFormat.format(Date())}] [SYS] Terminal log buffer cleared")
        _logEntries.value = emptyList()
    }
}
