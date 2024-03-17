package com.example.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.coreui.components.AppScaffold
import com.example.ui.R
import com.example.ui.list.states.ListMoviesState
import com.example.core.R as coreR


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(viewModel: MoviesListViewModel = hiltViewModel(), onMovieClick: (id: Int) -> Unit) {

    val stateDelegate = viewModel.screenStateDelegate
    var searchQuery by remember { mutableStateOf("") }
    stateDelegate.StatesContainer {
        AppScaffold(
            isLoading = loadingShown,
            scrollBehavior = scrollBehavior,
            apiError = apiError,
            topBar = {
                AppHeader(scrollBehavior = scrollBehavior, query = searchQuery) {
                    searchQuery = it
                    viewModel.resetPagination(searchQuery)
                }
            }) {

            val lazyListState = rememberLazyGridState()
            val state = rememberPullToRefreshState()

            val cachedMoviesState by viewModel.cachedMovies.collectAsStateWithLifecycle()

            val moviesState by viewModel.movies.collectAsStateWithLifecycle()

            val shouldGetNextPage by remember {
                derivedStateOf {
                    (viewModel.canPaginate && (lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                        ?: -4) >= (lazyListState.layoutInfo.totalItemsCount - 6))
                }
            }

            LaunchedEffect(shouldGetNextPage) {
                if (shouldGetNextPage) viewModel.requestMovies(searchQuery)
            }

            Box(Modifier.nestedScroll(state.nestedScrollConnection)) {
                MoviesList(moviesState ?: cachedMoviesState, lazyListState, onMovieClick)
                PullToRefreshContainer(
                    modifier = Modifier.align(Alignment.TopCenter),
                    state = state,
                )
            }

            LaunchedEffect(state.isRefreshing) {
                if (state.isRefreshing)
                    viewModel.resetPagination(searchQuery)
                state.endRefresh()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader(
    scrollBehavior: TopAppBarScrollBehavior, query: String, onQueryChange: (query: String) -> Unit
) {
    LargeTopAppBar(title = {
        Text(text = stringResource(R.string.header_title))
    }, actions = {
        SearchInput(
            query = query, onQueryChange = onQueryChange
        )
    }, scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchInput(
    query: String, onQueryChange: (query: String) -> Unit
) {
    SearchBar(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .padding(bottom = 8.dp),
        active = false,
        onActiveChange = {},
        onSearch = onQueryChange,
        query = query,
        colors = SearchBarDefaults.colors(
            containerColor = Color.Cyan
        ),
        trailingIcon = { Icon(imageVector = Icons.Rounded.Search, contentDescription = null) },
        placeholder = {
            Text(text = stringResource(id = R.string.placeholder_search))
        },
        onQueryChange = onQueryChange,
    ) {}
}

@Composable
fun MoviesList(
    moviesState: ListMoviesState.ListMovies?, listState: LazyGridState, onMovieClick: (id: Int) -> Unit
) {
    val movies = moviesState?.list

    LazyVerticalGrid(
        state = listState,
        columns = GridCells.Adaptive(185.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = movies ?: emptyList()) { movie ->
            MoviePoster(movie.posterUrl) {
                onMovieClick(movie.id)
            }
        }
    }
}

@Composable
fun MoviePoster(posterUrl: String?, onMovieClick: () -> Unit) {
    AsyncImage(
        modifier = Modifier
            .width(185.dp)
            .clickable {
                onMovieClick()
            },
        contentScale = ContentScale.FillWidth,
        model = ImageRequest.Builder(LocalContext.current).data(posterUrl).placeholder(coreR.drawable.placeholder).build(),
        contentDescription = "MOVIE_POSTER"
    )
}