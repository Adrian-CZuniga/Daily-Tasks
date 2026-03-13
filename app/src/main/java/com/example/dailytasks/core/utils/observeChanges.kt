package com.example.dailytasks.core.utils

import android.os.Build
import android.os.FileObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import java.io.File



/**
 * Escucha cambios en un archivo específico y emite Unit cada vez que cambia.
 * Usa la API moderna de FileObserver para evitar deprecations.
 */
fun File.observeChanges(): Flow<Unit> = callbackFlow {
    val observer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // API 29+: Constructor que recibe el objeto File directamente
        object : FileObserver(this@observeChanges, MODIFY or CLOSE_WRITE or DELETE_SELF) {
            override fun onEvent(event: Int, path: String?) {
                trySend(Unit)
            }
        }
    } else {
        // Versiones antiguas: Constructor que recibe el String path
        @Suppress("DEPRECATION")
        object : FileObserver(this@observeChanges.path, MODIFY or CLOSE_WRITE or DELETE_SELF) {
            override fun onEvent(event: Int, path: String?) {
                trySend(Unit)
            }
        }
    }

    // Si el archivo no existe, el observer no fallará, pero no detectará nada hasta que exista.
    // Es recomendable que el directorio padre exista.
    observer.startWatching()

    // Importante: Detener el observador cuando el Flow se cancela (ej: al salir de la pantalla)
    awaitClose { observer.stopWatching() }
}.onStart { emit(Unit) } // Emitimos inicialmente para que el Flow lea el archivo la primera vez