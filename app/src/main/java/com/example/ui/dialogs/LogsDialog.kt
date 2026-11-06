package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.core.LogCategory
import com.example.ui.components.PaperCard
import com.example.ui.components.PaperSectionHeader
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.PastelAmber
import com.example.ui.theme.PastelIceBlue
import com.example.ui.theme.PastelMint

@Composable
fun LogsDialog(
    logs: List<String>,
    onClearLogs: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()

    var selectedCategory by remember { mutableStateOf<LogCategory>(LogCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredLogs = remember(logs, selectedCategory, searchQuery) {
        logs.filter { line ->
            val matchesCategory = when (selectedCategory) {
                LogCategory.ALL -> true
                LogCategory.TUNNEL -> line.contains("TUNNEL") || line.contains("VPN") || line.contains("AETHER") || line.contains("VPS")
                LogCategory.NETWORK -> line.contains("NET") || line.contains("SOCKET") || line.contains("PING") || line.contains("IP")
                LogCategory.DNS -> line.contains("DNS") || line.contains("RESOLVE")
                LogCategory.ERROR -> line.contains("ERR") || line.contains("ERROR") || line.contains("FAIL")
                LogCategory.SYSTEM -> line.contains("SYS") || line.contains("INIT")
            }
            val matchesSearch = searchQuery.isBlank() || line.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    LaunchedEffect(filteredLogs.size) {
        if (filteredLogs.isNotEmpty()) {
            listState.animateScrollToItem(filteredLogs.size - 1)
        }
    }

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
                    .padding(14.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PaperSectionHeader(title = "ترمینال زنده پیشرفته وقایع (Logs)")

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Copy Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x30FFFFFF))
                                .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .clickable {
                                    val text = filteredLogs.joinToString("\n")
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("JimVPN Logs", text)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "کپی شد (${filteredLogs.size} سطر)", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "کپی",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "کپی",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                    color = NeonCyan
                                )
                            }
                        }

                        // Clear Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x30FFFFFF))
                                .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .clickable { onClearLogs() }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "پاکسازی",
                                    tint = NeonCoral,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "پاکسازی",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                    color = NeonCoral
                                )
                            }
                        }

                        // Close Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x30FFFFFF))
                                .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .clickable { onDismiss() }
                                .padding(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "بستن",
                                tint = GlassTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar Filter
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("جستجو در خطوط لاگ...", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = GlassTextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "جستجو", tint = PastelIceBlue, modifier = Modifier.size(16.dp)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0x30FFFFFF),
                        unfocusedContainerColor = Color(0x20FFFFFF),
                        focusedBorderColor = PastelIceBlue,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                        focusedTextColor = GlassTextPrimary,
                        unfocusedTextColor = GlassTextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Log Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(LogCategory.values()) { cat ->
                        val isSelected = cat == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PastelIceBlue.copy(alpha = 0.25f) else Color(0x30FFFFFF))
                                .border(1.dp, if (isSelected) PastelIceBlue else Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = cat.tagFa,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    fontSize = 10.5.sp
                                ),
                                color = if (isSelected) PastelIceBlue else GlassTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Terminal Dark High-Tech Screen Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF070B14))
                        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                        .padding(10.dp)
                ) {
                    if (filteredLogs.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "هیچ لاگی در این دسته‌بندی یافت نشد...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                ),
                                color = GlassTextSecondary
                            )
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(filteredLogs) { line ->
                                val color = when {
                                    line.contains("ERR") || line.contains("ERROR") || line.contains("FAIL") -> NeonCoral
                                    line.contains("WARN") || line.contains("TIP") -> PastelAmber
                                    line.contains("READY") || line.contains("CONNECTED") || line.contains("SUCCESS") -> NeonEmerald
                                    line.contains("DNS") -> PastelMint
                                    line.contains("VPN") || line.contains("TUNNEL") || line.contains("AETHER") -> NeonCyan
                                    line.contains("NET") || line.contains("SOCKET") -> NeonBlue
                                    else -> GlassTextPrimary
                                }
                                Text(
                                    text = line,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.5.sp,
                                        lineHeight = 15.sp
                                    ),
                                    color = color,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
