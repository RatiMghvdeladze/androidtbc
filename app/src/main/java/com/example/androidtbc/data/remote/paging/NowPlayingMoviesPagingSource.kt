package com.example.androidtbc.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidtbc.data.remote.dto.MovieResult
import com.example.androidtbc.data.repository.MovieRepository
import com.example.androidtbc.utils.Resource

class NowPlayingMoviesPagingSource(
    private val repository: MovieRepository
) : PagingSource<Int, MovieResult>() {

    override fun getRefreshKey(state: PagingState<Int, MovieResult>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieResult> {
        return try {
            val page = params.key ?: 1
            when (val response = repository.getNowPlayingMovies(page = page)) {
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