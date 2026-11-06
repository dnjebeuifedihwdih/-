package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PublicIpInfo
import com.example.model.VpnStatus
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.PastelIceBlue
import com.example.ui.theme.PastelMint

@Composable
fun IpLocationCard(
    ipInfo: PublicIpInfo,
    vpnStatus: VpnStatus,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    PaperCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Title + Refresh Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = "آی‌پی عمومی",
                        tint = PastelIceBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "آدرس آی‌پی فعلی و موقعیت خروجی",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        ),
                        color = GlassTextPrimary
                    )
                }

                // Refresh pill button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xD0FFFFFF))
                        .border(1.dp, Color.White, RoundedCornerShape(12.dp))
                        .clickable(enabled = !ipInfo.isLoading) { onRefresh() }
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "بروزرسانی",
                            tint = PastelIceBlue,
                            modifier = Modifier
                                .size(14.dp)
                                .rotate(if (ipInfo.isLoading) rotation else 0f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (ipInfo.isLoading) "بررسی..." else "استعلام",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            ),
                            color = PastelIceBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // IP Address Display Box with Copy Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xD0FFFFFF))
                    .border(1.dp, Color.White, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "PUBLIC IPV4 ADDRESS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.5.sp,
                                letterSpacing = 1.sp
                            ),
                            color = GlassTextSecondary
                        )
                        Text(
                            text = ipInfo.ip.ifBlank { "در انتظار اتصال..." },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 0.5.sp
                            ),
                            color = if (vpnStatus == VpnStatus.CONNECTED) PastelMint else GlassTextPrimary
                        )
                    }

                    // Copy IP button
                    if (ipInfo.ip.isNotBlank() && !ipInfo.ip.contains("خطا") && !ipInfo.ip.contains("انتظار")) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(PastelIceBlue.copy(alpha = 0.12f))
                                .border(1.dp, PastelIceBlue.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("IP Address", ipInfo.ip)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "آی‌پی ${ipInfo.ip} کپی شد", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "کپی آی‌پی",
                                    tint = PastelIceBlue,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "کپی",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = PastelIceBlue
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Geo Location & ISP Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Location Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xB0FFFFFF))
                        .border(1.dp, Color.White, RoundedCornerShape(14.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "موقعیت جغرافیایی:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 9.sp
                            ),
                            color = GlassTextSecondary
                        )
                        Text(
                            text = ipInfo.country.ifBlank { "نامشخص" } + if (ipInfo.city.isNotBlank()) " (${ipInfo.city})" else "",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = GlassTextPrimary,
                            maxLines = 1
                        )
                    }
                }

                // ISP Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xB0FFFFFF))
                        .border(1.dp, Color.White, RoundedCornerShape(14.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "سرویس‌دهنده (ISP):",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 9.sp
                            ),
                            color = GlassTextSecondary
                        )
                        Text(
                            text = ipInfo.isp.ifBlank { "شبکه بین‌الملل" },
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = GlassTextPrimary,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // DNS Leak & Privacy Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xB0FFFFFF))
                    .border(1.dp, Color.White, RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "امنیت دی‌ان‌اس",
                        tint = PastelMint,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "محافظت نشت DNS:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp
                        ),
                        color = GlassTextPrimary
                    )
                }
                Text(
                    text = if (vpnStatus == VpnStatus.CONNECTED) "فعال • DoH رمزنگاری شده" else "غیرفعال (آفلاین)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = if (vpnStatus == VpnStatus.CONNECTED) PastelMint else GlassTextSecondary
                    )
                )
            }
        }
    }
}
