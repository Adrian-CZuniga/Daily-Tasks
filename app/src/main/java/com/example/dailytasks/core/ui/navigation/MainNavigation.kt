package com.example.dailytasks.core.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class MainNavigation {
    @Serializable
    data object Home : MainNavigation()
    @Serializable
    data object AddTask : MainNavigation()
}