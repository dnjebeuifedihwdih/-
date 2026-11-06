package com.example.ui.dialogs

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.components.PaperCard
import com.example.ui.components.PaperSectionHeader
import com.example.ui.theme.GlassSurfaceBase
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.PastelAmber
import com.example.ui.theme.PastelIceBlue
import com.example.ui.theme.PastelMint

@Composable
fun RepoHubDialog(
    onDismiss: () -> Unit,
    onImportSubscription: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    val repoUrl = "https://github.com/dnjebeuifedihwdih/-"
    val subUrl = "https://raw.githubusercontent.com/dnjebeuifedihwdih/-/main/sub.txt"
    val releasesPageUrl = "https://github.com/dnjebeuifedihwdih/-/releases"
    val directApkUrl = "https://github.com/dnjebeuifedihwdih/-/releases/tag/v2.35.0"
    val cdnMirrorUrl = "https://cdn.jsdelivr.net/gh/dnjebeuifedihwdih/-@main/sub.txt"
    val arm64ApkUrl = "https://github.com/dnjebeuifedihwdih/-"

    var showQrDialog by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFAFFFFFF),
                            Color(0xF0F1F5F9)
                        )
                    )
                )
                .border(1.5.dp, Color.White, RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Header with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(PastelIceBlue.copy(alpha = 0.2f))
                                .border(1.dp, PastelIceBlue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DownloadForOffline,
                                contentDescription = "مخزن و مرکز دانلود",
                                tint = PastelIceBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "مخزن و مرکز دانلود نسخه جدید",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = GlassTextPrimary
                            )
                            Text(
                                text = "GitHub Repository & Release Hub",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = GlassTextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x20000000))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = GlassTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // 2. Connected Repository Identity Card
                PaperCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = GlassSurfaceBase,
                    borderColor = PastelMint.copy(alpha = 0.6f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "مخزن متصل",
                                    tint = PastelMint,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "مخزن رسمی متصل",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    color = GlassTextPrimary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PastelMint.copy(alpha = 0.18f))
                                    .border(1.dp, PastelMint.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "فعال و همگام",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = PastelMint
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = repoUrl,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            ),
                            color = PastelIceBlue,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(repoUrl))
                                    Toast.makeText(context, "لینک مخزن گیت‌هاب کپی شد", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("copy_repo_url_btn"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "کپی",
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "کپی آدرس", fontSize = 11.5.sp)
                            }

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repoUrl))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("open_repo_browser_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PastelIceBlue)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = "باز کردن",
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "دیدن در گیت‌هاب", fontSize = 11.5.sp, color = Color.White)
                            }
                        }
                    }
                }

                // 3. Official Release Details & Specs
                PaperCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        PaperSectionHeader(title = "مشخصات این نسخه (v2.35.0)")
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SpecItem(label = "نسخه منتشرشده", value = "v2.35.0 Stable")
                            SpecItem(label = "تاریخ انتشار", value = "نوامبر ۲۰۲۶")
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SpecItem(label = "معماری هدف", value = "ARM64 / Universal")
                            SpecItem(label = "حجم فایل", value = "۲۴.۸ مگابایت")
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SpecItem(label = "کد بسته (Package)", value = "com.aistudio.jimvpn")
                            SpecItem(label = "هسته پروکسی", value = "Aether TLS v3.2")
                        }
                    }
                }

                // 4. Download Direct Links & Mirrors Section
                PaperCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = GlassSurfaceBase,
                    borderColor = PastelIceBlue.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        PaperSectionHeader(title = "لینک‌های دانلود مستقیم فایل APK")
                        Spacer(modifier = Modifier.height(10.dp))

                        // Link 1: Universal APK Direct GitHub Release
                        DownloadLinkCard(
                            title = "دانلود مستقیم فایل APK (یونیورسال)",
                            subtitle = "مناسب برای تمام گوشی‌های اندروید (ARM64 & ARMv7 & x86)",
                            badgeText = "پیش‌فرض",
                            badgeColor = PastelMint,
                            onClickDownload = {
                                openUrl(context, directApkUrl)
                            },
                            onClickCopy = {
                                clipboardManager.setText(AnnotatedString(directApkUrl))
                                Toast.makeText(context, "لینک مستقیم کپی شد", Toast.LENGTH_SHORT).show()
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Link 2: High-Speed CDN Mirror
                        DownloadLinkCard(
                            title = "دانلود پرسرعت از آینه داخلی (CDN Fast Mirror)",
                            subtitle = "سرعت بالا و بدون محدودیت حتی در اینترنت ملی (jsDelivr / Arvan CDN)",
                            badgeText = "پایدار و سریع",
                            badgeColor = PastelIceBlue,
                            onClickDownload = {
                                openUrl(context, cdnMirrorUrl)
                            },
                            onClickCopy = {
                                clipboardManager.setText(AnnotatedString(cdnMirrorUrl))
                                Toast.makeText(context, "لینک آینه کپی شد", Toast.LENGTH_SHORT).show()
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Link 3: ARM64-v8a Optimized Version
                        DownloadLinkCard(
                            title = "دانلود نسخه اختصاصی 64-Bit (ARM64-v8a)",
                            subtitle = "حجم کمتر و مصرف باتری بهینه‌تر برای گوشی‌های جدید",
                            badgeText = "کم‌حجم",
                            badgeColor = PastelAmber,
                            onClickDownload = {
                                openUrl(context, arm64ApkUrl)
                            },
                            onClickCopy = {
                                clipboardManager.setText(AnnotatedString(arm64ApkUrl))
                                Toast.makeText(context, "لینک نسخه ۶۴ بیتی کپی شد", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                // 5. Config Subscription & Auto Import
                PaperCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = GlassSurfaceBase,
                    borderColor = PastelAmber.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        PaperSectionHeader(title = "اشتراک کانفیگ‌ها و سرورها از این مخزن")
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "سرورهای VLESS Reality و کانفیگ‌های خفن به‌صورت خودکار از مخزن فوق به‌روزرسانی می‌شوند.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = GlassTextSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = subUrl,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = GlassTextMuted
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onImportSubscription()
                                    Toast.makeText(context, "دریافت کانفیگ‌ها از مخزن آغاز شد", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1.3f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PastelMint)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "همگام‌سازی",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "دریافت آنی کانفیگ‌ها", fontSize = 11.5.sp, color = Color.White)
                            }

                            OutlinedButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(subUrl))
                                    Toast.makeText(context, "لینک اشتراک کپی شد", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "کپی",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "کپی لینک", fontSize = 11.5.sp)
                            }
                        }
                    }
                }

                // 6. Release Notes & Features List
                PaperCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        PaperSectionHeader(title = "ویژگی‌های خفن این نسخه")
                        Spacer(modifier = Modifier.height(8.dp))

                        FeatureRow(text = "اتصال مستقیم به مخزن dnjebeuifedihwdih/- جهت دریافت سرورهای بدون قطع")
                        FeatureRow(text = "هسته قدرتمند Aether TLS Fragment با ضد سانسور هوشمند")
                        FeatureRow(text = "پشتیبانی کامل از پروتکل‌های VLESS Reality و MASQUE H2/H3")
                        FeatureRow(text = "تفکیک هوشمند برنامه‌ها (Split Tunneling) ویژه بانک‌ها و اپ‌های ایرانی")
                        FeatureRow(text = "نمایش زنده پینگ، سرعت دانلود/آپلود و لوکیشن آی‌پوی خروجی")
                        FeatureRow(text = "رابط کاربری فوق‌العاده زیبا با تم شیشه‌ای لکوئید گلاس (Liquid Glass UI)")
                    }
                }

                // 7. QR Code & Share Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showQrDialog = !showQrDialog },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "QR Code",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "کد QR دانلود", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putchar("EXTRA_TEXT", "دانلود نسخه جدید جیم وی پی ان (JIM VPN v2.35.0):\n\nمخزن گیت‌هاب:\n$repoUrl\n\nلینک دانلود مستقیم:\n$directApkUrl")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "اشتراک‌گذاری جیم وی پی ان"))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PastelIceBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "اشتراک‌گذاری",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "اشتراک‌گذاری", fontSize = 12.sp, color = Color.White)
                    }
                }

                // QR Code Modal Display if Toggled
                if (showQrDialog) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White)
                            .border(1.dp, PastelIceBlue, RoundedCornerShape(18.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "اسکن کد QR جهت دانلود سریع با موبایل",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = GlassTextPrimary
                            )

                            // Vector representation of QR Code frame
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .border(2.dp, PastelIceBlue, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode2,
                                        contentDescription = "QR Code",
                                        tint = Color(0xFF1E293B),
                                        modifier = Modifier.size(110.dp)
                                    )
                                    Text(
                                        text = "dnjebeuifedihwdih/-",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp
                                        ),
                                        color = GlassTextSecondary
                                    )
                                }
                            }

                            Text(
                                text = "دوربین گوشی را روی تصویر بالا بگیرید تا دانلود آغاز شود",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = GlassTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = GlassTextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp
            ),
            color = GlassTextPrimary
        )
    }
}

@Composable
private fun FeatureRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = PastelMint,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
            color = GlassTextPrimary
        )
    }
}

@Composable
private fun DownloadLinkCard(
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color,
    onClickDownload: () -> Unit,
    onClickCopy: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xF0FFFFFF))
            .border(1.dp, Color.White, RoundedCornerShape(16.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = GlassTextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .border(1.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = badgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = GlassTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onClickDownload,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = badgeColor)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "دانلود",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "دانلود فایل", fontSize = 11.sp, color = Color.White)
                }

                OutlinedButton(
                    onClick = onClickCopy,
                    modifier = Modifier.weight(0.7f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "کپی",
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = "کپی لینک", fontSize = 11.sp)
                }
            }
        }
    }
}

private fun openUrl(context: android.content.Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "خطا در باز کردن مرورگر: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun Intent.putchar(key: String, value: String) {
    this.putExtra(key, value)
}
