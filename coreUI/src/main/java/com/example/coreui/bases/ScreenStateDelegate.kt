package com.example.coreui.bases

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.core.domain.ApiError
import com.example.core.domain.DataState
import com.example.core.ui.ViewAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ScreenStateDelegate @Inject constructor(
) {
    private var loadingShown = mutableStateOf(false)
    private var apiError = MutableStateFlow<ApiError?>(null)
    private val ongoingRequests = HashSet<ViewAction>()

    fun showLoading() {
        loadingShown.value = true
    }

    fun hideLoading() {
        loadingShown.value = false
    }

    fun showError(
        errorTitle: String? = null,
        errorDesc: String? = null
    ) {
        apiError.update { ApiError(errorTitle, errorDesc) }
        hideLoading()
    }

    fun updateState(action: ViewAction, state: DataState<out Any>) {
        when (state) {
            is DataState.Loading -> {
                ongoingRequests.add(action)
            }

            is DataState.Error -> {
                ongoingRequests.remove(action)
                onFailure(state)
            }

            is DataState.Success -> {
                if (!state.cached)
                    ongoingRequests.remove(action)
            }
        }

        if (ongoingRequests.isEmpty())
            hideLoading()
        else
            showLoading()
    }

    private fun onFailure(it: DataState<*>) {
        showError(it.apiError?.title, it.apiError?.message)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun StatesContainer(
        Container: @Composable ScreenStatesScope.() -> Unit
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
        val error by apiError.collectAsStateWithLifecycle()

        val isLoading by remember { loadingShown }

        val navController = rememberNavController()

        Container(
            ScreenStatesScope(
                isLoading,
                error,
                navController,
                scrollBehavior
            )
        )
    }

    inner class ScreenStatesScope @OptIn(ExperimentalMaterial3Api::class) constructor(
        val loadingShown: Boolean,
        val apiError: ApiError?,
        val navController: NavHostController,
        val scrollBehavior: TopAppBarScrollBehavior
    )
}