package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AetherProtocol
import com.example.model.Country
import com.example.model.NoiseProfile
import com.example.model.RoutingMode
import com.example.model.ScanMode
import com.example.model.TunnelMethod
import com.example.model.VpsServer
import com.example.ui.components.PaperButton
import com.example.ui.components.PaperCard
import com.example.ui.components.PaperSectionHeader
import com.example.ui.panels.AetherCorePanel
import com.example.ui.panels.GithubVpsPanel
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald

@Composable
fun SettingsDialog(
    selectedMethod: TunnelMethod,
    // Method 1
    aetherProtocol: AetherProtocol,
    onSelectProtocol: (AetherProtocol) -> Unit,
    routingMode: RoutingMode,
    onSelectRoutingMode: (RoutingMode) -> Unit,
    selectedCountry: Country,
    onSelectCountry: (Country) -> Unit,
    noiseProfile: NoiseProfile,
    onSelectNoise: (NoiseProfile) -> Unit,
    scanMode: ScanMode,
    onSelectScan: (ScanMode) -> Unit,
    bypassIr: Boolean,
    onToggleBypassIr: () -> Unit,
    // Method 2
    vpsServers: List<VpsServer>,
    selectedVpsServer: VpsServer?,
    onSelectVpsServer: (VpsServer) -> Unit,
    vpsCountryFilter: String,
    onSelectVpsCountryFilter: (String) -> Unit,
    coverageDomain: String,
    onChangeCoverageDomain: (String) -> Unit,
    isRefreshingVps: Boolean,
    onRefreshVps: () -> Unit,
    onAutoSelectFastestVps: () -> Unit,
    // New Feature Controls
    vibrationEnabled: Boolean,
    onToggleVibration: () -> Unit,
    killSwitchEnabled: Boolean,
    onToggleKillSwitch: () -> Unit,
    onOpenSplitTunnel: () -> Unit,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        PaperCard(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PaperSectionHeader(title = "تنظیمات پیشرفته جیم وی پی ان")
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x200F172A))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                            .clickable { onDismiss() }
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = GlassTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Body containing panels
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .height(500.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Feature 1: Split Tunneling & Quick Actions Card
                    PaperCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "ویژگی‌های اختصاصی و حریم خصوصی",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                ),
                                color = GlassTextPrimary
                            )

                            // Split Tunneling Entry Button
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0x200F172A))
                                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                                    .clickable { onOpenSplitTunnel() }
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Apps,
                                            contentDescription = "تفکیک برنامه‌ها",
                                            tint = NeonCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "تفکیک برنامه‌ها (Split Tunneling)",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 11.5.sp
                                                ),
                                                color = GlassTextPrimary
                                            )
                                            Text(
                                                text = "دور زدن همراه بانک، اسنپ، دیوار و سایت‌های داخلی",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = GlassTextSecondary
                                            )
                                        }
                                    }
                                    Text(
                                        text = "تنظیم ❯",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = NeonEmerald
                                        )
                                    )
                                }
                            }

                            // Vibration Toggle
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0x200F172A))
                                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                                    .clickable { onToggleVibration() }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Vibration,
                                            contentDescription = "ویبره",
                                            tint = NeonCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "ویبره هنگام اتصال (Haptic Feedback)",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                ),
                                                color = GlassTextPrimary
                                            )
                                            Text(
                                                text = "لرزش تأیید لمسی هنگام برقراری موفق تونل",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = GlassTextSecondary
                                            )
                                        }
                                    }
                                    Switch(
                                        checked = vibrationEnabled,
                                        onCheckedChange = { onToggleVibration() },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.Black,
                                            checkedTrackColor = NeonEmerald,
                                            uncheckedThumbColor = GlassTextSecondary,
                                            uncheckedTrackColor = Color(0x200F172A)
                                        )
                                    )
                                }
                            }

                            // Kill Switch Toggle
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0x200F172A))
                                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                                    .clickable { onToggleKillSwitch() }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = "قطع اضطراری",
                                            tint = if (killSwitchEnabled) NeonCoral else NeonCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "سوئیچ قطع اضطراری اینترنت (Kill Switch)",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                ),
                                                color = GlassTextPrimary
                                            )
                                            Text(
                                                text = "قطع خودکار ترافیک در صورت قطع موقت تونل ضد نشت",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = GlassTextSecondary
                                            )
                                        }
                                    }
                                    Switch(
                                        checked = killSwitchEnabled,
                                        onCheckedChange = { onToggleKillSwitch() },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.Black,
                                            checkedTrackColor = NeonEmerald,
                                            uncheckedThumbColor = GlassTextSecondary,
                                            uncheckedTrackColor = Color(0x200F172A)
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Panels
                    when (selectedMethod) {
                        TunnelMethod.AETHER_CORE -> {
                            AetherCorePanel(
                                selectedProtocol = aetherProtocol,
                                onSelectProtocol = onSelectProtocol,
                                selectedRoutingMode = routingMode,
                                onSelectRoutingMode = onSelectRoutingMode,
                                selectedCountry = selectedCountry,
                                onSelectCountry = onSelectCountry,
                                selectedNoise = noiseProfile,
                                onSelectNoise = onSelectNoise,
                                selectedScan = scanMode,
                                onSelectScan = onSelectScan,
                                bypassIr = bypassIr,
                                onToggleBypassIr = onToggleBypassIr
                            )
                        }
                        TunnelMethod.GITHUB_VPS -> {
                            GithubVpsPanel(
                                servers = vpsServers,
                                selectedServer = selectedVpsServer,
                                onSelectServer = onSelectVpsServer,
                                countryFilter = vpsCountryFilter,
                                onSelectCountryFilter = onSelectVpsCountryFilter,
                                coverageDomain = coverageDomain,
                                onChangeCoverageDomain = onChangeCoverageDomain,
                                isRefreshing = isRefreshingVps,
                                onRefresh = onRefreshVps,
                                onAutoSelectFastest = onAutoSelectFastestVps
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                PaperButton(
                    onClick = onDismiss,
                    text = "ذخیره و بازگشت به صفحه اصلی",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
