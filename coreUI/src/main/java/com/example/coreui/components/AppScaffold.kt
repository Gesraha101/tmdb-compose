package com.example.coreui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.zIndex
import com.example.core.domain.ApiError
import com.example.coreui.theme.TmdbComposeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    apiError: ApiError?,
    isLoading: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
    topBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }
    TmdbComposeTheme {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState)
            },
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                topBar()
            },
            content = {
                Box(modifier = Modifier.padding(it)) {
                    if (isLoading)
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .zIndex(1f)
                        )
                    content()
                }
            })
    }

    LaunchedEffect(apiError) {
        apiError?.message?.let {
            snackBarHostState.showSnackbar(it, withDismissAction = true, duration = SnackbarDuration.Indefinite)
        }
    }
}