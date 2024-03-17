package com.example.coreui.bases

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import javax.inject.Inject

enum class ListState {
    IDLE, LOADING, API_ERROR, NETWORK_ERROR, END_OF_PAGINATION
}

class PaginationDelegate @Inject constructor() {

    val dataSet = ArrayList<Any>()
    var page by mutableStateOf(1)
    private var listState by mutableStateOf(ListState.IDLE)
    val paginationAllowed: Boolean
        get() = listState != ListState.END_OF_PAGINATION && listState == ListState.IDLE
    val hasError get() = listState == ListState.API_ERROR || listState == ListState.NETWORK_ERROR

    fun blockPaging() {
        listState = ListState.LOADING
    }

    fun updatePaginationState(success: Boolean, list: List<Any>? = null) {
        listState = ListState.IDLE
        if (success) {
            if (list?.size != 20)
                listState = ListState.END_OF_PAGINATION

            list?.let { dataSet.addAll(it) }

            page++
        } else
            listState = ListState.NETWORK_ERROR
    }

    fun reset() {
        page = 1
        dataSet.clear()
        listState = ListState.IDLE
    }
}