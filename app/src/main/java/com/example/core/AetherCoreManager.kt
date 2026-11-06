package com.example.core

import android.content.Context
import com.example.model.AetherProtocol
import com.example.model.NoiseProfile
import com.example.model.RoutingMode
import com.example.model.ScanMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.Socket

class AetherCoreManager(private val context: Context) {
    private var process: Process? = null
    private var stdoutReaderJob: Job? = null
    private var stderrReaderJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    @Synchronized
    fun start(
        protocol: AetherProtocol,
        routingMode: RoutingMode,
        countryCode: String,
        noiseProfile: NoiseProfile,
        scanMode: ScanMode,
        bypassIr: Boolean = true
    ): Boolean {
        stop()

        AetherLogRepository.log("AETHER", "Preparing native environment & Pluggable Transports...")
        preparePluggableTransports()
        val binary = resolveAetherExecutable()

        if (binary == null || !binary.exists()) {
            AetherLogRepository.log("AETHER_ERR", "Could not locate executable libaether.so in nativeLibraryDir")
            return false
        }

        val cmd = mutableListOf<String>()
        cmd.add(binary.absolutePath)
        cmd.add("--bind")
        cmd.add("127.0.0.1:1819")
        cmd.add("--scan")
        cmd.add(scanMode.value)
        cmd.add("--noize")
        cmd.add(noiseProfile.value)
        cmd.add("--api-address")
        cmd.add("162.159.192.1")

        // Protocol flags
        cmd.addAll(protocol.flags)

        val psiphonBin = resolvePsiphonBinary()
        val lyrebirdBin = resolveLyrebirdBinary()

        // Routing & Pluggable Transport modes
        val psiphonDir = java.io.File(context.filesDir, "psiphon").apply { mkdirs() }

        when (routingMode) {
            RoutingMode.AETHER_WARP_DEFAULT -> {
                cmd.add("--warp")
            }
            RoutingMode.PSIPHON_INSIDE -> {
                cmd.add("--psiphon")
                cmd.add("--psiphon-region")
                cmd.add(countryCode.uppercase())
                cmd.add("--psiphon-dir")
                cmd.add(psiphonDir.absolutePath)
                if (psiphonBin != null && psiphonBin.exists()) {
                    cmd.add("--psiphon-bin")
                    cmd.add(psiphonBin.absolutePath)
                }
            }
            RoutingMode.PSIPHON_REVERSE -> {
                cmd.add("--psiphon-reverse")
                cmd.add("--psiphon-region")
                cmd.add(countryCode.uppercase())
                cmd.add("--psiphon-dir")
                cmd.add(psiphonDir.absolutePath)
                if (psiphonBin != null && psiphonBin.exists()) {
                    cmd.add("--psiphon-bin")
                    cmd.add(psiphonBin.absolutePath)
                }
            }
            RoutingMode.PSIPHON_ONLY_CDN -> {
                cmd.add("--psiphon-only")
                cmd.add("--psiphon-mode")
                cmd.add("cdn")
                cmd.add("--psiphon-region")
                cmd.add(countryCode.uppercase())
                cmd.add("--psiphon-dir")
                cmd.add(psiphonDir.absolutePath)
                if (psiphonBin != null && psiphonBin.exists()) {
                    cmd.add("--psiphon-bin")
                    cmd.add(psiphonBin.absolutePath)
                }
            }
            RoutingMode.TOR_INSIDE -> {
                cmd.add("--tor")
                if (lyrebirdBin != null && lyrebirdBin.exists()) {
                    cmd.add("--tor-pt")
                    cmd.add(lyrebirdBin.absolutePath)
                }
                cmd.add("--tor-pt-dir")
                cmd.add(context.applicationInfo.nativeLibraryDir)
            }
            RoutingMode.TOR_REVERSE -> {
                cmd.add("--tor-reverse")
                if (lyrebirdBin != null && lyrebirdBin.exists()) {
                    cmd.add("--tor-pt")
                    cmd.add(lyrebirdBin.absolutePath)
                }
                cmd.add("--tor-pt-dir")
                cmd.add(context.applicationInfo.nativeLibraryDir)
            }
        }

        if (bypassIr) {
            cmd.add("--route-direct")
            cmd.add("private,ir")
        }

        AetherLogRepository.log("AETHER", "Command: ${cmd.joinToString(" ")}")

        try {
            val pb = ProcessBuilder(cmd)
            pb.directory(context.filesDir)

            val env = pb.environment()
            env["HOME"] = context.filesDir.absolutePath
            env["TMPDIR"] = context.cacheDir.absolutePath
            env["LD_LIBRARY_PATH"] = context.applicationInfo.nativeLibraryDir

            val proc = pb.start()
            process = proc

            stdoutReaderJob = scope.launch {
                val reader = BufferedReader(InputStreamReader(proc.inputStream))
                try {
                    while (isActive) {
                        val line = reader.readLine() ?: break
                        AetherLogRepository.log("AETHER", line)
                    }
                } catch (e: Exception) {
                    // stream closed
                }
            }

            stderrReaderJob = scope.launch {
                val reader = BufferedReader(InputStreamReader(proc.errorStream))
                try {
                    while (isActive) {
                        val line = reader.readLine() ?: break
                        AetherLogRepository.log("AETHER", line)
                        if (line.contains("selected MASQUE gateway") || line.contains("best gateway") ||
                            line.contains("psiphon reached a server") || line.contains("transport: HTTP/2") ||
                            line.contains("gool ready") || line.contains("ironclad verified")
                        ) {
                            AetherLogRepository.log("TUNNEL_READY", ">>> GATEWAY CONNECTED • TRAFFIC FLOWING <<<")
                        } else if (line.contains("hunting for a working") || line.contains("candidates=")) {
                            AetherLogRepository.log("PROBER", "Scanning Cloudflare endpoints for unblocked gateway...")
                        } else if (line.contains("scan deadline reached") || line.contains("no usable MASQUE gateway")) {
                            AetherLogRepository.log("AETHER_TIP", "[TIP] UDP blocked by ISP. Switch to MASQUE H2 (TCP) or Psiphon Reverse.")
                        }
                    }
                } catch (e: Exception) {
                    // stream closed
                }
            }

            // Wait briefly for port 1819 to become available
            val ready = waitForSocksPort(1819, maxWaitMs = 6000)
            if (ready) {
                AetherLogRepository.log("AETHER", "SOCKS5 proxy successfully bound to 127.0.0.1:1819")
                return true
            } else {
                AetherLogRepository.log("AETHER_WARN", "Port 1819 not yet bound, checking process status...")
                return proc.isAlive
            }
        } catch (e: Exception) {
            AetherLogRepository.log("AETHER_ERR", "Failed to launch Aether: ${e.message}")
            return false
        }
    }

