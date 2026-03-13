package com.example.dailytasks.core.utils

import java.io.File

/**
 * Guarda contenido en un archivo de forma segura (atómica). * Previene que el FileObserver dispare una lectura de un JSON incompleto.
 */
fun File.writeAtomic(content: String) {
    // 1. Crear un archivo temporal en la misma carpeta
    val tempFile = File.createTempFile(
        "temp_${this.nameWithoutExtension}_", 
        ".tmp", 
        this.parentFile
    )
    
    try {
        // 2. Escribir todo el contenido en el temporal
        tempFile.writeText(content)
        
        // 3. Renombrar el temporal al archivo original (Operación Atómica)
        // Esto reemplaza el original instantáneamente.
        if (!tempFile.renameTo(this)) {
            throw Exception("No se pudo renombrar el archivo temporal")
        }
    } catch (e: Exception) {
        if (tempFile.exists()) tempFile.delete()
        throw e
    }
}