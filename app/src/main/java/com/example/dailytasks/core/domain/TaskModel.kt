package com.example.dailytasks.core.domain

import java.util.UUID

abstract class TaskModel(
    open val id : String
) {
    abstract val name : String
    abstract val type : TypeTask

    companion object{
        fun createId(name : String, type : TypeTask) : String {
            return "task_${UUID.randomUUID()}_${name.replace("\\s".toRegex(), "")}_${type.name}"
        }
    }
}



