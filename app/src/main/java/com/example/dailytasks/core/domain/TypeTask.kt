package com.example.dailytasks.core.domain

import androidx.compose.ui.graphics.Color

enum class TypeTask(val title : String, val color : Color) {
    PERSONAL("Personal", Color(0xFF5973FF)), // Blue complementary
    STUDY("Study", Color(0xFFFF5959)),    // Reddish
    WORK("Work", Color(0xFF59FFD3)),     // Mint
    HEALTH("Health", Color(0xFFB6FF59)),  // Lime
    OTHER("Other", Color(0xFFBDBDBD))     // Gray
}

fun TypeTask.brandColor(): Color = this.color
