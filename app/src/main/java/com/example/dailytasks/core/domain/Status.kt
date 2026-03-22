package com.example.dailytasks.core.domain

/**
 * Representa los estados generales de carga de datos en la aplicación.
 */
enum class Status {
    /** Estado inicial sin carga activa. */
    UNDEFINED,
    /** Los datos se cargaron con éxito. */
    SUCCESS,
    /** Ocurrió un error durante la carga. */
    ERROR,
    /** La carga está en proceso. */
    LOADING
}
