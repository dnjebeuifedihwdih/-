package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Country
import com.example.model.TunnelMethod
import com.example.model.VpnConnectionState
import com.example.model.VpnStatus
import com.example.ui.theme.GlassBgBase
import com.example.ui.theme.GlassCardBg
import com.example.ui.theme.GlassCardBorder
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.PastelAmber
import com.example.ui.theme.PastelCoral
import com.example.ui.theme.PastelIceBlue
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelPink
import com.example.ui.theme.PastelSky
import com.example.ui.theme.PastelSlate
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * LiquidLens Colorful Pastel Mesh Canvas Background.
 * Dynamic, fluid morphing pastel gradient spheres floating continuously behind liquid glass.
 */
@Composable
fun LiquidMeshBackground(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "liquid_lens_pastel")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "mesh_time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Soft Twilight Base
        drawRect(color = GlassBgBase)

        // Sphere 1: Pastel Lavender & Violet (Top Right)
        val x1 = w * 0.70f + sin(time) * (w * 0.15f)
        val y1 = h * 0.18f + sin(time * 0.8f) * (h * 0.10f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x90C084FC),
                    Color(0x50E9D5FF),
                    Color.Transparent
                ),
                center = Offset(x1, y1),
                radius = w * 0.85f
            ),
            center = Offset(x1, y1),
            radius = w * 0.85f
        )

        // Sphere 2: Pastel Sky & Ocean Blue (Center Left)
        val x2 = w * 0.20f + sin(time * 0.7f + 1.5f) * (w * 0.18f)
        val y2 = h * 0.45f + sin(time * 0.9f) * (h * 0.12f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x8038BDF8),
                    Color(0x40818CF8),
                    Color.Transparent
                ),
                center = Offset(x2, y2),
                radius = w * 0.80f
            ),
            center = Offset(x2, y2),
            radius = w * 0.80f
        )

        // Sphere 3: Pastel Rose & Sunset Pink (Bottom Right)
        val x3 = w * 0.80f + sin(time * 1.1f + 3.0f) * (w * 0.12f)
        val y3 = h * 0.75f + sin(time * 0.6f) * (h * 0.10f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x80F472B6),
                    Color(0x40FBCFE8),
                    Color.Transparent
                ),
                center = Offset(x3, y3),
                radius = w * 0.75f
            ),
            center = Offset(x3, y3),
            radius = w * 0.75f
        )

        // Sphere 4: Pastel Mint Green & Peach (Top Left)
        val x4 = w * 0.15f + sin(time * 0.85f + 4.5f) * (w * 0.12f)
        val y4 = h * 0.82f + sin(time * 0.75f) * (h * 0.08f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x8034D399),
                    Color(0x40FBBF24),
                    Color.Transparent
                ),
                center = Offset(x4, y4),
                radius = w * 0.70f
            ),
            center = Offset(x4, y4),
            radius = w * 0.70f
        )
    }
}

/**
 * PaperDotGrid compatibility extension.
 */
@Composable
fun Modifier.paperDotGrid(
    dotColor: Color = Color(0x08000000),
    step: Dp = 20.dp,
    dotRadius: Dp = 1.0.dp
): Modifier = this

/**
 * Translucent Liquid Glass Container (LiquidLens Frosted Glass).
 */
@Composable
fun PaperCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = GlassCardBg,
    borderColor: Color = GlassCardBorder,
    shadowColor: Color = Color.Transparent,
    borderWidth: Dp = 1.dp,
    shadowOffset: Dp = 0.dp,
    cornerRadius: Dp = 24.dp,
    content: @Composable () -> Unit
) {
    val glassShape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .clip(glassShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x55FFFFFF),
                        Color(0x30FFFFFF)
                    )
                )
            )
            .border(
                width = borderWidth,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.85f),
                        Color.White.copy(alpha = 0.30f)
                    )
                ),
                shape = glassShape
            )
            .padding(0.dp)
    ) {
        content()
    }
}

/**
 * Clean Liquid Glass Pill Button.
 */
@Composable
fun PaperButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "",
    icon: (@Composable () -> Unit)? = null,
    backgroundColor: Color = Color(0x50FFFFFF),
    textColor: Color = GlassTextPrimary,
    borderColor: Color = GlassCardBorder,
    shadowOffset: Dp = 0.dp,
    enabled: Boolean = true,
    testTag: String = "paper_button"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "btn_scale")
    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .testTag(testTag)
            .scale(scale)
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        Color(0x30FFFFFF)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.9f),
                        Color.White.copy(alpha = 0.4f)
                    )
                ),
                shape = shape
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                icon()
                if (text.isNotEmpty()) Spacer(modifier = Modifier.width(8.dp))
            }
            if (text.isNotEmpty()) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
                    ),
                    color = if (enabled) textColor else textColor.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Translucent Glass Selection Chip.
 */
