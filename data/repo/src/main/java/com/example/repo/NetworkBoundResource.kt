package com.example.repo

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.example.core.domain.ApiError
import com.example.core.domain.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext


abstract class NetworkBoundResource<DataModel, DomainModel>(var context: CoroutineContext = Dispatchers.IO) {

    fun asFlow(): Flow<DataState<DomainModel>> {
        return flow {
            emit(DataState.Loading())

            if (hasCachedData()) loadFromCache()?.let {
                toDomainModels(it)?.let { res -> emit(res.apply { cached = true }) }
            }

            try {
                val result = fetchFromNetwork()
                result?.let {
                    toDomainModels(it)?.let { emit(it) }
                    cacheDataResponse(it)
                }
            } catch (e: Exception) {
                emit(handleException(e))
            }
        }.flowOn(context)
    }

    protected open fun toDomainModels(response: DataModel): DataState.Success<DomainModel>? = null

    @MainThread
    protected abstract suspend fun fetchFromNetwork(): DataModel

    @WorkerThread
    open suspend fun cacheDataResponse(item: DataModel) {
    }

    @MainThread
    protected open suspend fun loadFromCache(): DataModel? = null

    protected open suspend fun hasCachedData(): Boolean = false

    protected open suspend fun handleException(e: java.lang.Exception): DataState<DomainModel> {
        return DataState.Error(extractErrorStateMessage(e))
    }

    private fun extractErrorStateMessage(e: java.lang.Exception?) = ApiError(message = e?.message)
}
