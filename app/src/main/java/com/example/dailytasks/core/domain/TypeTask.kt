package com.example.dailytasks.core.domain

import androidx.compose.ui.graphics.Color

enum class TypeTask(val title : String, val color : Color) {
    PERSONAL("Personal", Color.Blue),
    STUDY("Study", Color.Red),
    WORK("Work", Color.Green),
    HEALTH("Health", Color.Yellow),
    OTHER("Other", Color.Gray)
}
