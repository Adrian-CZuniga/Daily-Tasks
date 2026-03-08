package com.example.dailytasks.core.domain

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

abstract class TaskModel() {
    abstract val name : String
    abstract val type : TypeTask
    abstract val id : String

}



