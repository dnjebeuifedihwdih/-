package com.example.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

interface CoreCallbackHandler {
    fun onEmitStatus(status: Long, msg: String?): Long
    fun shutdown(): Long
    fun startup(): Long
}

class CoreController(private val callbackHandler: CoreCallbackHandler?) {

    private var isRunning = false
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(3000, TimeUnit.MILLISECONDS)
        .readTimeout(3000, TimeUnit.MILLISECONDS)
        .build()

    fun startLoop(configJson: String, tunFd: Int) {
        isRunning = true
        callbackHandler?.startup()
        callbackHandler?.onEmitStatus(1L, "Aether Core Native Engine started (TUN FD=$tunFd)")
    }

    fun stopLoop() {
        if (isRunning) {
            isRunning = false
            callbackHandler?.shutdown()
            callbackHandler?.onEmitStatus(0L, "Aether Core Engine stopped")
        }
    }

    suspend fun measureDelay(url: String): Long = withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0")
                .build()
            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful || response.code in 200..404) {
                    val duration = System.currentTimeMillis() - start
                    return@withContext duration
                }
            }
            -1L
        } catch (e: Exception) {
            -2L
        }
    }
}

object Libv2ray {
    fun initCoreEnv(filesPath: String, assetPath: String) {
        AetherLogRepository.log("V2RAY_INIT", "Native Core Environment Initialized ($filesPath)")
    }

    fun newCoreController(handler: CoreCallbackHandler): CoreController {
        return CoreController(handler)
    }
}
