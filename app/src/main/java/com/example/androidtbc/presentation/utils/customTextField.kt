package com.example.androidtbc.presentation.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidtbc.R
import com.example.androidtbc.presentation.theme.AppColors
import com.example.androidtbc.presentation.theme.textFieldColors

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIconRes: Int,
    isError: Boolean = false,
    errorText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityToggle: (() -> Unit)? = null
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        singleLine = true,
        maxLines = 1,
        leadingIcon = {
            Icon(
                modifier = Modifier.padding(horizontal = 12.dp),
                painter = painterResource(id = leadingIconRes),
                contentDescription = label,
                tint = AppColors.PrimaryColor,
            )
        },
        trailingIcon = if (isPassword) {
            {
                val visibilityIcon = if (passwordVisible)
                    painterResource(id = R.drawable.ic_visibility_on)
                else
                    painterResource(id = R.drawable.ic_visibility_off)

                IconButton(onClick = { onPasswordVisibilityToggle?.invoke() }) {
                    Icon(
                        painter = visibilityIcon,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        isError = isError,
        supportingText = { errorText?.let { Text(text = it) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = TextStyle(fontSize = 24.sp),
        colors = textFieldColors()
    )
}