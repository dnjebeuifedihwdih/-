package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppInfoItem
import com.example.model.SplitTunnelMode
import com.example.ui.components.PaperCard
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald

@Composable
fun SplitTunnelDialog(
    currentMode: SplitTunnelMode,
    selectedPackages: Set<String>,
    installedApps: List<AppInfoItem>,
    isLoadingApps: Boolean,
    onSelectMode: (SplitTunnelMode) -> Unit,
    onTogglePackage: (String) -> Unit,
    onApplyIranianPreset: () -> Unit,
    onClearSelection: () -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredApps = remember(installedApps, searchQuery) {
        if (searchQuery.isBlank()) {
            installedApps
        } else {
            val q = searchQuery.trim().lowercase()
            installedApps.filter { it.appName.lowercase().contains(q) || it.packageName.lowercase().contains(q) }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            PaperCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.92f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Title Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "تفکیک برنامه‌ها (Split Tunneling)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                ),
                                color = GlassTextPrimary
                            )
                            Text(
                                text = "عبور برنامه‌های بانکی و ایرانی بدون فیلترشکن",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = GlassTextSecondary
                            )
                        }

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
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mode Selection Buttons
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SplitTunnelMode.values().forEach { mode ->
                            val isSelected = mode == currentMode
                            val shape = RoundedCornerShape(14.dp)
                            val bg = if (isSelected) {
                                Brush.horizontalGradient(listOf(NeonCyan.copy(alpha = 0.20f), NeonBlue.copy(alpha = 0.10f)))
                            } else {
                                Brush.horizontalGradient(listOf(Color(0x12FFFFFF), Color(0x05FFFFFF)))
                            }
                            val border = if (isSelected) {
                                Brush.horizontalGradient(listOf(NeonCyan, NeonBlue))
                            } else {
                                Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.12f), Color.White.copy(alpha = 0.04f)))
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(shape)
                                    .background(bg)
                                    .border(1.dp, border, shape)
                                    .clickable { onSelectMode(mode) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = mode.titleFa,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                fontSize = 12.sp
                                            ),
                                            color = if (isSelected) NeonCyan else GlassTextPrimary
                                        )
                                        Text(
                                            text = mode.subtitleFa,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                            color = GlassTextSecondary
                                        )
                                    }
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(NeonCyan),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "انتخاب شده",
                                                tint = Color.Black,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Smart Preset Button: Iranian Banks
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(NeonEmerald.copy(alpha = 0.18f))
                            .border(1.dp, NeonEmerald.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                            .clickable { onApplyIranianPreset() }
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = "تنظیم سریع",
                                tint = NeonEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "⚡ انتخاب هوشمند برنامه‌های بانکی و ایرانی (Bypass)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.5.sp
                                ),
                                color = NeonEmerald
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search input & selected count
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x200F172A))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "جستجو",
                            tint = GlassTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.weight(1f),
                            textStyle = MaterialTheme.typography.labelMedium.copy(
                                color = GlassTextPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            cursorBrush = SolidColor(NeonCyan),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "جستجوی برنامه بر اساس نام یا پکیج...",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = GlassTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        )
                        if (searchQuery.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "پاک کردن",
                                tint = GlassTextSecondary,
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable { searchQuery = "" }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Counter bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "برنامه‌های انتخاب‌شده: ${selectedPackages.size}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 10.5.sp
                            ),
                            color = NeonCyan
                        )
                        if (selectedPackages.isNotEmpty()) {
                            Text(
                                text = "پاک‌سازی انتخاب‌ها",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.5.sp
                                ),
                                color = NeonCoral,
                                modifier = Modifier.clickable { onClearSelection() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Apps List
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x150F172A))
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                    ) {
                        if (isLoadingApps) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = NeonCyan,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        } else if (filteredApps.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "برنامه‌ای یافت نشد",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = GlassTextSecondary
                                )
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(filteredApps, key = { it.packageName }) { app ->
                                    val isChecked = selectedPackages.contains(app.packageName)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onTogglePackage(app.packageName) }
                                            .background(if (isChecked) NeonCyan.copy(alpha = 0.08f) else Color.Transparent)
                                            .padding(horizontal = 12.dp, vertical = 9.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = app.appName,
                                                    style = MaterialTheme.typography.labelMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp
                                                    ),
                                                    color = GlassTextPrimary
                                                )
                                                if (app.isIranianOrBank) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(NeonEmerald.copy(alpha = 0.2f))
                                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                                    ) {
                                                        Text(
                                                            text = "بانکی/داخلی",
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                fontSize = 8.5.sp,
                                                                color = NeonEmerald
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                            Text(
                                                text = app.packageName,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 9.5.sp,
                                                    color = GlassTextSecondary
                                                ),
                                                maxLines = 1
                                            )
                                        }

                                        // Checkbox box
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .border(1.dp, if (isChecked) NeonCyan else GlassTextSecondary, RoundedCornerShape(6.dp))
                                                .background(if (isChecked) NeonCyan else Color.Transparent),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isChecked) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "انتخاب شده",
                                                    tint = Color.Black,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Confirm / Done Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.horizontalGradient(listOf(NeonCyan, NeonBlue)))
                            .clickable { onDismiss() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ذخیره و اعمال تنظیمات",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 13.5.sp,
                                color = Color.Black
                            )
                        )
                    }
                }
            }
        }
    }
}
