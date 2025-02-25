package com.example.androidtbc.data.remote.paging

import android.util.Log
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

    // Cache for movies that can be filtered locally
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

        // Return empty results for very short queries
        if (normalizedQuery.length < MIN_QUERY_LENGTH) {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )
        }

        try {
            // If we have a complete query like "mufasa", try the direct API search first
            if (normalizedQuery.length >= 5) {
                when (val response = movieRepository.searchMovies(query = normalizedQuery, page = page)) {
                    is Resource.Success -> {
                        val results = response.data.results
                        Log.d("SearchPaging", "Success with complete query: $normalizedQuery, Results: ${results.size}")

                        // Add any new movies to our cache
                        updateCache(results)

                        return LoadResult.Page(
                            data = results,
                            prevKey = if (page == 1) null else page - 1,
                            nextKey = if (page >= response.data.totalPages) null else page + 1
                        )
                    }
                    is Resource.Error -> {
                        Log.e("SearchPaging", "Error with query: $normalizedQuery - ${response.errorMessage}")
                        // Fall through to local search
                    }
                    else -> {
                        Log.e("SearchPaging", "Unexpected result for query: $normalizedQuery")
                        // Fall through to local search
                    }
                }
            }

            // For partial queries or if API search failed, use local filtering on cached data
            return performLocalSearch(normalizedQuery, page)

        } catch (e: Exception) {
            Log.e("SearchPaging", "Exception during search: ${e.message}", e)
            return LoadResult.Error(e)
        }
    }

    private suspend fun performLocalSearch(query: String, page: Int): LoadResult<Int, MovieResult> {
        // If we haven't loaded initial data, fetch popular movies first
        if (!hasLoadedInitialData || cachedMovies.isEmpty()) {
            // Load a larger set of popular movies to search through
            val popularPages = 3 // Load multiple pages to have a good base for search

            try {
                for (p in 1..popularPages) {
                    when (val response = movieRepository.getPopularMovies(page = p)) {
                        is Resource.Success -> {
                            updateCache(response.data.results)
                        }
                        else -> {
                            Log.e("SearchPaging", "Failed to load popular movies for local search")
                        }
                    }
                }

                // Also try loading top rated as an alternative source
                when (val response = movieRepository.getTopRatedMovies(page = 1)) {
                    is Resource.Success -> {
                        updateCache(response.data.results)
                    }
                    else -> {
                        Log.e("SearchPaging", "Failed to load top rated movies for local search")
                    }
                }

                hasLoadedInitialData = true

            } catch (e: Exception) {
                Log.e("SearchPaging", "Error loading initial data: ${e.message}")
                return LoadResult.Error(e)
            }
        }

        // Filter the cached movies based on the query
        val filteredResults = withContext(Dispatchers.Default) {
            cachedMovies.filter { movie ->
                val title = movie.title?.lowercase() ?: ""
                val originalTitle = movie.originalTitle?.lowercase() ?: ""

                title.contains(query) || originalTitle.contains(query)
            }
        }

        Log.d("SearchPaging", "Local search for '$query' found ${filteredResults.size} results out of ${cachedMovies.size} cached movies")

        // Split into pages for the PagingSource
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
        // Add movies that aren't already in the cache
        for (movie in newMovies) {
            if (cachedMovies.none { it.id == movie.id }) {
                cachedMovies.add(movie)
            }
        }
    }
}