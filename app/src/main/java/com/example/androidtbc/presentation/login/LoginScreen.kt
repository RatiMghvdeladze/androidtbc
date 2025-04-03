package com.example.androidtbc.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
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
fun LoginScreen(
    state: LoginState,
    email: String? = null,
    password: String? = null,
    onLogin: (email: String, password: String, rememberMe: Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {}
) {

    var emailInput by remember(email) { mutableStateOf(email ?: "") }
    var passwordInput by remember(password) { mutableStateOf(password ?: "") }
    var rememberMe by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

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
                text = stringResource(R.string.login),
                color = Color.Black,
                fontSize = 48.sp
            )

            Spacer(modifier = Modifier.height(21.dp))

            Image(
                modifier = Modifier.padding(bottom = 25.dp),
                painter = painterResource(id = R.drawable.logo_login),
                contentDescription = "Login Logo",
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = AppColors.PrimaryColor
                        )
                    )
                    Text(
                        text = stringResource(R.string.remember_me),
                        color = Color.Black
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = stringResource(R.string.don_t_have_an_account),
                        color = Color.Black
                    )
                    TextButton(
                        onClick = onRegisterClick
                    ) {
                        Text(
                            text = stringResource(R.string.register),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            CustomButton(
                text = stringResource(R.string.login),
                onClick = { onLogin(emailInput, passwordInput, rememberMe) },
                isLoading = state.isLoading,
                enabled = !state.isLoading &&
                        state.emailError == null &&
                        state.passwordError == null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AppTheme {
        val previewState = LoginState(
            isLoading = false,
            emailError = null,
            passwordError = null
        )

        LoginScreen(
            state = previewState,
            email = "john@doe.com",
            password = "helloWorld",
            onLogin = { _, _, _ -> },
            onRegisterClick = {},
            onEmailChange = {},
            onPasswordChange = {}
        )
    }
}