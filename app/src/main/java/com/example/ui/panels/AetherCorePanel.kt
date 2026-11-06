package com.example.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AetherProtocol
import com.example.model.Country
import com.example.model.NoiseProfile
import com.example.model.RoutingMode
import com.example.model.ScanMode
import com.example.model.SupportedCountries
import com.example.ui.components.PaperCard
import com.example.ui.components.PaperChip
import com.example.ui.components.PaperSectionHeader
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.PastelIceBlue
import com.example.ui.theme.PastelMint

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AetherCorePanel(
    selectedProtocol: AetherProtocol,
    onSelectProtocol: (AetherProtocol) -> Unit,
    selectedRoutingMode: RoutingMode,
    onSelectRoutingMode: (RoutingMode) -> Unit,
    selectedCountry: Country,
    onSelectCountry: (Country) -> Unit,
    selectedNoise: NoiseProfile,
    onSelectNoise: (NoiseProfile) -> Unit,
    selectedScan: ScanMode,
    onSelectScan: (ScanMode) -> Unit,
    bypassIr: Boolean,
    onToggleBypassIr: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Tunnel Transport Protocol Chips
        PaperCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                PaperSectionHeader(title = "پروتکل اتصال و حمل‌ونقل تانل")
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AetherProtocol.values().forEach { proto ->
                        PaperChip(
                            label = proto.label,
                            isSelected = selectedProtocol == proto,
                            onSelect = { onSelectProtocol(proto) },
                            modifier = Modifier.testTag("proto_${proto.id}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = selectedProtocol.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 11.sp,
                        color = GlassTextSecondary
                    )
                )
            }
        }

        // 2. Routing & Pluggable Transport Modes
        PaperCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                PaperSectionHeader(title = "حالت‌های مسیریابی و پل‌های سایفون و تور")
                Spacer(modifier = Modifier.height(8.dp))

                RoutingMode.values().forEach { mode ->
                    val isSelected = selectedRoutingMode == mode
                    val shape = RoundedCornerShape(16.dp)
                    val bg = if (isSelected) {
                        Brush.horizontalGradient(listOf(PastelIceBlue.copy(alpha = 0.16f), PastelIceBlue.copy(alpha = 0.08f)))
                    } else {
                        Brush.horizontalGradient(listOf(Color(0xD0FFFFFF), Color(0xA0FFFFFF)))
                    }
                    val border = if (isSelected) {
                        Brush.horizontalGradient(listOf(PastelIceBlue, PastelIceBlue.copy(alpha = 0.4f)))
                    } else {
                        Brush.horizontalGradient(listOf(Color.White, Color(0x3094A3B8)))
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(shape)
                            .background(bg)
                            .border(1.dp, border, shape)
                            .clickable { onSelectRoutingMode(mode) }
                            .padding(horizontal = 12.dp, vertical = 9.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mode.label,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (isSelected) PastelIceBlue else GlassTextPrimary
                                )
                                Text(
                                    text = mode.description,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.5.sp),
                                    color = GlassTextSecondary
                                )
                            }
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(PastelIceBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "انتخاب شد",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Country Egress Selector Grid
        PaperCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                PaperSectionHeader(title = "موقعیت سرور خروجی [${selectedCountry.flagEmoji} ${selectedCountry.name}]")
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SupportedCountries.list.forEach { country ->
                        val isSelected = selectedCountry.code == country.code
                        val shape = RoundedCornerShape(12.dp)
                        val bg = if (isSelected) PastelIceBlue.copy(alpha = 0.18f) else Color(0xD0FFFFFF)
                        val border = if (isSelected) PastelIceBlue else Color.White

                        Box(
                            modifier = Modifier
                                .testTag("country_${country.code}")
                                .clip(shape)
                                .background(bg)
                                .border(1.dp, border, shape)
                                .clickable { onSelectCountry(country) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${country.flagEmoji} ${country.name}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) PastelIceBlue else GlassTextPrimary
                            )
                        }
                    }
                }
            }
        }

        // 4. Anti-DPI & Scanning Tuning
        PaperCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                PaperSectionHeader(title = "تنظیمات ضد فیلترینگ عمیق و اسکنر")
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "پروفایل تزریق نویز پکت (--noize)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GlassTextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NoiseProfile.values().forEach { profile ->
                        PaperChip(
                            label = profile.label,
                            isSelected = selectedNoise == profile,
                            onSelect = { onSelectNoise(profile) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "حالت اسکنر گیت‌وی کلودفلر (--scan)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GlassTextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ScanMode.values().forEach { scan ->
                        PaperChip(
                            label = scan.label,
                            isSelected = selectedScan == scan,
                            onSelect = { onSelectScan(scan) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                // Route direct .ir and banking
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xD0FFFFFF))
                        .border(1.dp, Color.White, RoundedCornerShape(14.dp))
                        .clickable { onToggleBypassIr() }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = bypassIr,
                        onCheckedChange = { onToggleBypassIr() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PastelMint,
                            checkmarkColor = Color.White,
                            uncheckedColor = GlassTextSecondary
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "عبور مستقیم سایت‌های ایرانی و بانکی (بای‌پس .ir)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = GlassTextPrimary
                        )
                        Text(
                            text = "سایت‌های داخلی، درگاه‌های شاپرک و اسنپ بدون فیلترشکن باز می‌شوند",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.5.sp,
                                color = GlassTextSecondary
                            )
                        )
                    }
                }
            }
        }
    }
}
