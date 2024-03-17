package com.example.repo.repos

import com.example.core.domain.DataState
import com.example.domain.models.Movie
import com.example.domain.repos.MovieRepository
import com.example.repo.NetworkBoundResource
import com.example.repo.dataproviders.local.TopRatedMoviesLocalDataProvider
import com.example.repo.dataproviders.remote.DetailsRemoteDataProvider
import com.example.repo.dataproviders.remote.ListRemoteDataProvider
import com.example.repo.mappers.MovieMapper
import com.example.repo.models.responses.ListResponse
import com.example.repo.models.responses.RawMovie
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val remoteDataProvider: ListRemoteDataProvider,
    private val movieDetailsDataProvider: DetailsRemoteDataProvider,
    private val movieMapper: MovieMapper,
    private val topRatedDataProvider: TopRatedMoviesLocalDataProvider,
) : MovieRepository {

    override fun getTopRatedMovies(page: Int): Flow<DataState<List<Movie>>> {
        return object : NetworkBoundResource<ListResponse<RawMovie>, List<Movie>>() {

            override suspend fun fetchFromNetwork(): ListResponse<RawMovie> {
                return remoteDataProvider.getTopRatedMovies(page)
            }

            override fun toDomainModels(response: ListResponse<RawMovie>): DataState.Success<List<Movie>> {
                return DataState.Success(response.results.map { movieMapper.toDomainLayer(it) })
            }

            override suspend fun cacheDataResponse(item: ListResponse<RawMovie>) {
                topRatedDataProvider.saveData(item.results)
            }

            override suspend fun loadFromCache(): ListResponse<RawMovie> {
                val list = topRatedDataProvider.getData()
                return ListResponse(list)
            }

            override suspend fun hasCachedData(): Boolean {
                return topRatedDataProvider.hasData() && page == 1
            }
        }.asFlow()
    }

    override fun searchMovies(query: String, page: Int): Flow<DataState<List<Movie>>> {
        return object : NetworkBoundResource<ListResponse<RawMovie>, List<Movie>>() {

            override suspend fun fetchFromNetwork(): ListResponse<RawMovie> {
                return remoteDataProvider.searchMovies(query, page)
            }

            override fun toDomainModels(response: ListResponse<RawMovie>): DataState.Success<List<Movie>> {
                return DataState.Success(response.results.map { movieMapper.toDomainLayer(it) })
            }
        }.asFlow()

    }

    override fun getMovieDetails(id: Int): Flow<DataState<Movie>> {
        return object : NetworkBoundResource<RawMovie, Movie>() {

            override suspend fun fetchFromNetwork(): RawMovie {
                return movieDetailsDataProvider.getMovieDetails(id)
            }

            override fun toDomainModels(response: RawMovie): DataState.Success<Movie> {
                return DataState.Success(movieMapper.toDomainLayer(response))
            }
        }.asFlow()
    }
}