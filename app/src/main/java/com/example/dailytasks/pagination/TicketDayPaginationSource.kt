package com.example.dailytasks.pagination

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.dailytasks.model.DayTicketModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate

class FileTicketsPagingSource(
    private val context: Context,
    private val fromDate : LocalDate,
    private val pageSize: Int
) : PagingSource<Int, DayTicketModel>() {

    private val allTicketFiles: List<File> by lazy {
        context.filesDir
            .listFiles { file -> file.name.startsWith("ticket_") && file.name.endsWith(".json") }
            ?.sortedBy { it.name }
            ?: emptyList()
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DayTicketModel> {
        return try {
            val page = params.key ?: 0
            val fromIndex = page * pageSize
            val toIndex = minOf(fromIndex + pageSize, allTicketFiles.size)

            val filesPage = allTicketFiles.subList(fromIndex, toIndex)

            val data = withContext(Dispatchers.IO) {
                filesPage.mapNotNull { file ->
                    runCatching {
                        val content = file.readText()
                        decodeDayTicketJson(content)
                    }.getOrNull()
                }
            }

            LoadResult.Page(
                data = data,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (toIndex >= allTicketFiles.size) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DayTicketModel>): Int = 0

    private fun decodeDayTicketJson(json: String): DayTicketModel? {
        return null
    }
}