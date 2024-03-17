package com.example.coreui.bases

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.ApiError
import com.example.core.domain.DataState
import com.example.core.ui.Error
import com.example.core.ui.Loading
import com.example.core.ui.ViewAction
import com.example.core.ui.ViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<ACTION : ViewAction>(
    val screenStateDelegate: ScreenStateDelegate,
    protected val paginationDelegate: PaginationDelegate,
    private val state: SavedStateHandle
) : ViewModel() {

    protected val uiState = MutableStateFlow<ViewState?>(null)

    fun <T> Flow<T>.collectAsStateFlow() = stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        null
    )

    private fun Flow<DataState<out Any>>.toUiState(action: ACTION, isPaging: Boolean): Flow<ViewState?> {
        return map { dataState ->
            when (dataState) {
                is DataState.Loading -> Loading
                is DataState.Error -> {
                    if (isPaging)
                        paginationDelegate.updatePaginationState(false)
                    Error(requestFailed(action, dataState.apiError))
                }

                is DataState.Success -> {
                    if (dataState.cached) cachedDataReceived(
                        action,
                        dataState.data
                    )
                    else if (isPaging) {
                        paginationDelegate.updatePaginationState(true, dataState.data as? List<Any>)
                        requestSucceeded(action, paginationDelegate.dataSet)
                    } else requestSucceeded(action, dataState.data)
                }
            }
        }
    }

    suspend fun enqueueSuspendAction(action: ACTION): ViewState? {
        return initAction(action).lastOrNull()
    }

    fun enqueuePagingAction(action: ACTION) {
        paginationDelegate.blockPaging()
        initAction(action, true).launchIn(viewModelScope)
    }

    fun enqueueAction(action: ACTION) {
        initAction(action).launchIn(viewModelScope)
    }

    private fun initAction(action: ACTION, isPaging: Boolean = false): Flow<ViewState?> {
        return request(action)
            .onEach { screenStateDelegate.updateState(action, it) }
            .toUiState(action, isPaging)
            .onEach { uiState.update { _ -> it } }
    }

    open fun requestFailed(action: ACTION, apiError: ApiError?) = apiError

    abstract fun cachedDataReceived(
        action: ACTION,
        data: Any?
    ): ViewState

    abstract fun requestSucceeded(action: ACTION, data: Any?): ViewState

    abstract fun request(action: ACTION): Flow<DataState<out Any>>

    override fun onCleared() {
        paginationDelegate.reset()
        super.onCleared()
    }

    companion object {
        private const val UI_STATE_KEY = "UI_STATE_KEY"
    }
}