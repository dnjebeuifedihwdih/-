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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpsServer
import com.example.ui.components.PaperButton
import com.example.ui.components.PaperCard
import com.example.ui.components.PaperChip
import com.example.ui.components.PaperSectionHeader
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.PastelCoral
import com.example.ui.theme.PastelIceBlue
import com.example.ui.theme.PastelMint

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GithubVpsPanel(
    servers: List<VpsServer>,
    selectedServer: VpsServer?,
    onSelectServer: (VpsServer) -> Unit,
    countryFilter: String,
    onSelectCountryFilter: (String) -> Unit,
    coverageDomain: String,
    onChangeCoverageDomain: (String) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onAutoSelectFastest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredServers = if (countryFilter == "ALL") {
        servers
    } else {
        servers.filter { it.countryCode.equals(countryFilter, ignoreCase = true) }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Actions & Fast Select
        PaperCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                PaperSectionHeader(title = "سرورهای رایگان گیت‌هاب (VLESS REALITY)")
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaperButton(
                        onClick = onRefresh,
                        text = if (isRefreshing) "در حال دریافت..." else "بروزرسانی سرورها",
                        icon = {
                            if (isRefreshing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = GlassTextPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "بروزرسانی",
                                    tint = PastelIceBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        enabled = !isRefreshing,
                        modifier = Modifier.weight(1f),
                        testTag = "refresh_vps_btn"
                    )

                    PaperButton(
                        onClick = onAutoSelectFastest,
                        text = "انتخاب سریع‌ترین",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "سریع‌ترین",
                                tint = PastelMint,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "auto_fastest_btn"
                    )
                }
            }
        }

        // 2. Country Filter Chips
        val availableCountries = listOf("ALL", "US", "DE", "NL", "FI", "GB", "FR")
        PaperCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                PaperSectionHeader(title = "فیلتر بر اساس کشور")
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableCountries.forEach { code ->
                        val label = when (code) {
                            "ALL" -> "همه کشورها"
                            "US" -> "🇺🇸 آمریکا"
                            "DE" -> "🇩🇪 آلمان"
                            "NL" -> "🇳🇱 هلند"
                            "FI" -> "🇫🇮 فنلاند"
                            "GB" -> "🇬🇧 انگلیس"
                            "FR" -> "🇫🇷 فرانسه"
                            else -> code
                        }
                        PaperChip(
                            label = label,
                            isSelected = countryFilter == code,
                            onSelect = { onSelectCountryFilter(code) },
                            modifier = Modifier.testTag("filter_$code")
                        )
                    }
                }
            }
        }

        // 3. Server List
        PaperCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                PaperSectionHeader(title = "لیست سرورهای فعال (${filteredServers.size} سرور)")
                Spacer(modifier = Modifier.height(8.dp))

                if (filteredServers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "سروری یافت نشد. دکمه بروزرسانی سرورها را لمس کنید.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = GlassTextSecondary)
                        )
                    }
                } else {
                    filteredServers.forEach { server ->
                        val isSelected = selectedServer?.id == server.id
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
                                .clickable { onSelectServer(server) }
                                .padding(horizontal = 12.dp, vertical = 9.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = server.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        ),
                                        color = if (isSelected) PastelIceBlue else GlassTextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${server.protocol.uppercase()} Reality • ${server.sni}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                        color = GlassTextSecondary
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val pingText = when {
                                        server.isTesting -> "..."
                                        server.pingMs > 0 -> "${server.pingMs} ms"
                                        server.pingMs == -2L -> "ناموفق"
                                        else -> "تست نشده"
                                    }
                                    val pingColor = when {
                                        server.pingMs in 1..250 -> PastelMint
                                        server.pingMs > 250 -> PastelCoral
                                        else -> GlassTextSecondary
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(pingColor.copy(alpha = 0.15f))
                                            .border(1.dp, pingColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = pingText,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace
                                            ),
                                            color = pingColor
                                        )
                                    }

                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(6.dp))
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
            }
        }

        // 4. Fragment Evasion & Coverage Domain
        PaperCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                PaperSectionHeader(title = "تنظیمات ضد فیلترینگ و فرگمنت TLS")
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "دامنه پوشش داخلی (جهت عبور بدون اختلال)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GlassTextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xD0FFFFFF))
                        .border(1.dp, Color.White, RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    BasicTextField(
                        value = coverageDomain,
                        onValueChange = onChangeCoverageDomain,
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = GlassTextPrimary
                        ),
                        cursorBrush = SolidColor(PastelIceBlue),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "پکت‌های ClientHello فرگمنت شده (طول ۱۰-۳۰ با فاصله ۱۰-۲۰) فیلترینگ SNI را کاملاً بی‌اثر می‌کنند.",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = GlassTextSecondary
                    )
                )
            }
        }
    }
}
