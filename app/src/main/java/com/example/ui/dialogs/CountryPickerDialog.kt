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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Country
import com.example.model.SupportedCountries
import com.example.ui.components.PaperCard
import com.example.ui.components.PaperSectionHeader
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan

@Composable
fun CountryPickerDialog(
    selectedCountry: Country,
    onSelectCountry: (Country) -> Unit,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        PaperCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
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
                    PaperSectionHeader(title = "انتخاب کشور و سرور خروجی")
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

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "کشور مقصد جهت اتصال مستقیم سایفون و گیت‌وی کلودفلر را انتخاب کنید:",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 11.sp,
                        color = GlassTextSecondary
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SupportedCountries.list.forEach { country ->
                        val isSelected = selectedCountry.code == country.code
                        val shape = RoundedCornerShape(16.dp)
                        val bg = if (isSelected) {
                            Brush.horizontalGradient(listOf(NeonCyan.copy(alpha = 0.25f), NeonBlue.copy(alpha = 0.12f)))
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
                                .clickable {
                                    onSelectCountry(country)
                                    onDismiss()
                                }
                                .padding(horizontal = 14.dp, vertical = 11.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = country.flagEmoji,
                                        fontSize = 22.sp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = country.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                            fontSize = 13.5.sp
                                        ),
                                        color = if (isSelected) NeonCyan else GlassTextPrimary
                                    )
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(NeonCyan),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "انتخاب شد",
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
        }
    }
}
