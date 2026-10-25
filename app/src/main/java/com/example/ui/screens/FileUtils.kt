package com.example.ui.screens

import java.util.Locale

fun formatFileSize(bytes: Long): String {
    if (bytes <= 0L) return "0 B"
    val k = 1024.0
    val m = k * 1024.0
    val g = m * 1024.0
    return when {
        bytes < 1024L -> String.format(Locale.US, "%.1f B", bytes.toDouble())
        bytes < 1024L * 1024L -> String.format(Locale.US, "%.1f KB", bytes / k)
        bytes < 1024L * 1024L * 1024L -> String.format(Locale.US, "%.1f MB", bytes / m)
        else -> String.format(Locale.US, "%.1f GB", bytes / g)
    }
}
