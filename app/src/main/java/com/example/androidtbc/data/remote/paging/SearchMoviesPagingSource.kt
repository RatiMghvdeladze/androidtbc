package com.example.androidtbc.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidtbc.data.remote.dto.MovieResult
import com.example.androidtbc.data.repository.MovieRepository
import com.example.androidtbc.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchMoviesPagingSource(
    private val movieRepository: MovieRepository,
    private val query: String
) : PagingSource<Int, MovieResult>() {

    companion object {
        private var cachedMovies: MutableList<MovieResult> = mutableListOf()
        private var hasLoadedInitialData = false
        private const val MIN_QUERY_LENGTH = 2
    }

    override fun getRefreshKey(state: PagingState<Int, MovieResult>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieResult> {
        val page = params.key ?: 1
        val normalizedQuery = query.lowercase().trim()

        if (normalizedQuery.length < MIN_QUERY_LENGTH) {
            return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
        }

        if (normalizedQuery.length >= 5) {
            when (val response = movieRepository.searchMovies(query = normalizedQuery, page = page)) {
                is Resource.Success -> {
                    updateCache(response.data.results)
                    return LoadResult.Page(
                        data = response.data.results,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (page >= response.data.totalPages) null else page + 1
                    )
                }
                is Resource.Error -> {}
                else -> {}
            }
        }

        return performLocalSearch(normalizedQuery, page)
    }

    private suspend fun performLocalSearch(query: String, page: Int): LoadResult<Int, MovieResult> {
        if (!hasLoadedInitialData || cachedMovies.isEmpty()) {
            for (p in 1..3) {
                when (val response = movieRepository.getPopularMovies(page = p)) {
                    is Resource.Success -> updateCache(response.data.results)
                    else -> {}
                }
            }

            when (val response = movieRepository.getTopRatedMovies(page = 1)) {
                is Resource.Success -> updateCache(response.data.results)
                else -> {}
            }

            hasLoadedInitialData = true
        }

        val filteredResults = withContext(Dispatchers.Default) {
            cachedMovies.filter { movie ->
                val title = movie.title?.lowercase() ?: ""
                val originalTitle = movie.originalTitle?.lowercase() ?: ""
                title.contains(query) || originalTitle.contains(query)
            }
        }

        val pageSize = 20
        val totalPages = (filteredResults.size + pageSize - 1) / pageSize
        val start = (page - 1) * pageSize
        val end = minOf(start + pageSize, filteredResults.size)

        val pageData = if (start < filteredResults.size) {
            filteredResults.subList(start, end)
        } else {
            emptyList()
        }

        return LoadResult.Page(
            data = pageData,
            prevKey = if (page > 1) page - 1 else null,
            nextKey = if (page < totalPages) page + 1 else null
        )
    }

    private fun updateCache(newMovies: List<MovieResult>) {
        newMovies.forEach { movie ->
            if (cachedMovies.none { it.id == movie.id }) {
                cachedMovies.add(movie)
            }
        }
    }
}
