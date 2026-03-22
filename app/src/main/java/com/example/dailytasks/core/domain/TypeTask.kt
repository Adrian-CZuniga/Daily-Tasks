package com.example.dailytasks.core.domain

import androidx.compose.ui.graphics.Color

/**
 * Define las categorías de las tareas y sus colores asociados.
 */
enum class TypeTask(val title : String, val color : Color) {
    PERSONAL("Personal", Color(0xFF5973FF)),
    STUDY("Study", Color(0xFFFF5959)),
    WORK("Work", Color(0xFF59FFD3)),
    HEALTH("Health", Color(0xFFB6FF59)),
    OTHER("Other", Color(0xFFBDBDBD))
}

/**
 * Extensión para obtener el color de marca de la categoría.
 */
fun TypeTask.brandColor(): Color = this.color
