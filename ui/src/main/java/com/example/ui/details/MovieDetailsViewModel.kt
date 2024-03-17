package com.example.ui.details

import com.example.core.domain.DataState
import com.example.core.ui.ViewState
import com.example.coreui.bases.BaseViewModel
import com.example.coreui.bases.PaginationDelegate
import com.example.coreui.bases.ScreenStateDelegate
import com.example.domain.models.Movie
import com.example.domain.usecases.details.GetMovieDetailsUseCase
import com.example.ui.details.actions.MovieDetailsAction
import com.example.ui.details.states.MovieDetailsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    screenStateDelegate: ScreenStateDelegate,
    paginationDelegate: PaginationDelegate,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
) : BaseViewModel<MovieDetailsAction>(screenStateDelegate, paginationDelegate) {

    var details =
        uiState.filterIsInstance<MovieDetailsState.DisplayMovieDetails>()
            .collectAsStateFlow()

    override fun requestSucceeded(action: MovieDetailsAction, data: Any?): MovieDetailsState {
        return when (action) {
            is MovieDetailsAction.GetMovieDetails -> MovieDetailsState.DisplayMovieDetails(data as? Movie)
        }
    }

    override fun request(action: MovieDetailsAction): Flow<DataState<out Any>> {
        return when (action) {
            is MovieDetailsAction.GetMovieDetails -> getMovieDetailsUseCase.invoke(action.id)
        }
    }

    override fun cachedDataReceived(action: MovieDetailsAction, data: Any?): ViewState {
        TODO("Not yet implemented")
    }
}