package com.example.dailytasks.core.domain

import java.util.UUID

abstract class TaskModel() {
    abstract val name : String
    abstract val type : TypeTask
    abstract val id : String

    companion object{
        fun createId(name : String, type : TypeTask) : String {
            return "task_${UUID.randomUUID()}_${name.replace("\\s".toRegex(), "")}_${type.name}"
        }
    }
}



