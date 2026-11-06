package com.example.ui

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.AetherLogRepository
import com.example.core.GithubVpsManager
import com.example.core.IpLocationRepository
import com.example.core.VibrationHelper
import com.example.model.AetherProtocol
import com.example.model.AppInfoItem
import com.example.model.Country
import com.example.model.NoiseProfile
import com.example.model.PublicIpInfo
import com.example.model.RoutingMode
import com.example.model.ScanMode
import com.example.model.SplitTunnelMode
import com.example.model.SupportedCountries
import com.example.model.TunnelMethod
import com.example.model.VpsServer
import com.example.model.VpnConnectionState
import com.example.model.VpnStatus
import com.example.service.AetherPaperVpnService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val vpsManager = GithubVpsManager()

    // Active Tab: Method 1 vs Method 2
    private val _selectedMethod = MutableStateFlow(TunnelMethod.AETHER_CORE)
    val selectedMethod: StateFlow<TunnelMethod> = _selectedMethod.asStateFlow()

    // Method 1 (Aether) state
    private val _aetherProtocol = MutableStateFlow(AetherProtocol.MASQUE_H2)
    val aetherProtocol: StateFlow<AetherProtocol> = _aetherProtocol.asStateFlow()

    private val _routingMode = MutableStateFlow(RoutingMode.AETHER_WARP_DEFAULT)
    val routingMode: StateFlow<RoutingMode> = _routingMode.asStateFlow()

    private val _selectedCountry = MutableStateFlow(SupportedCountries.list[0]) // Default Germany DE
    val selectedCountry: StateFlow<Country> = _selectedCountry.asStateFlow()

    private val _noiseProfile = MutableStateFlow(NoiseProfile.FIREWALL)
    val noiseProfile: StateFlow<NoiseProfile> = _noiseProfile.asStateFlow()

    private val _scanMode = MutableStateFlow(ScanMode.TURBO)
    val scanMode: StateFlow<ScanMode> = _scanMode.asStateFlow()

    private val _bypassIr = MutableStateFlow(true)
    val bypassIr: StateFlow<Boolean> = _bypassIr.asStateFlow()

    // Method 2 (GitHub VPS) state
    private val _vpsServers = MutableStateFlow<List<VpsServer>>(vpsManager.getBundledServers())
    val vpsServers: StateFlow<List<VpsServer>> = _vpsServers.asStateFlow()

    private val _selectedVpsServer = MutableStateFlow<VpsServer?>(vpsManager.getBundledServers().firstOrNull())
    val selectedVpsServer: StateFlow<VpsServer?> = _selectedVpsServer.asStateFlow()

    private val _vpsCountryFilter = MutableStateFlow("ALL")
    val vpsCountryFilter: StateFlow<String> = _vpsCountryFilter.asStateFlow()

    private val _coverageDomain = MutableStateFlow("shaparak.ir")
    val coverageDomain: StateFlow<String> = _coverageDomain.asStateFlow()

    private val _isRefreshingVps = MutableStateFlow(false)
    val isRefreshingVps: StateFlow<Boolean> = _isRefreshingVps.asStateFlow()

    // Live terminal logs from repository
    val logs: StateFlow<List<String>> = AetherLogRepository.logs

    // VPN Connection State from Service
    val vpnState: StateFlow<VpnConnectionState> = AetherPaperVpnService.vpnState

    // Live Outbound IP & Location
    private val _publicIpInfo = MutableStateFlow(PublicIpInfo(ip = "در انتظار اتصال...", country = "آماده", isp = "بدون تونل"))
    val publicIpInfo: StateFlow<PublicIpInfo> = _publicIpInfo.asStateFlow()

    // Preferences & Advanced Features
    private val _vibrationEnabled = MutableStateFlow(true)
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    private val _killSwitchEnabled = MutableStateFlow(false)
    val killSwitchEnabled: StateFlow<Boolean> = _killSwitchEnabled.asStateFlow()

    // Split Tunneling
    private val _splitTunnelMode = MutableStateFlow(SplitTunnelMode.ALL_APPS)
    val splitTunnelMode: StateFlow<SplitTunnelMode> = _splitTunnelMode.asStateFlow()

    private val _splitTunnelPackages = MutableStateFlow<Set<String>>(emptySet())
    val splitTunnelPackages: StateFlow<Set<String>> = _splitTunnelPackages.asStateFlow()

    private val _installedApps = MutableStateFlow<List<AppInfoItem>>(emptyList())
    val installedApps: StateFlow<List<AppInfoItem>> = _installedApps.asStateFlow()

    private val _isLoadingApps = MutableStateFlow(false)
    val isLoadingApps: StateFlow<Boolean> = _isLoadingApps.asStateFlow()

    init {
        // Fetch fresh VPS servers in background on startup
        refreshVpsServers()

        // Listen to VPN state: trigger IP fetch on connection
        viewModelScope.launch {
            vpnState.collect { state ->
                if (state.status == VpnStatus.CONNECTED) {
                    refreshPublicIp()
                } else if (state.status == VpnStatus.DISCONNECTED) {
                    _publicIpInfo.value = PublicIpInfo(
                        ip = "قطع ارتباط",
                        country = "آفلاین",
                        city = "",
                        isp = "اتصال عادی اینترنت",
                        isSecureDns = false,
                        isLoading = false
                    )
                }
            }
        }

        // Preload installed applications
        loadInstalledApps()
    }

    fun selectMethod(method: TunnelMethod) {
        _selectedMethod.value = method
    }

    fun selectAetherProtocol(protocol: AetherProtocol) {
        _aetherProtocol.value = protocol
    }

    fun selectRoutingMode(mode: RoutingMode) {
        _routingMode.value = mode
    }

    fun selectCountry(country: Country) {
        _selectedCountry.value = country
        val matchingVps = _vpsServers.value.firstOrNull { it.countryCode.equals(country.code, ignoreCase = true) }
        if (matchingVps != null) {
            _selectedVpsServer.value = matchingVps
        }
    }

    fun selectNoiseProfile(profile: NoiseProfile) {
        _noiseProfile.value = profile
    }

    fun selectScanMode(mode: ScanMode) {
        _scanMode.value = mode
    }

    fun toggleBypassIr() {
        _bypassIr.value = !_bypassIr.value
    }

    fun setCoverageDomain(domain: String) {
        _coverageDomain.value = domain
    }

    fun setVpsCountryFilter(country: String) {
        _vpsCountryFilter.value = country
    }

    fun selectVpsServer(server: VpsServer) {
        _selectedVpsServer.value = server
    }

    fun toggleVibration() {
        _vibrationEnabled.value = !_vibrationEnabled.value
        AetherPaperVpnService.activeVibration = _vibrationEnabled.value
    }

    fun toggleKillSwitch() {
        _killSwitchEnabled.value = !_killSwitchEnabled.value
        AetherPaperVpnService.activeKillSwitch = _killSwitchEnabled.value
    }

    fun setSplitTunnelMode(mode: SplitTunnelMode) {
        _splitTunnelMode.value = mode
        AetherPaperVpnService.activeSplitTunnelMode = mode
    }

    fun toggleSplitTunnelApp(packageName: String) {
        val current = _splitTunnelPackages.value.toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        _splitTunnelPackages.value = current
        AetherPaperVpnService.activeSplitTunnelPackages = current
    }

    fun applyIranianBanksPreset() {
        val preset = setOf(
            "ir.melli.bam",
            "ir.tejaratbank.mbank",
            "com.samanpr.blu",
            "com.bmi.mobile",
            "ir.resalat.mbank",
            "com.parsian.mobilebank",
            "ir.sepah.mbank",
            "com.saderat.bank",
            "ir.asanpardakht.app",
            "com.farsitel.bazaar",
            "cab.snapp.passenger",
            "ir.divar",
            "ir.tapsi.cab",
            "com.digikala",
            "ir.torob",
            "com.rubika"
        )
        _splitTunnelPackages.value = preset
        _splitTunnelMode.value = SplitTunnelMode.BYPASS_SELECTED
        AetherPaperVpnService.activeSplitTunnelPackages = preset
        AetherPaperVpnService.activeSplitTunnelMode = SplitTunnelMode.BYPASS_SELECTED
    }

    fun clearSplitTunnelSelection() {
        _splitTunnelPackages.value = emptySet()
        AetherPaperVpnService.activeSplitTunnelPackages = emptySet()
    }

    fun refreshPublicIp() {
        viewModelScope.launch {
            _publicIpInfo.value = _publicIpInfo.value.copy(isLoading = true)
            val info = IpLocationRepository.getPublicIpInfo()
            _publicIpInfo.value = info
            AetherLogRepository.log("IP_CHECK", "Current Public IP: ${info.ip} (${info.country} • ${info.isp})")
        }
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            _isLoadingApps.value = true
            withContext(Dispatchers.IO) {
                try {
                    val pm = getApplication<Application>().packageManager
                    val packages = pm.getInstalledApplications(0)
                    val list = packages.map { appInfo ->
                        val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                        val name = pm.getApplicationLabel(appInfo).toString()
                        val isIr = isLikelyIranianApp(appInfo.packageName, name)
                        AppInfoItem(
                            packageName = appInfo.packageName,
                            appName = name,
                            isSystemApp = isSystem,
                            isIranianOrBank = isIr
                        )
                    }.sortedWith(compareByDescending<AppInfoItem> { it.isIranianOrBank }
                        .thenBy { it.isSystemApp }
                        .thenBy { it.appName })

                    _installedApps.value = list
                } catch (e: Exception) {
                    AetherLogRepository.log("APPS_WARN", "Error loading apps: ${e.message}")
                } finally {
                    _isLoadingApps.value = false
                }
            }
        }
    }

    private fun isLikelyIranianApp(pkg: String, name: String): Boolean {
        val lowerPkg = pkg.lowercase()
        val lowerName = name.lowercase()
        val irKeywords = listOf(
            "bank", "bam", "blu", "melli", "sepah", "tejarat", "saderat", "parsian", "pasargad",
            "snapp", "divar", "tapsi", "torob", "digikala", "bazaar", "rubika", "eitaa", "soroush",
            "ir.", "shaparak", "asan", "ap", "mellat", "keshavarzi", "refah"
        )
        return irKeywords.any { lowerPkg.contains(it) || lowerName.contains(it) }
    }

    fun refreshVpsServers() {
        if (_isRefreshingVps.value) return
        viewModelScope.launch {
            _isRefreshingVps.value = true
            try {
                kotlinx.coroutines.withTimeoutOrNull(5000L) {
                    val fetched = vpsManager.fetchServers()
                    val tested = vpsManager.pingServersInParallel(fetched)
                    _vpsServers.value = tested
                    if (_selectedVpsServer.value == null || !tested.any { it.id == _selectedVpsServer.value?.id }) {
                        _selectedVpsServer.value = tested.firstOrNull { it.pingMs > 0 } ?: tested.firstOrNull()
                    }
                }
            } catch (e: Exception) {
                AetherLogRepository.log("VPS_ERR", "Refresh error: ${e.message}")
            } finally {
                _isRefreshingVps.value = false
            }
        }
    }

    fun autoSelectFastestVps() {
        val tested = _vpsServers.value.filter { it.pingMs > 0 }.sortedBy { it.pingMs }
        if (tested.isNotEmpty()) {
            _selectedVpsServer.value = tested.first()
            AetherLogRepository.log("VPS", "Auto-selected fastest node: ${tested.first().title} (${tested.first().pingMs} ms)")
        } else {
            refreshVpsServers()
        }
    }

    fun toggleVpn(context: Context) {
        if (_vibrationEnabled.value) {
            VibrationHelper.vibrateClick(context)
        }

        if (vpnState.value.status == VpnStatus.CONNECTED || vpnState.value.status == VpnStatus.CONNECTING) {
            AetherPaperVpnService.stopVpn(context)
        } else {
            AetherPaperVpnService.activeSplitTunnelMode = _splitTunnelMode.value
            AetherPaperVpnService.activeSplitTunnelPackages = _splitTunnelPackages.value
            AetherPaperVpnService.activeKillSwitch = _killSwitchEnabled.value
            AetherPaperVpnService.activeVibration = _vibrationEnabled.value

            when (_selectedMethod.value) {
                TunnelMethod.AETHER_CORE -> {
                    val params = AetherPaperVpnService.AetherParams(
                        protocol = _aetherProtocol.value,
                        routingMode = _routingMode.value,
                        countryCode = _selectedCountry.value.code,
                        noiseProfile = _noiseProfile.value,
                        scanMode = _scanMode.value,
                        bypassIr = _bypassIr.value
                    )
                    AetherPaperVpnService.startAether(context, params)
                }
                TunnelMethod.GITHUB_VPS -> {
                    val server = _selectedVpsServer.value
                    if (server != null) {
                        AetherPaperVpnService.activeVpsServer = server
                        AetherPaperVpnService.activeCoverageDomain = _coverageDomain.value
                        AetherPaperVpnService.activeBypassIr = _bypassIr.value
                        val intent = android.content.Intent(context, AetherPaperVpnService::class.java).apply {
                            action = AetherPaperVpnService.ACTION_START_VPS
                        }
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            context.startForegroundService(intent)
                        } else {
                            context.startService(intent)
                        }
                    } else {
                        AetherLogRepository.log("VPS_WARN", "No VPS server selected")
                    }
                }
            }
        }
    }

    fun testTunnelPing(context: Context) {
        if (_vibrationEnabled.value) {
            VibrationHelper.vibrateClick(context)
        }
        AetherPaperVpnService.pingTunnel(context)
    }

    fun clearLogs() {
        AetherLogRepository.clear()
    }
}