    private fun waitForSocksPort(port: Int, maxWaitMs: Long): Boolean {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < maxWaitMs) {
            if (process?.isAlive != true) return false
            try {
                Socket("127.0.0.1", port).use {
                    return true
                }
            } catch (ignored: Exception) {
                Thread.sleep(200)
            }
        }
        return false
    }

    @Synchronized
    fun stop() {
        stdoutReaderJob?.cancel()
        stdoutReaderJob = null
        stderrReaderJob?.cancel()
        stderrReaderJob = null

        process?.let { proc ->
            try {
                AetherLogRepository.log("AETHER", "Stopping Aether engine...")
                proc.destroy()
                Thread.sleep(100)
                if (proc.isAlive) {
                    proc.destroyForcibly()
                }
            } catch (e: Exception) {
                AetherLogRepository.log("AETHER_WARN", "Error stopping process: ${e.message}")
            }
        }
        process = null
    }

    fun isRunning(): Boolean = process?.isAlive == true

    private fun resolveAetherExecutable(): File? {
        val nativeLibDir = File(context.applicationInfo.nativeLibraryDir)
        val defaultSo = File(nativeLibDir, "libaether.so")
        if (defaultSo.exists()) {
            return defaultSo
        }
        return null
    }

    private fun resolvePsiphonBinary(): File? {
        val nativeLib = File(context.applicationInfo.nativeLibraryDir, "libpsiphon.so")
        if (nativeLib.exists()) {
            return nativeLib
        }
        val ptBin = File(context.filesDir, "pt/psiphon-tunnel-core")
        if (ptBin.exists()) return ptBin
        return null
    }

    private fun resolveLyrebirdBinary(): File? {
        val nativeLib = File(context.applicationInfo.nativeLibraryDir, "liblyrebird.so")
        if (nativeLib.exists()) {
            return nativeLib
        }
        val ptBin = File(context.filesDir, "pt/lyrebird")
        if (ptBin.exists()) return ptBin
        return null
    }

    private fun preparePluggableTransports(): File {
        return File(context.filesDir, "pt").apply { mkdirs() }
    }
}
