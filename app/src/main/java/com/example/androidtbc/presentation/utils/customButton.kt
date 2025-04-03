package com.example.androidtbc.presentation.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidtbc.presentation.theme.AppColors

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    enabled: Boolean = true,
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        onClick = onClick,
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.PrimaryColor,
            disabledContainerColor = AppColors.PrimaryColor.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = AppColors.White
            )
        } else {
            Text(
                text = text,
                fontSize = 23.sp,
                color = AppColors.White
            )
        }
    }
}