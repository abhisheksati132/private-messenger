package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Default Soft Slate & Arctic Blue Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = Color(0xFF388EFE),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFF8FAFC),
    tertiary = Color(0xFF94A3B8),
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    error = ErrorRed,
    errorContainer = Color(0xFF450A0A)
)

// Default Soft Slate & Arctic Blue Light Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = Color(0xFF0284C7),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = Color(0xFF64748B),
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    error = ErrorRed,
    errorContainer = ErrorContainer
)

// Clean Ocean Navy Theme (Telegram Style)
private val TelegramDarkColorScheme = darkColorScheme(
    primary = Color(0xFF5288C1),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF2B5278),
    onPrimaryContainer = Color(0xFFE1EEFA),
    secondary = Color(0xFF64B5F6),
    onSecondary = Color(0xFF0E1621),
    secondaryContainer = Color(0xFF1F2C3A),
    onSecondaryContainer = Color(0xFFE4ECF4),
    tertiary = Color(0xFF8DA3B8),
    background = Color(0xFF0E1621),
    onBackground = Color(0xFFF5F5F5),
    surface = Color(0xFF17212B),
    onSurface = Color(0xFFF5F5F5),
    surfaceVariant = Color(0xFF242F3D),
    onSurfaceVariant = Color(0xFFB0BCC8),
    outline = Color(0xFF2E3D4F),
    outlineVariant = Color(0xFF1E2834),
    error = ErrorRed,
    errorContainer = Color(0xFF450A0A)
)

// Clean Emerald Green Theme (Signal Style)
private val SignalEmeraldColorScheme = darkColorScheme(
    primary = Color(0xFF10B981),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF065F46),
    onPrimaryContainer = Color(0xFFD1FAE5),
    secondary = Color(0xFF34D399),
    onSecondary = Color(0xFF064E3B),
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Color(0xFFE0F2FE),
    tertiary = Color(0xFF6EE7B7),
    background = Color(0xFF062016),
    onBackground = Color(0xFFF2FDF7),
    surface = Color(0xFF0B2E21),
    onSurface = Color(0xFFF2FDF7),
    surfaceVariant = Color(0xFF134231),
    onSurfaceVariant = Color(0xFFA7F3D0),
    outline = Color(0xFF1A5A43),
    outlineVariant = Color(0xFF0E3829),
    error = ErrorRed,
    errorContainer = Color(0xFF450A0A)
)

// Clean Slate Monochrome Theme
private val SlateMonochromeColorScheme = darkColorScheme(
    primary = Color(0xFFE2E8F0),
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF334155),
    onPrimaryContainer = Color(0xFFF8FAFC),
    secondary = Color(0xFF94A3B8),
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFF8FAFC),
    tertiary = Color(0xFF64748B),
    background = Color(0xFF020617),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF0F172A),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155),
    outlineVariant = Color(0xFF1E293B),
    error = ErrorRed,
    errorContainer = Color(0xFF450A0A)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    themeName: String = "DEFAULT",
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> when (themeName.uppercase()) {
            "TELEGRAM_NAVY", "TELEGRAM", "OCEAN" -> TelegramDarkColorScheme
            "SIGNAL_EMERALD", "SIGNAL", "EMERALD" -> SignalEmeraldColorScheme
            "SLATE_MONOCHROME", "MONOCHROME", "SLATE" -> SlateMonochromeColorScheme
            else -> DarkColorScheme
        }
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
