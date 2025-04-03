package com.example.androidtbc.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidtbc.R
import com.example.androidtbc.presentation.theme.AppColors

@Composable
fun ProfileScreen(
    state: ProfileState,
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(45.dp))

            Text(
                text = stringResource(R.string.profile),
                fontSize = 48.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.your_email),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(end = 20.dp)
                )

                Text(
                    text = state.userEmail ?: "",
                    fontSize = 21.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = onLogout,
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.PrimaryColor,
                    disabledContainerColor = AppColors.PrimaryColor.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = stringResource(R.string.log_out),
                        fontSize = 23.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}