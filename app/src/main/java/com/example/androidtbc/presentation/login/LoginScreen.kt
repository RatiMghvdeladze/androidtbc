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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidtbc.R
import com.example.androidtbc.presentation.theme.AppTheme


@Composable
fun textFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedIndicatorColor = Color(0xFFB683F7),
        unfocusedIndicatorColor = Color(0xFFB683F7),
        focusedLabelColor = Color(0xFFB683F7),
        unfocusedLabelColor = Color(0xFFB683F7),
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        cursorColor = Color.Black
    )
}

val PrimaryColor = Color(0xFFB683F7)
val BackgroundColor = Color(0xFFF5F5F5)

@Composable
fun LoginScreen(
    state: LoginState,
    email: String? = null,
    password: String? = null,
    onLogin: (email: String, password: String, rememberMe: Boolean) -> Unit,
    onRegisterClick: () -> Unit
) {
    var emailInput by remember { mutableStateOf(email ?: "") }
    var passwordInput by remember { mutableStateOf(password ?: "") }
    var rememberMe by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
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

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(21.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_login),
                contentDescription = "Login Logo",
                modifier = Modifier.padding(bottom = 25.dp)
            )

            OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                label = { Text(text = stringResource(R.string.email)) },
                leadingIcon = { Icon(painter = painterResource(id = R.drawable.ic_email), contentDescription = "Email") },
                isError = state.emailError != null,
                supportingText = { state.emailError?.let { Text(text = it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textStyle = TextStyle(fontSize = 24.sp),
                colors = textFieldColors()
            )

            OutlinedTextField(
                value = passwordInput,
                onValueChange = { passwordInput = it },
                label = { Text(text = stringResource(R.string.password)) },
                leadingIcon = { Icon(painter = painterResource(id = R.drawable.ic_lock), contentDescription = "Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = state.passwordError != null,
                supportingText = { state.passwordError?.let { Text(text = it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 24.sp),
                colors = textFieldColors()
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
                            checkedColor = PrimaryColor
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

            Button(
                onClick = { onLogin(emailInput, passwordInput, rememberMe) },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    disabledContainerColor = PrimaryColor.copy(alpha = 0.6f)
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
                        text = stringResource(R.string.login),
                        fontSize = 23.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreenContent(
    state: LoginState,
    email: String,
    password: String,
    rememberMe: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRememberMeChange: (Boolean) -> Unit,
    onLoginClick: (String, String, Boolean) -> Unit,
    onRegisterClick: () -> Unit
) {
    var emailInput by remember { mutableStateOf(email) }
    var passwordInput by remember { mutableStateOf(password) }
    var rememberMeState by remember { mutableStateOf(rememberMe) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(65.dp))

            Text(
                text = stringResource(R.string.login),
                color = Color.Black,
                fontSize = 48.sp
            )

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(21.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_login),
                contentDescription = "Login Logo",
                modifier = Modifier.padding(bottom = 25.dp)
            )

            OutlinedTextField(
                value = emailInput,
                onValueChange = {
                    emailInput = it
                    onEmailChange(it)
                },
                label = { Text(text = stringResource(R.string.email)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_email),
                        contentDescription = "Email",
                        tint = PrimaryColor
                    )
                },
                isError = state.emailError != null,
                supportingText = { state.emailError?.let { Text(text = it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textStyle = TextStyle(fontSize = 24.sp),
                colors = textFieldColors()
            )

            OutlinedTextField(
                value = passwordInput,
                onValueChange = {
                    passwordInput = it
                    onPasswordChange(it)
                },
                label = { Text(text = stringResource(R.string.password)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = "Password",
                        tint = PrimaryColor
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                isError = state.passwordError != null,
                supportingText = { state.passwordError?.let { Text(text = it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 24.sp),
                colors = textFieldColors()
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
                        checked = rememberMeState,
                        onCheckedChange = {
                            rememberMeState = it
                            onRememberMeChange(it)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryColor
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

            Button(
                onClick = { onLoginClick(emailInput, passwordInput, rememberMeState) },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    disabledContainerColor = PrimaryColor.copy(alpha = 0.6f)
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
                        text = stringResource(R.string.login),
                        fontSize = 23.sp,
                        color = Color.White
                    )
                }
            }
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

        LoginScreenContent(
            state = previewState,
            email = "john@doe.com",
            password = "helloWorld",
            rememberMe = false,
            onEmailChange = {},
            onPasswordChange = {},
            onRememberMeChange = {},
            onLoginClick = { _, _, _ -> },
            onRegisterClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenWithErrorsPreview() {
    AppTheme {
        val previewState = LoginState(
            isLoading = false,
            emailError = "Invalid email format",
            passwordError = "Password must be at least 6 characters"
        )

        LoginScreenContent(
            state = previewState,
            email = "john@doe.com",
            password = "helloWorld",
            rememberMe = false,
            onEmailChange = {},
            onPasswordChange = {},
            onRememberMeChange = {},
            onLoginClick = { _, _, _ -> },
            onRegisterClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenLoadingPreview() {
    AppTheme {
        val previewState = LoginState(
            isLoading = true,
            emailError = null,
            passwordError = null
        )

        LoginScreenContent(
            state = previewState,
            email = "john@doe.com",
            password = "helloWorld",
            rememberMe = true,
            onEmailChange = {},
            onPasswordChange = {},
            onRememberMeChange = {},
            onLoginClick = { _, _, _ -> },
            onRegisterClick = {}
        )
    }
}