package com.example.dailytasks.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class TicketIndexDto(
    val mapping: Map<String, String> = emptyMap() // "ticketId" -> "yyyy-MM-dd"
)