package com.example.domain.usecases.list

import com.example.core.domain.DataState
import com.example.domain.models.Movie
import com.example.domain.repos.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(private val repo: MovieRepository) {
    operator fun invoke(query: String, page: Int): Flow<DataState<List<Movie>>> {
        return repo.searchMovies(query, page)
    }
}