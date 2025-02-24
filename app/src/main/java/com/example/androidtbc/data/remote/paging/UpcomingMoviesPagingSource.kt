package com.example.androidtbc.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidtbc.data.remote.dto.Result
import com.example.androidtbc.data.repository.MovieRepository
import com.example.androidtbc.utils.Resource

class UpcomingMoviesPagingSource(
    private val repository: MovieRepository
) : PagingSource<Int, Result>() {

    override fun getRefreshKey(state: PagingState<Int, Result>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Result> {
        return try {
            val page = params.key ?: 1
            when (val response = repository.getUpcomingMovies(page = page)) {
                is Resource.Success -> {
                    LoadResult.Page(
                        data = response.data.results,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (page < response.data.totalPages) page + 1 else null
                    )
                }
                is Resource.Error -> {
                    LoadResult.Error(Exception(response.errorMessage))
                }
                else -> LoadResult.Error(Exception("Unknown error"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}