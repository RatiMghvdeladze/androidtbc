package com.example.androidtbc.data.remote.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidtbc.data.remote.dto.Result
import com.example.androidtbc.data.repository.MovieRepository
import com.example.androidtbc.utils.Resource

class SearchMoviesPagingSource(
    private val movieRepository: MovieRepository,
    private val query: String
) : PagingSource<Int, Result>() {

    override fun getRefreshKey(state: PagingState<Int, Result>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Result> {
        val page = params.key ?: 1

        if (query.isBlank()) {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )
        }

        return try {
            when (val response = movieRepository.searchMovies(query = query, page = page)) {
                is Resource.Success -> {
                    val data = response.data
                    val normalizedQuery = query.lowercase()

                    val filteredResults = data.results.filter { movie ->
                        val normalizedTitle = movie.title?.lowercase() ?: ""
                        val normalizedOriginalTitle = movie.originalTitle?.lowercase() ?: ""

                        // Ensure it includes search term anywhere, not just starts with
                        normalizedTitle.contains(normalizedQuery) || normalizedOriginalTitle.contains(normalizedQuery)
                    }

                    Log.d(
                        "SearchPaging",
                        "Query: $query, API Results: ${data.results.size}, Filtered Results: ${filteredResults.size}"
                    )

                    val nextKey = if (page >= data.totalPages) null else page + 1
                    val prevKey = if (page == 1) null else page - 1

                    LoadResult.Page(
                        data = filteredResults,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }
                is Resource.Error -> {
                    Log.e("SearchPaging", "Error in API response: ${response.errorMessage}")
                    LoadResult.Error(Exception(response.errorMessage))
                }
                else -> {
                    Log.e("SearchPaging", "Unexpected error occurred")
                    LoadResult.Error(Exception("Unknown error occurred"))
                }
            }
        } catch (e: Exception) {
            Log.e("SearchPaging", "Exception: ${e.message}", e)
            LoadResult.Error(e)
        }
    }
}
