package com.example.androidtbc.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.androidtbc.R
import com.example.androidtbc.presentation.models.UserPresentation

@Composable
fun UserItem(user: UserPresentation) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(user.avatar)
                .crossfade(true)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .build()
        )

        Image(
            painter = painter,
            contentDescription = "User Avatar",
            modifier = Modifier.size(64.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                .height(48.dp),
        ) {
            Text(
                text = user.fullName,
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = user.email,
                color = Color.Black,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserItemPreview() {
    val user = UserPresentation(
        id = 1,
        email = "john.doe@example.com",
        avatar = "https://reqres.in/img/faces/1-image.jpg",
        fullName = "John Doe"
    )

    UserItem(user = user)

}