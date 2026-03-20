package com.example.dailytasks.core.utils

import java.io.File

/**
 * Guarda contenido en un archivo de forma segura (atómica). * Previene que el FileObserver dispare una lectura de un JSON incompleto.
 */
fun File.writeAtomic(content: String) {
    val tempFile = File.createTempFile(
        "temp_${this.nameWithoutExtension}_", 
        ".tmp", 
        this.parentFile
    )
    
    try {
        tempFile.writeText(content)
        
        if (!tempFile.renameTo(this)) {
            throw Exception("No se pudo renombrar el archivo temporal")
        }
    } catch (e: Exception) {
        if (tempFile.exists()) tempFile.delete()
        throw e
    }
}