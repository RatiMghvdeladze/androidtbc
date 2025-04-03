package com.example.androidtbc.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.androidtbc.R
import com.example.androidtbc.domain.models.UserDomain
import com.example.androidtbc.presentation.mapper.toUserPresentation
import com.example.androidtbc.presentation.theme.AppColors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun HomeScreen(
    state: HomeState,
    usersFlow: Flow<PagingData<UserDomain>>,
    onNavigateToProfile: () -> Unit,
    onRetry: () -> Unit
) {
    val lazyPagingItems = usersFlow.collectAsLazyPagingItems()

    Column(modifier = Modifier
        .fillMaxSize()
        .background(AppColors.BackgroundColor)
    ) {
        TextButton(
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.Start),
            onClick = onNavigateToProfile,
        ) {
            Text(
                text = stringResource(R.string.go_to_profile),
                color = Color(0xFF8607B6),
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(
                    count = lazyPagingItems.itemCount,
                    key = { index ->
                        val user = lazyPagingItems[index]
                        user?.id ?: index
                    }
                ) { index ->
                    val user = lazyPagingItems[index]
                    if (user != null) {
                        UserItem(user = user.toUserPresentation())
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                item {
                    if (lazyPagingItems.loadState.append is LoadState.Loading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        if (state.isLoading && lazyPagingItems.loadState.refresh !is LoadState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        if (lazyPagingItems.loadState.refresh is LoadState.NotLoading &&
            lazyPagingItems.itemCount == 0) {
            Text(
                text = stringResource(R.string.no_users_found),
                color = Color(0xFF8607B6),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
        }

        if (lazyPagingItems.loadState.refresh is LoadState.Error && lazyPagingItems.itemCount == 0) {
            Button(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
                onClick = {
                    lazyPagingItems.retry()
                    onRetry()
                }
            ) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val state = HomeState(
        isLoading = false,
    )

    val mockUsers = listOf(
        UserDomain(
            id = 1,
            email = "john.doe@example.com",
            firstName = "John",
            lastName = "Doe",
            avatar = "https://reqres.in/img/faces/1-image.jpg"
        ),
        UserDomain(
            id = 2,
            email = "jane.smith@example.com",
            firstName = "Jane",
            lastName = "Smith",
            avatar = "https://reqres.in/img/faces/2-image.jpg"
        ),
        UserDomain(
            id = 3,
            email = "steve.rogers@example.com",
            firstName = "Steve",
            lastName = "Rogers",
            avatar = "https://reqres.in/img/faces/3-image.jpg"
        )
    )

    val mockUsersFlow = flowOf(PagingData.from(mockUsers))

    HomeScreen(
        state = state,
        usersFlow = mockUsersFlow,
        onNavigateToProfile = { },
        onRetry = {}
    )
}