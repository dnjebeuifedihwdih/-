package com.example

import android.app.Activity
import android.net.VpnService
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpnStatus
import com.example.ui.MainViewModel
import com.example.ui.components.InkStampPowerButton
import com.example.ui.components.IpLocationCard
import com.example.ui.components.LatencyTestBar
import com.example.ui.components.LiquidMeshBackground
import com.example.ui.components.LiveSpeedMeterCard
import com.example.ui.components.MechanicalTabSwitch
import com.example.ui.components.PaperCard
import com.example.ui.components.QuickCountryCard
import com.example.ui.dialogs.CountryPickerDialog
import com.example.ui.dialogs.LogsDialog
import com.example.ui.dialogs.RepoHubDialog
import com.example.ui.dialogs.SettingsDialog
import com.example.ui.dialogs.SplitTunnelDialog
import com.example.ui.theme.AetherPaperTheme
import com.example.ui.theme.GlassBgBase
import com.example.ui.theme.GlassSurfaceBase
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.LocalPaperColors
import com.example.ui.theme.PastelAmber
import com.example.ui.theme.PastelIceBlue
import com.example.ui.theme.PastelMint

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AetherPaperTheme {
                val context = LocalContext.current
                val vpnPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        viewModel.toggleVpn(context)
                    } else {
                        Toast.makeText(context, "مجوز VPN برای اتصال الزامی است", Toast.LENGTH_SHORT).show()
                    }
                }

                // Force Right-to-Left Layout Direction for Persian language
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    AetherPaperApp(
                        viewModel = viewModel,
                        onConnectClick = {
                            val prepareIntent = VpnService.prepare(context)
                            if (prepareIntent != null) {
                                vpnPermissionLauncher.launch(prepareIntent)
                            } else {
                                viewModel.toggleVpn(context)
                            }
                        },
                        onPingClick = {
                            viewModel.testTunnelPing(context)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AetherPaperApp(
    viewModel: MainViewModel,
    onConnectClick: () -> Unit,
    onPingClick: () -> Unit
) {
    val colors = LocalPaperColors.current
    val scrollState = rememberScrollState()

    // Dialog visibility states
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showCountryPickerDialog by remember { mutableStateOf(false) }
    var showLogsDialog by remember { mutableStateOf(false) }
    var showSplitTunnelDialog by remember { mutableStateOf(false) }
    var showRepoHubDialog by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }

    val selectedMethod by viewModel.selectedMethod.collectAsState()
    val aetherProtocol by viewModel.aetherProtocol.collectAsState()
    val routingMode by viewModel.routingMode.collectAsState()
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val noiseProfile by viewModel.noiseProfile.collectAsState()
    val scanMode by viewModel.scanMode.collectAsState()
    val bypassIr by viewModel.bypassIr.collectAsState()

    val vpsServers by viewModel.vpsServers.collectAsState()
    val selectedVpsServer by viewModel.selectedVpsServer.collectAsState()
    val vpsCountryFilter by viewModel.vpsCountryFilter.collectAsState()
    val coverageDomain by viewModel.coverageDomain.collectAsState()
    val isRefreshingVps by viewModel.isRefreshingVps.collectAsState()

    val logs by viewModel.logs.collectAsState()
    val vpnState by viewModel.vpnState.collectAsState()

    // New Features states
    val publicIpInfo by viewModel.publicIpInfo.collectAsState()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()
    val killSwitchEnabled by viewModel.killSwitchEnabled.collectAsState()
    val splitTunnelMode by viewModel.splitTunnelMode.collectAsState()
    val splitTunnelPackages by viewModel.splitTunnelPackages.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val isLoadingApps by viewModel.isLoadingApps.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassBgBase),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Ultra-Glossy Light Liquid Glass Mesh Background
            LiquidMeshBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 14.dp)
                    .padding(top = 10.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Top Specular Glass Header Bar
                PaperCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = GlassSurfaceBase,
                    borderColor = Color.White,
                    cornerRadius = 24.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(PastelIceBlue.copy(alpha = 0.15f))
                                    .border(1.dp, PastelIceBlue.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "سپر امنیت",
                                    tint = PastelIceBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "جیم وی پی ان",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    ),
                                    color = GlassTextPrimary
                                )
                                Text(
                                    text = "JIM VPN v2.35 • Light Liquid Glass",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = GlassTextSecondary
                                )
                            }
                        }

                        // 3-Dots Overflow Menu Button
                        Box {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xE0FFFFFF))
                                    .border(1.dp, Color.White, CircleShape)
                                    .clickable { showOverflowMenu = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "منوی اختیارات",
                                    tint = GlassTextPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Glass Dropdown Menu
                            DropdownMenu(
                                expanded = showOverflowMenu,
                                onDismissRequest = { showOverflowMenu = false },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0xF0FFFFFF))
                                    .border(1.dp, Color.White, RoundedCornerShape(18.dp))
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Download,
                                                contentDescription = null,
                                                tint = PastelMint,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "مخزن و مرکز دانلود (Repo Hub)",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = GlassTextPrimary
                                            )
                                        }
                                    },
                                    onClick = {
                                        showOverflowMenu = false
                                        showRepoHubDialog = true
                                    }
                                )

                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Apps,
                                                contentDescription = null,
                                                tint = if (splitTunnelPackages.isNotEmpty()) PastelMint else PastelIceBlue,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = if (splitTunnelPackages.isNotEmpty()) "تفکیک برنامه‌ها (فعال)" else "تفکیک برنامه‌ها (Split Tunnel)",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                                color = GlassTextPrimary
                                            )
                                        }
                                    },
                                    onClick = {
                                        showOverflowMenu = false
                                        showSplitTunnelDialog = true
                                    }
                                )

                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Description,
                                                contentDescription = null,
                                                tint = PastelIceBlue,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "گزارشات لاگ سیستم (Logs)",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                                color = GlassTextPrimary
                                            )
                                        }
                                    },
                                    onClick = {
                                        showOverflowMenu = false
                                        showLogsDialog = true
                                    }
                                )

                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = null,
                                                tint = PastelIceBlue,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "تنظیمات پیشرفته (Settings)",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                                color = GlassTextPrimary
                                            )
                                        }
                                    },
                                    onClick = {
                                        showOverflowMenu = false
                                        showSettingsDialog = true
                                    }
                                )
                            }
                        }
                    }
                }

                // 2. Repo & Releases Download Hero Card
                PaperCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showRepoHubDialog = true },
                    backgroundColor = GlassSurfaceBase,
                    borderColor = PastelMint.copy(alpha = 0.5f),
                    cornerRadius = 20.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PastelMint.copy(alpha = 0.18f))
                                    .border(1.dp, PastelMint.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "دانلود",
                                    tint = PastelMint,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "مخزن رسمی و مرکز دانلود",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        ),
                                        color = GlassTextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(PastelMint.copy(alpha = 0.2f))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "v2.35.0",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = PastelMint
                                        )
                                    }
                                }
                                Text(
                                    text = "dnjebeuifedihwdih/- • لینک مستقیم و آینه‌ها",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = GlassTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(PastelIceBlue)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "مرکز دانلود",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    }
                }

                // 2. Status Glass Pill Indicator
                val isConnected = vpnState.status == VpnStatus.CONNECTED
                val isConnecting = vpnState.status == VpnStatus.CONNECTING
                val statusDotColor = when {
                    isConnected -> PastelMint
                    isConnecting -> PastelAmber
                    else -> GlassTextMuted
                }

                PaperCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = GlassSurfaceBase,
                    borderColor = statusDotColor.copy(alpha = 0.4f),
                    cornerRadius = 20.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(statusDotColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "وضعیت شبکه:",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = GlassTextSecondary
                                )
                            )
                        }
                        Text(
                            text = vpnState.status.titleFa,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = statusDotColor
                            )
                        )
                    }
                }

                // 3. Method Switcher Glass Pill Slider (Aether Core vs GitHub VPS)
                MechanicalTabSwitch(
                    selectedMethod = selectedMethod,
                    onSelectMethod = { viewModel.selectMethod(it) }
                )

                // 4. TREND Gesture Slider: Apple iOS Slide-To-Tunnel
                InkStampPowerButton(
                    status = vpnState.status,
                    activeInfo = vpnState.activeInfo,
                    onToggle = onConnectClick
                )

                // 5. Real-time Live Glass Speed & Traffic Meters
                LiveSpeedMeterCard(vpnState = vpnState)

                // 6. Live Outbound Glass IP & Geo Location Card
                IpLocationCard(
                    ipInfo = publicIpInfo,
                    vpnStatus = vpnState.status,
                    onRefresh = { viewModel.refreshPublicIp() }
                )

                // 7. Quick Country & Egress Selector Glass Card
                QuickCountryCard(
                    selectedCountry = selectedCountry,
                    onOpenCountryPicker = { showCountryPickerDialog = true }
                )

                // 8. Glass Latency Ping Bar
                LatencyTestBar(
                    pingMs = vpnState.pingMs,
                    isPinging = vpnState.isPinging,
                    onTestPing = onPingClick
                )
            }
        }

        // --- Translucent Glass Modals & Dialogs ---
        if (showCountryPickerDialog) {
            CountryPickerDialog(
                selectedCountry = selectedCountry,
                onSelectCountry = { viewModel.selectCountry(it) },
                onDismiss = { showCountryPickerDialog = false }
            )
        }

        if (showSplitTunnelDialog) {
            SplitTunnelDialog(
                currentMode = splitTunnelMode,
                selectedPackages = splitTunnelPackages,
                installedApps = installedApps,
                isLoadingApps = isLoadingApps,
                onSelectMode = { viewModel.setSplitTunnelMode(it) },
                onTogglePackage = { viewModel.toggleSplitTunnelApp(it) },
                onApplyIranianPreset = { viewModel.applyIranianBanksPreset() },
                onClearSelection = { viewModel.clearSplitTunnelSelection() },
                onDismiss = { showSplitTunnelDialog = false }
            )
        }

        if (showSettingsDialog) {
            SettingsDialog(
                selectedMethod = selectedMethod,
                aetherProtocol = aetherProtocol,
                onSelectProtocol = { viewModel.selectAetherProtocol(it) },
                routingMode = routingMode,
                onSelectRoutingMode = { viewModel.selectRoutingMode(it) },
                selectedCountry = selectedCountry,
                onSelectCountry = { viewModel.selectCountry(it) },
                noiseProfile = noiseProfile,
                onSelectNoise = { viewModel.selectNoiseProfile(it) },
                scanMode = scanMode,
                onSelectScan = { viewModel.selectScanMode(it) },
                bypassIr = bypassIr,
                onToggleBypassIr = { viewModel.toggleBypassIr() },
                vpsServers = vpsServers,
                selectedVpsServer = selectedVpsServer,
                onSelectVpsServer = { viewModel.selectVpsServer(it) },
                vpsCountryFilter = vpsCountryFilter,
                onSelectVpsCountryFilter = { viewModel.setVpsCountryFilter(it) },
                coverageDomain = coverageDomain,
                onChangeCoverageDomain = { viewModel.setCoverageDomain(it) },
                isRefreshingVps = isRefreshingVps,
                onRefreshVps = { viewModel.refreshVpsServers() },
                onAutoSelectFastestVps = { viewModel.autoSelectFastestVps() },
                vibrationEnabled = vibrationEnabled,
                onToggleVibration = { viewModel.toggleVibration() },
                killSwitchEnabled = killSwitchEnabled,
                onToggleKillSwitch = { viewModel.toggleKillSwitch() },
                onOpenSplitTunnel = {
                    showSettingsDialog = false
                    showSplitTunnelDialog = true
                },
                onDismiss = { showSettingsDialog = false }
            )
        }

        if (showLogsDialog) {
            LogsDialog(
                logs = logs,
                onClearLogs = { viewModel.clearLogs() },
                onDismiss = { showLogsDialog = false }
            )
        }

        if (showRepoHubDialog) {
            RepoHubDialog(
                onDismiss = { showRepoHubDialog = false },
                onImportSubscription = { viewModel.refreshVpsServers() }
            )
        }
    }
}
