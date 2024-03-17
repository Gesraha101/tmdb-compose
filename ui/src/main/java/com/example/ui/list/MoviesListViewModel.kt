package com.example.ui.list

import androidx.lifecycle.SavedStateHandle
import com.example.core.domain.DataState
import com.example.core.ui.ViewState
import com.example.coreui.bases.BaseViewModel
import com.example.coreui.bases.PaginationDelegate
import com.example.coreui.bases.ScreenStateDelegate
import com.example.domain.models.Movie
import com.example.domain.usecases.list.ListTopRatedMoviesUseCase
import com.example.domain.usecases.list.SearchMoviesUseCase
import com.example.ui.list.actions.ListMoviesAction
import com.example.ui.list.states.ListMoviesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import javax.inject.Inject

@HiltViewModel
class MoviesListViewModel @Inject constructor(
    screenStateDelegate: ScreenStateDelegate,
    paginationDelegate: PaginationDelegate,
    state: SavedStateHandle,
    val topRatedMoviesUseCase: ListTopRatedMoviesUseCase,
    val searchMoviesUseCase: SearchMoviesUseCase
) : BaseViewModel<ListMoviesAction>(screenStateDelegate, paginationDelegate, state) {

    var cachedMovies =
        uiState.filterIsInstance<ListMoviesState.ListCachedMovies>()
            .collectAsStateFlow()
    var movies =
        uiState.filterIsInstance<ListMoviesState.ListMovies>()
            .collectAsStateFlow()
    val canPaginate get() = paginationDelegate.paginationAllowed

    override fun requestSucceeded(action: ListMoviesAction, data: Any?): ListMoviesState {
        return when (action) {
            is ListMoviesAction.SearchMovies ->
                ListMoviesState.ListSearchResults(data as? List<Movie>)

            is ListMoviesAction.GetTopRatedMovies ->
                ListMoviesState.ListTopRatedMovies(data as? List<Movie>)
        }
    }

    override fun request(action: ListMoviesAction): Flow<DataState<out Any>> {
        return when (action) {
            is ListMoviesAction.SearchMovies -> searchMoviesUseCase.invoke(action.query, paginationDelegate.page)
            is ListMoviesAction.GetTopRatedMovies -> topRatedMoviesUseCase.invoke(paginationDelegate.page)
        }
    }

    fun resetPagination(query: String) {
        paginationDelegate.reset()
        requestMovies(query)
    }

    fun requestMovies(query: String) {
        val action = if (query != "") ListMoviesAction.SearchMovies(query) else ListMoviesAction.GetTopRatedMovies
        enqueuePagingAction(action)
    }

    override fun cachedDataReceived(action: ListMoviesAction, data: Any?): ViewState {
        return ListMoviesState.ListCachedMovies(data as? List<Movie>)
    }
}