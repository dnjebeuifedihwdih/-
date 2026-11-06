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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.ui.components.PaperCard
import com.example.ui.components.PaperSectionHeader
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald

@Composable
fun LogsDialog(
    logs: List<String>,
    onClearLogs: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()

    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        PaperCard(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp)
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
                    PaperSectionHeader(title = "ترمینال و وقایع زنده اتصال")

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Copy Logs Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x200F172A))
                                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                                .clickable {
                                    val text = logs.joinToString("\n")
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Logs", text)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "گزارشات لاگ کپی شدند", Toast.LENGTH_SHORT).show()
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
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = NeonCyan
                                )
                            }
                        }

                        // Clear Logs Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x200F172A))
                                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
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
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = NeonCoral
                                )
                            }
                        }

                        // Close Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x200F172A))
                                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
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

                Spacer(modifier = Modifier.height(12.dp))

                // Terminal Console Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0A0F1D))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                        .padding(10.dp)
                ) {
                    if (logs.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ترمینال آماده ثبت رویدادها...",
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
                            items(logs) { line ->
                                val color = when {
                                    line.contains("ERR") || line.contains("ERROR") -> NeonCoral
                                    line.contains("WARN") || line.contains("TIP") -> NeonBlue
                                    line.contains("READY") || line.contains("Connected") || line.contains("CONNECTED") -> NeonEmerald
                                    line.contains("VPN") || line.contains("AETHER") -> NeonCyan
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
