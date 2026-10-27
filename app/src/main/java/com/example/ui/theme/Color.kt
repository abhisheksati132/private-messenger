package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// SOFT SLATE & ARCTIC BLUE PALETTE (Clean, Minimal, iMessage-Inspired)
// =========================================================================

// --- Light Theme ---
val LightBackground = Color(0xFFFFFFFF)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9) // Soft cool slate for bubbles & search
val LightSurfaceContainer = Color(0xFFE2E8F0)
val LightOnSurface = Color(0xFF0F172A) // Deep charcoal slate
val LightOnSurfaceVariant = Color(0xFF64748B) // Gentle muted slate
val LightOutline = Color(0xFFE2E8F0)
val LightOutlineVariant = Color(0xFFF1F5F9)

val PrimaryLight = Color(0xFF007AFF) // Iconic Arctic / Apple Blue
val PrimaryContainerLight = Color(0xFFE0F2FE)
val OnPrimaryContainerLight = Color(0xFF0369A1)

val SecondaryContainerLight = Color(0xFFF1F5F9)
val OnSecondaryContainerLight = Color(0xFF0F172A)

val TertiaryContainerLight = Color(0xFFE0F2FE)
val OnTertiaryContainerLight = Color(0xFF0284C7)

val SuccessGreen = Color(0xFF10B981)
val SuccessContainer = Color(0xFFD1FAE5)
val ErrorRed = Color(0xFFEF4444)
val ErrorContainer = Color(0xFFFEE2E2)

// --- Dark Theme ---
val DarkBackground = Color(0xFF0B0F19) // Elegant deep slate charcoal
val DarkSurface = Color(0xFF0B0F19)
val DarkSurfaceVariant = Color(0xFF1E293B) // Soft elevated slate for bubbles & cards
val DarkSurfaceContainer = Color(0xFF334155)
val DarkOnSurface = Color(0xFFF8FAFC) // Crisp clean white
val DarkOnSurfaceVariant = Color(0xFF94A3B8) // Soft readable gray
val DarkOutline = Color(0xFF334155)
val DarkOutlineVariant = Color(0xFF1E293B)

val DarkPrimary = Color(0xFF388EFE) // Vibrant Arctic Blue in dark mode
val DarkPrimaryContainer = Color(0xFF0C3866)
val DarkOnPrimaryContainer = Color(0xFFD4E7FF)

// Friendly, human, soft pastel avatar color pairings
val AvatarColors = listOf(
    Pair(Color(0xFFE0F2FE), Color(0xFF0369A1)), // Arctic Blue
    Pair(Color(0xFFD1FAE5), Color(0xFF047857)), // Fresh Mint
    Pair(Color(0xFFEDE9FE), Color(0xFF6D28D9)), // Soft Lavender
    Pair(Color(0xFFFEF3C7), Color(0xFFB45309)), // Warm Amber
    Pair(Color(0xFFFFE4E6), Color(0xFFBE123C)), // Rose Coral
    Pair(Color(0xFFE2E8F0), Color(0xFF334155))  // Cool Slate
)
