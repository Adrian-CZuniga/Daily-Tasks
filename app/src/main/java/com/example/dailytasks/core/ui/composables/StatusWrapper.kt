package com.example.dailytasks.core.ui.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dailytasks.core.domain.Status

@Composable
fun StatusWrapper(
    status: Status,
    modifier: Modifier = Modifier,
    loadingContent: @Composable () -> Unit = { DefaultLoading() },
    errorContent: @Composable () -> Unit = { DefaultError() },
    content: @Composable () -> Unit
) {
    AnimatedContent(
        targetState = status,
        transitionSpec = {
            fadeIn().togetherWith(fadeOut())
        },
        label = "status_transition",
        modifier = modifier
    ) { targetStatus ->
        when (targetStatus) {
            Status.LOADING -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    loadingContent()
                }
            }
            Status.ERROR -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    errorContent()
                }
            }
            Status.SUCCESS -> {
                content()
            }
            Status.UNDEFINED -> {
                Spacer(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun DefaultLoading() {
    CircularProgressIndicator(
        color = MaterialTheme.colorScheme.primary,
        strokeWidth = 3.dp
    )
}

@Composable
fun DefaultError() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Algo salió mal", style = MaterialTheme.typography.bodyLarge)
    }
}