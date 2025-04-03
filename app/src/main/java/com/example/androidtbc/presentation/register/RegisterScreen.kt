package com.example.androidtbc.presentation.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidtbc.R
import com.example.androidtbc.presentation.theme.AppColors
import com.example.androidtbc.presentation.theme.AppTheme
import com.example.androidtbc.presentation.utils.CustomButton
import com.example.androidtbc.presentation.utils.CustomTextField

@Composable
fun RegisterScreen(
    state: RegisterState,
    onRegister: (email: String, password: String, repeatPassword: String) -> Unit,
    onBackClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRepeatPasswordChange: (String, String) -> Unit
) {
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var repeatPasswordInput by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var passwordRepeatVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BackgroundColor)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 30.dp)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(65.dp))

            Text(
                text = stringResource(R.string.register),
                color = Color.Black,
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(21.dp))


            Image(
                painter = painterResource(id = R.drawable.logo_register),
                contentDescription = "Register Logo",
                modifier = Modifier.padding(bottom = 25.dp)
            )

            CustomTextField(
                value = emailInput,
                onValueChange = {
                    emailInput = it
                    onEmailChange(it)
                },
                label = stringResource(R.string.email),
                leadingIconRes = R.drawable.ic_email,
                isError = state.emailError != null,
                errorText = state.emailError,
                keyboardType = KeyboardType.Email
            )

            CustomTextField(
                value = passwordInput,
                onValueChange = {
                    passwordInput = it
                    onPasswordChange(it)
                    onRepeatPasswordChange(it, repeatPasswordInput)
                                },
                label = stringResource(R.string.password),
                leadingIconRes = R.drawable.ic_lock,
                isError = state.passwordError != null,
                errorText = state.passwordError,
                keyboardType = KeyboardType.Password,
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordVisibilityToggle = { passwordVisible = !passwordVisible }
            )

            CustomTextField(
                value = repeatPasswordInput,
                onValueChange = {
                    repeatPasswordInput = it
                    onRepeatPasswordChange(passwordInput, it)
                                },
                label = stringResource(R.string.repeat_password),
                leadingIconRes = R.drawable.ic_lock,
                isError = state.repeatPasswordError != null,
                errorText = state.repeatPasswordError,
                keyboardType = KeyboardType.Password,
                isPassword = true,
                passwordVisible = passwordRepeatVisible,
                onPasswordVisibilityToggle = { passwordRepeatVisible = !passwordRepeatVisible }
            )

            Spacer(modifier = Modifier.height(21.dp))


            CustomButton(
                text = stringResource(R.string.register),
                onClick = { onRegister(emailInput, passwordInput, repeatPasswordInput) },
                isLoading = state.isLoading,
                enabled = !state.isLoading &&
                        state.emailError == null &&
                        state.passwordError == null &&
                        state.repeatPasswordError == null
            )
        }
        IconButton(
            onClick = {
                onBackClick()
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                tint = AppColors.PrimaryColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    AppTheme {
        val previewState = RegisterState(
            isLoading = false,
            emailError = null,
            passwordError = null,
            repeatPasswordError = null
        )

        RegisterScreen(
            state = previewState,
            onRegister = { _, _, _ -> },
            onBackClick = {},
            onEmailChange = {},
            onPasswordChange = {},
            onRepeatPasswordChange = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenWithErrorsPreview() {
    AppTheme {
        val previewState = RegisterState(
            isLoading = false,
            emailError = "Invalid email format",
            passwordError = "Password must be at least 6 characters",
            repeatPasswordError = "Passwords do not match"
        )

        RegisterScreen(
            state = previewState,
            onRegister = { _, _, _ -> },
            onBackClick = {},
            onEmailChange = {},
            onPasswordChange = {},
            onRepeatPasswordChange = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenLoadingPreview() {
    AppTheme {
        val previewState = RegisterState(
            isLoading = true,
            emailError = null,
            passwordError = null,
            repeatPasswordError = null
        )

        RegisterScreen(
            state = previewState,
            onRegister = { _, _, _ -> },
            onBackClick = {},
            onEmailChange = {},
            onPasswordChange = {},
            onRepeatPasswordChange = { _, _ -> }
        )
    }
}