package com.example.androidtbc.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidtbc.AuthService
import com.example.androidtbc.responseDtoClasses.User

class UserPagingSource(private val authService: AuthService) : PagingSource<Int, User>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            val currentPage = params.key ?: 1
            val response = authService.getUsers(currentPage)

            if (response.isSuccessful) {
                val userResponse = response.body()
                val users = userResponse?.data ?: emptyList()

                LoadResult.Page(
                    data = users,
                    prevKey = if (currentPage == 1) null else currentPage - 1,
                    nextKey = if ((userResponse?.totalPages ?: 1) > currentPage) currentPage + 1 else null
                )
            } else {
                LoadResult.Error(Exception("API call failed: ${response.message()}"))
            }

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { position ->
            val anchorPage = state.closestPageToPosition(position)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
