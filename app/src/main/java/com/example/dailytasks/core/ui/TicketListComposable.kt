package com.example.dailytasks.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dailytasks.core.domain.DayTicketModel
import com.example.dailytasks.core.ui.composables.TicketListItem
import java.time.LocalDate

@Composable
fun TicketListComposable(
    modifier: Modifier = Modifier,
    taskList: List<DayTicketModel>,
    dateFilter: LocalDate? = null,
) {
    val filterList = taskList.filter { dateFilter == null || it.date.isEqual(dateFilter) }
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filterList) { ticket ->
            TicketListItem(
                modifier = Modifier.fillMaxWidth(),
                dayTicketModel = ticket,
                onToggleComplete = { }
            )
        }
    }
}