@Composable
fun PaperChip(
    label: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)
    val bgColors = if (isSelected) {
        listOf(PastelIceBlue.copy(alpha = 0.25f), PastelIceBlue.copy(alpha = 0.15f))
    } else {
        listOf(Color(0x40FFFFFF), Color(0x20FFFFFF))
    }
    val borderBrush = if (isSelected) {
        Brush.linearGradient(listOf(PastelIceBlue, PastelIceBlue.copy(alpha = 0.6f)))
    } else {
        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.8f), Color.White.copy(alpha = 0.3f)))
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(Brush.verticalGradient(bgColors))
            .border(1.dp, borderBrush, shape)
            .clickable { onSelect() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                fontSize = 11.5.sp
            ),
            color = if (isSelected) PastelIceBlue else GlassTextSecondary
        )
    }
}

/**
 * LiquidLens iOS Control Center Tab Switcher.
 */
@Composable
fun MechanicalTabSwitch(
    selectedMethod: TunnelMethod,
    onSelectMethod: (TunnelMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    val pillShape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(pillShape)
            .background(Color(0x40FFFFFF))
            .border(1.dp, Brush.linearGradient(listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0.35f))), pillShape)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TunnelMethod.values().forEach { method ->
                val isSelected = method == selectedMethod
                val tabBg = if (isSelected) {
                    Brush.horizontalGradient(listOf(PastelIceBlue.copy(alpha = 0.22f), PastelIceBlue.copy(alpha = 0.12f)))
                } else {
                    Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                }
                val tabBorder = if (isSelected) {
                    Brush.horizontalGradient(listOf(PastelIceBlue, PastelIceBlue.copy(alpha = 0.5f)))
                } else {
                    Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(tabBg)
                        .border(if (isSelected) 1.5.dp else 0.dp, tabBorder, RoundedCornerShape(20.dp))
                        .clickable { onSelectMethod(method) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = method.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = if (isSelected) GlassTextPrimary else GlassTextSecondary
                        )
                        Text(
                            text = method.subtitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = if (isSelected) PastelIceBlue else GlassTextSecondary.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * iOS Control Center Liquid Glass Power Switch Capsule.
 * Ultra-responsive, bug-free, tactile power switch card.
 */
/**
 * Cyber Tactical Liquid Glass Reactor Switch (Game-Style HUD Connect Trigger).
 * Interactive, futuristic, smooth gesture & tap trigger with rotating hologram rings and haptic pulses.
 */
@Composable
fun InkStampPowerButton(
    status: VpnStatus,
    activeInfo: String,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isConnected = status == VpnStatus.CONNECTED
    val isConnecting = status == VpnStatus.CONNECTING

    val primaryColor = when {
        isConnected -> PastelMint
        isConnecting -> PastelAmber
        else -> PastelIceBlue
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "power_reactor_scale")

    val infiniteTransition = rememberInfiniteTransition(label = "hud_reactor_spin")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isConnected) 5000 else 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "reactor_ring_rotation"
    )

    val capsuleShape = RoundedCornerShape(32.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .testTag("ink_power_btn")
            .clip(capsuleShape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0x65FFFFFF),
                        if (isConnected) PastelMint.copy(alpha = 0.25f) else Color(0x38FFFFFF)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.95f),
                        primaryColor.copy(alpha = 0.70f)
                    )
                ),
                shape = capsuleShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                vibrateFeedback(context)
                onToggle()
            }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Game Sci-Fi Reactor Core with Hologram Ring
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Rotating HUD Ring Arc Canvas
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeW = 2.dp.toPx()
                        val r = (size.minDimension - strokeW) / 2f
                        val centerOffset = Offset(size.width / 2f, size.height / 2f)

                        // Outer Dashed Orbit
                        drawCircle(
                            color = primaryColor.copy(alpha = 0.35f),
                            radius = r,
                            center = centerOffset,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = strokeW,
                                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                            )
                        )

                        // Rotating Arc
                        drawArc(
                            color = primaryColor,
                            startAngle = rotationAngle,
                            sweepAngle = if (isConnected) 240f else 120f,
                            useCenter = false,
                            topLeft = Offset((size.width - 2 * r) / 2f, (size.height - 2 * r) / 2f),
                            size = androidx.compose.ui.geometry.Size(2 * r, 2 * r),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeW + 1.dp.toPx())
                        )
                    }

                    // Inner Orb Sphere
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        primaryColor.copy(alpha = 0.45f),
                                        primaryColor.copy(alpha = 0.15f)
                                    )
                                )
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.9f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isConnecting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp,
                                color = PastelAmber
                            )
                        } else {
                            Icon(
                                imageVector = if (isConnected) Icons.Default.Shield else Icons.Default.PowerSettingsNew,
                                contentDescription = "کلید رآکتور اتصال",
                                tint = primaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Center: Game Tactical Text & Matrix Status
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when {
                                isConnected -> "SYSTEM ONLINE"
                                isConnecting -> "LOCKING CORE..."
                                else -> "ENGAGE TUNNEL"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp,
                                letterSpacing = 1.sp
                            ),
                            color = primaryColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(primaryColor)
                        )
                    }
                    Text(
                        text = when {
                            isConnected -> "تونل امن دکمه‌ای فعال"
                            isConnecting -> "در حال راه‌اندازی ماتریکس..."
                            else -> "برای فعال‌سازی لمس کنید"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 14.5.sp
                        ),
                        color = GlassTextPrimary
                    )
                    Text(
                        text = activeInfo,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = GlassTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Right: Futuristic Tactile Reactor Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.30f),
                                primaryColor.copy(alpha = 0.12f)
                            )
                        )
                    )
                    .border(1.dp, primaryColor.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 9.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when {
                            isConnected -> "قطع"
                            isConnecting -> "..."
                            else -> "اتصال"
                        },
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        ),
                        color = primaryColor
                    )
                }
            }
        }
    }
}

