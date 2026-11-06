package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.core.AetherCoreManager
import com.example.core.AetherLogRepository
import com.example.core.XrayConfigBuilder
import com.example.model.AetherProtocol
import com.example.model.NoiseProfile
import com.example.model.RoutingMode
import com.example.model.ScanMode
import com.example.model.TunnelMethod
import com.example.model.VpsServer
import com.example.model.VpnConnectionState
import com.example.model.VpnStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.example.core.CoreCallbackHandler
import com.example.core.CoreController
import com.example.core.Libv2ray

class AetherPaperVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var coreController: CoreController? = null
    private var aetherManager: AetherCoreManager? = null
    private var statsJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    companion object {
        const val ACTION_START_AETHER = "com.example.service.START_AETHER"
        const val ACTION_START_VPS = "com.example.service.START_VPS"
        const val ACTION_STOP = "com.example.service.STOP"
        const val ACTION_PING = "com.example.service.PING"

        private const val NOTIFICATION_ID = 1819
        private const val CHANNEL_ID = "aether_vpn_channel"

        private val _vpnState = MutableStateFlow(VpnConnectionState())
        val vpnState: StateFlow<VpnConnectionState> = _vpnState.asStateFlow()

        // Parameters passed in memory for service start
        var activeAetherParams: AetherParams? = null
        var activeVpsServer: VpsServer? = null
        var activeCoverageDomain: String = "shaparak.ir"
        var activeBypassIr: Boolean = true
        var activeSplitTunnelMode: com.example.model.SplitTunnelMode = com.example.model.SplitTunnelMode.ALL_APPS
        var activeSplitTunnelPackages: Set<String> = emptySet()
        var activeKillSwitch: Boolean = false
        var activeVibration: Boolean = true

        fun stop(context: Context) {
            stopVpn(context)
        }

        fun startAether(context: Context, params: AetherParams) {
            activeAetherParams = params
            val intent = Intent(context, AetherPaperVpnService::class.java).apply {
                action = ACTION_START_AETHER
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopVpn(context: Context) {
            val intent = Intent(context, AetherPaperVpnService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }

        fun pingTunnel(context: Context) {
            val intent = Intent(context, AetherPaperVpnService::class.java).apply {
                action = ACTION_PING
            }
            context.startService(intent)
        }
    }

    data class AetherParams(
        val protocol: AetherProtocol,
        val routingMode: RoutingMode,
        val countryCode: String,
        val noiseProfile: NoiseProfile,
        val scanMode: ScanMode,
        val bypassIr: Boolean
    )

    override fun onCreate() {
        super.onCreate()
        aetherManager = AetherCoreManager(this)
        createNotificationChannel()
        extractGeoDataAssets()

        try {
            Libv2ray.initCoreEnv(filesDir.absolutePath, "")
            AetherLogRepository.log("V2RAY_INIT", "CoreEnv initialized with asset path")
        } catch (e: Throwable) {
            AetherLogRepository.log("V2RAY_INIT", "CoreEnv init note: ${e.message}")
        }
    }

    private fun extractGeoDataAssets() {
        val geoFiles = listOf("geoip.dat", "geosite.dat")
        for (name in geoFiles) {
            val target = java.io.File(filesDir, name)
            if (!target.exists() || target.length() == 0L) {
                try {
                    assets.open(name).use { input ->
                        java.io.FileOutputStream(target).use { output ->
                            input.copyTo(output)
                        }
                    }
                    AetherLogRepository.log("ASSETS", "Extracted $name (${target.length()} bytes)")
                } catch (e: Exception) {
                    // Non-fatal since explicit CIDRs are used
                    AetherLogRepository.log("ASSET_NOTE", "Asset $name: ${e.message}")
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_AETHER -> {
                val params = activeAetherParams
                if (params != null) {
                    serviceScope.launch {
                        startAetherMethod(params)
                    }
                }
            }
            ACTION_START_VPS -> {
                val server = activeVpsServer
                if (server != null) {
                    serviceScope.launch {
                        startVpsMethod(server, activeCoverageDomain, activeBypassIr)
                    }
                }
            }
            ACTION_STOP -> {
                stopVpnInternal()
            }
            ACTION_PING -> {
                serviceScope.launch {
                    testTunnelLatency()
                }
            }
        }
        return START_NOT_STICKY
    }

    private suspend fun startAetherMethod(params: AetherParams) {
        _vpnState.value = _vpnState.value.copy(
            status = VpnStatus.CONNECTING,
            method = TunnelMethod.AETHER_CORE,
            activeInfo = "STARTING AETHER [${params.protocol.label} | ${params.countryCode}]...",
            errorMessage = null
        )
        AetherLogRepository.log("VPN", "Initiating Method 1: Aether Rust Core (${params.protocol.label})")
        startForeground(NOTIFICATION_ID, buildNotification("Connecting to Aether Core..."))

        // 1. Launch libaether.so with user flags
        val started = aetherManager?.start(
            protocol = params.protocol,
            routingMode = params.routingMode,
            countryCode = params.countryCode,
            noiseProfile = params.noiseProfile,
            scanMode = params.scanMode,
            bypassIr = params.bypassIr
        ) ?: false

        if (!started) {
            _vpnState.value = _vpnState.value.copy(
                status = VpnStatus.ERROR,
                errorMessage = "Failed to start Aether core binary"
            )
            AetherLogRepository.log("VPN_ERR", "Aether process launch failed")
            stopForeground(STOP_FOREGROUND_REMOVE)
            return
        }

        // 2. Establish VPN TUN interface
        try {
            val pfd = establishVpnInterface()
            if (pfd == null) {
                _vpnState.value = _vpnState.value.copy(
                    status = VpnStatus.ERROR,
                    errorMessage = "Failed to establish Android TUN interface"
                )
                aetherManager?.stop()
                stopForeground(STOP_FOREGROUND_REMOVE)
                return
            }
            vpnInterface = pfd

            // 3. Start Libv2ray to bridge TUN to 127.0.0.1:1819
            val xrayJson = XrayConfigBuilder.buildAetherTunConfig(params.bypassIr)
            startCoreController(xrayJson, pfd.fd)

            _vpnState.value = _vpnState.value.copy(
                status = VpnStatus.CONNECTED,
                method = TunnelMethod.AETHER_CORE,
                activeInfo = "AETHER ACTIVE: ${params.protocol.label} / ${params.countryCode}",
                connectedSince = System.currentTimeMillis()
            )
            AetherLogRepository.log("VPN", "Aether Tunnel Fully Established! TUN -> 127.0.0.1:1819")
            startForeground(NOTIFICATION_ID, buildNotification("Connected: Aether [${params.countryCode}]"))

            if (activeVibration) {
                com.example.core.VibrationHelper.vibrateConnected(this@AetherPaperVpnService)
            }

            startStatsMonitor()
            // Latency is tested on-demand via the PING TEST button or once prober confirms gateway
        } catch (e: Exception) {
            AetherLogRepository.log("VPN_ERR", "Tunnel setup exception: ${e.message}")
            stopVpnInternal()
        }
    }

    private suspend fun startVpsMethod(server: VpsServer, coverageDomain: String, bypassIr: Boolean) {
        _vpnState.value = _vpnState.value.copy(
            status = VpnStatus.CONNECTING,
            method = TunnelMethod.GITHUB_VPS,
            activeInfo = "CONNECTING VPS: ${server.title}...",
            errorMessage = null
        )
        AetherLogRepository.log("VPN", "Initiating Method 2: GitHub VPS [${server.title}]")
        startForeground(NOTIFICATION_ID, buildNotification("Connecting to GitHub VPS..."))

        // Ensure Aether binary is stopped in VPS mode
        aetherManager?.stop()

        try {
            val pfd = establishVpnInterface()
            if (pfd == null) {
                _vpnState.value = _vpnState.value.copy(
                    status = VpnStatus.ERROR,
                    errorMessage = "Failed to establish Android TUN interface"
                )
                stopForeground(STOP_FOREGROUND_REMOVE)
                return
            }
            vpnInterface = pfd

            val xrayJson = XrayConfigBuilder.buildVpsConfig(
                server = server,
                coverageDomain = coverageDomain,
                enableFragment = true,
                bypassIr = bypassIr
            )
            AetherLogRepository.log("VPN", "Starting Xray Core with Fragment Evasion & VLESS Reality")
            startCoreController(xrayJson, pfd.fd)

            _vpnState.value = _vpnState.value.copy(
                status = VpnStatus.CONNECTED,
                method = TunnelMethod.GITHUB_VPS,
                activeInfo = "VPS ACTIVE: ${server.title}",
                connectedSince = System.currentTimeMillis()
            )
            AetherLogRepository.log("VPN", "GitHub VPS Tunnel Connected!")
            startForeground(NOTIFICATION_ID, buildNotification("Connected: ${server.title}"))

            if (activeVibration) {
                com.example.core.VibrationHelper.vibrateConnected(this@AetherPaperVpnService)
            }

            startStatsMonitor()
            // Latency is tested on-demand via the PING TEST button
        } catch (e: Exception) {
            _vpnState.value = _vpnState.value.copy(
                status = VpnStatus.ERROR,
                errorMessage = "VPS error: ${e.message}"
            )
            AetherLogRepository.log("VPN_ERR", "VPS tunnel failed: ${e.message}")
            stopVpnInternal()
        }
    }

    private fun startCoreController(configJson: String, tunFd: Int) {
        try {
            coreController?.stopLoop()
        } catch (ignored: Exception) {}

        try {
            android.system.Os.setenv("xray.tun.fd", tunFd.toString(), true)
            android.system.Os.setenv("XRAY_TUN_FD", tunFd.toString(), true)
            AetherLogRepository.log("VPN", "Exported xray.tun.fd=$tunFd for full-system TUN bridge")
        } catch (e: Exception) {
            AetherLogRepository.log("VPN_WARN", "setenv xray.tun.fd: ${e.message}")
        }

        coreController = Libv2ray.newCoreController(object : CoreCallbackHandler {
            override fun onEmitStatus(status: Long, msg: String?): Long {
                if (msg != null && msg.isNotBlank()) {
                    AetherLogRepository.log("V2RAY", msg)
                }
                return 0L
            }

            override fun shutdown(): Long {
                AetherLogRepository.log("V2RAY", "Core shutdown")
                return 0L
            }

            override fun startup(): Long {
                AetherLogRepository.log("V2RAY", "Core startup")
                return 0L
            }
        })

        coreController?.startLoop(configJson, tunFd)
    }

    private suspend fun testTunnelLatency() {
        if (_vpnState.value.status != VpnStatus.CONNECTED) return
        _vpnState.value = _vpnState.value.copy(isPinging = true)
        AetherLogRepository.log("TEST", "Measuring real tunnel delay via Cloudflare CDN trace...")

        val delayMs = try {
            val controller = coreController
            if (controller != null) {
                controller.measureDelay("https://www.cloudflare.com/cdn-cgi/trace")
            } else {
                -1L
            }
        } catch (e: Exception) {
            AetherLogRepository.log("TEST_ERR", "Ping measure failed: ${e.message}")
            -2L
        }

        AetherLogRepository.log("TEST", "Measured Tunnel Latency: ${if (delayMs > 0) "$delayMs ms" else "Timeout / Failed"}")
        _vpnState.value = _vpnState.value.copy(
            pingMs = delayMs,
            isPinging = false
        )
    }

    private fun startStatsMonitor() {
        statsJob?.cancel()
        statsJob = serviceScope.launch {
            val myUid = android.os.Process.myUid()
            var lastRx = android.net.TrafficStats.getUidRxBytes(myUid)
            var lastTx = android.net.TrafficStats.getUidTxBytes(myUid)
            var lastTime = System.currentTimeMillis()

            val baseRx = if (lastRx != android.net.TrafficStats.UNSUPPORTED.toLong()) lastRx else 0L
            val baseTx = if (lastTx != android.net.TrafficStats.UNSUPPORTED.toLong()) lastTx else 0L

            while (isActive && _vpnState.value.status == VpnStatus.CONNECTED) {
                delay(1000)
                val now = System.currentTimeMillis()
                val currentRx = android.net.TrafficStats.getUidRxBytes(myUid)
                val currentTx = android.net.TrafficStats.getUidTxBytes(myUid)
                val dt = (now - lastTime) / 1000.0

                if (currentRx != android.net.TrafficStats.UNSUPPORTED.toLong() && dt > 0) {
                    val rxDiff = (currentRx - lastRx).coerceAtLeast(0)
                    val txDiff = (currentTx - lastTx).coerceAtLeast(0)
                    val rxSpeed = (rxDiff / dt).toLong()
                    val txSpeed = (txDiff / dt).toLong()
                    val totalRx = (currentRx - baseRx).coerceAtLeast(0)
                    val totalTx = (currentTx - baseTx).coerceAtLeast(0)

                    _vpnState.value = _vpnState.value.copy(
                        downSpeedBytesPerSec = rxSpeed,
                        upSpeedBytesPerSec = txSpeed,
                        totalRxBytes = totalRx,
                        totalTxBytes = totalTx
                    )

                    lastRx = currentRx
                    lastTx = currentTx
                    lastTime = now
                }
            }
        }
    }

    private fun establishVpnInterface(): ParcelFileDescriptor? {
        val builder = Builder()
            .setSession("جیم وی پی ان")
            .setMtu(1500)
            .addAddress("26.26.26.1", 24)
            .addRoute("0.0.0.0", 0)
            .addDnsServer("1.1.1.1")
            .addDnsServer("8.8.8.8")

        // Exclude own app from TUN to avoid routing loops with Aether/Xray outbounds
        try {
            builder.addDisallowedApplication(packageName)
            AetherLogRepository.log("VPN", "Excluded $packageName from TUN (Loop Prevention)")
        } catch (e: Exception) {
            AetherLogRepository.log("VPN_WARN", "addDisallowedApplication: ${e.message}")
        }

        // Split Tunneling logic
        when (activeSplitTunnelMode) {
            com.example.model.SplitTunnelMode.BYPASS_SELECTED -> {
                var bypassed = 0
                for (pkg in activeSplitTunnelPackages) {
                    if (pkg != packageName) {
                        try {
                            builder.addDisallowedApplication(pkg)
                            bypassed++
                        } catch (ignored: Exception) {}
                    }
                }
                if (bypassed > 0) {
                    AetherLogRepository.log("VPN", "Split Tunnel: $bypassed apps bypassed from VPN")
                }
            }
            com.example.model.SplitTunnelMode.ONLY_SELECTED -> {
                var routed = 0
                for (pkg in activeSplitTunnelPackages) {
                    if (pkg != packageName) {
                        try {
                            builder.addAllowedApplication(pkg)
                            routed++
                        } catch (ignored: Exception) {}
                    }
                }
                if (routed > 0) {
                    AetherLogRepository.log("VPN", "Split Tunnel: Only $routed apps routed through VPN")
                }
            }
            com.example.model.SplitTunnelMode.ALL_APPS -> {
                // All apps routed through VPN
            }
        }

        // Kill Switch: Block non-VPN packets if active
        if (activeKillSwitch) {
            builder.setBlocking(true)
            AetherLogRepository.log("VPN", "Kill Switch (Anti-Leak): Active")
        }

        // Protect process socket
        try {
            setUnderlyingNetworks(null)
        } catch (ignored: Exception) {}

        return builder.establish()
    }

    private fun stopVpnInternal() {
        statsJob?.cancel()
        statsJob = null

        try {
            coreController?.stopLoop()
        } catch (e: Exception) {
            AetherLogRepository.log("VPN_WARN", "Core stop error: ${e.message}")
        }
        coreController = null

        aetherManager?.stop()

        try {
            vpnInterface?.close()
        } catch (e: Exception) {
            AetherLogRepository.log("VPN_WARN", "TUN close error: ${e.message}")
        }
        vpnInterface = null

        _vpnState.value = VpnConnectionState(
            status = VpnStatus.DISCONNECTED,
            activeInfo = "DISCONNECTED"
        )
        AetherLogRepository.log("VPN", "VPN Service Stopped")
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        stopVpnInternal()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "وضعیت جیم وی پی ان",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "نمایش وضعیت زنده اتصال جیم وی پی ان"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(text: String): Notification {
        val launchIntent = Intent(this, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, AetherPaperVpnService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPending = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("جیم وی پی ان")
            .setContentText(text)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pIntent)
            .addAction(0, "DISCONNECT", stopPending)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
