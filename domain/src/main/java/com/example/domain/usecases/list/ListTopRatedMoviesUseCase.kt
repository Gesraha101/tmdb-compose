package com.example.domain.usecases.list

import com.example.core.domain.DataState
import com.example.domain.models.Movie
import com.example.domain.repos.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListTopRatedMoviesUseCase @Inject constructor(val repo: MovieRepository) {
    operator fun invoke(page: Int): Flow<DataState<List<Movie>>> {
        return repo.getTopRatedMovies(page)
    }
}