private fun vibrateFeedback(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            @Suppress("DEPRECATION")
            vibrator?.vibrate(45)
        }
    } catch (_: Exception) {}
}

/**
 * Translucent Liquid Glass Real-time Speed Meter Card.
 */
@Composable
fun LiveSpeedMeterCard(
    vpnState: VpnConnectionState,
    modifier: Modifier = Modifier
) {
    PaperCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "سرعت شبکه",
                        tint = PastelIceBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "پایش زنده پهنای باند و مصرف ترافیک",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 12.5.sp
                        ),
                        color = GlassTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dual Speed Pill Displays
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Download Speed
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x45FFFFFF))
                        .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(PastelMint.copy(alpha = 0.20f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "دانلود",
                                tint = PastelMint,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "دریافت (DL)",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = GlassTextSecondary
                            )
                            Text(
                                text = vpnState.formattedDownSpeed(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.5.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = PastelMint
                            )
                        }
                    }
                }

                // Upload Speed
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x45FFFFFF))
                        .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(PastelIceBlue.copy(alpha = 0.20f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "آپلود",
                                tint = PastelIceBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "ارسال (UL)",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = GlassTextSecondary
                            )
                            Text(
                                text = vpnState.formattedUpSpeed(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.5.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = PastelIceBlue
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Total Traffic Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x35FFFFFF))
                    .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "حجم کل مصرفی:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = GlassTextSecondary
                )
                Text(
                    text = "📥 ${vpnState.formattedTotalRx()}  •  📤 ${vpnState.formattedTotalTx()}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = GlassTextPrimary
                )
            }
        }
    }
}

/**
 * Quick Country Selector Translucent Glass Card.
 */
@Composable
fun QuickCountryCard(
    selectedCountry: Country,
    onOpenCountryPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    PaperCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenCountryPicker() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = selectedCountry.flagEmoji.ifBlank { "🌐" },
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "موقعیت خروجی اتصال",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = GlassTextSecondary
                    )
                    Text(
                        text = selectedCountry.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        ),
                        color = GlassTextPrimary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(PastelIceBlue.copy(alpha = 0.18f))
                    .border(1.dp, PastelIceBlue.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = selectedCountry.code,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        ),
                        color = PastelIceBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "انتخاب کشور",
                        tint = PastelIceBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Latency Ping Test Bar.
 */
@Composable
fun LatencyTestBar(
    pingMs: Long,
    isPinging: Boolean,
    onTestPing: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pingText = when {
        isPinging -> "در حال پینگ..."
        pingMs > 0 -> "$pingMs ms"
        pingMs == -2L -> "تایم‌آوت"
        else -> "تست نشده"
    }

    val pingColor = when {
        pingMs in 1..150 -> PastelMint
        pingMs in 151..300 -> PastelIceBlue
        pingMs > 300 -> PastelCoral
        else -> GlassTextSecondary
    }

    PaperCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(pingColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "تاخیر تونل (Ping):",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = GlassTextSecondary
                    )
                    Text(
                        text = pingText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 13.5.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = pingColor
                    )
                }
            }

            PaperButton(
                onClick = onTestPing,
                text = if (isPinging) "تست..." else "سنجش تاخیر",
                icon = {
                    if (isPinging) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = GlassTextPrimary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "پینگ",
                            tint = PastelIceBlue,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                },
                enabled = !isPinging,
                testTag = "test_ping_btn"
            )
        }
    }
}

/**
 * Section Header Component.
 */
@Composable
fun PaperSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Black,
            fontSize = 13.sp
        ),
        color = GlassTextPrimary,
        modifier = modifier
    )
}
