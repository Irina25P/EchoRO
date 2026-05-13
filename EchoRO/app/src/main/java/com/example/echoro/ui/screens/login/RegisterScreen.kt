package com.example.echoro.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.echoro.R
import com.example.echoro.viewmodel.auth.AuthScreenEvent
import com.example.echoro.viewmodel.auth.AuthScreenOSE
import com.example.echoro.viewmodel.auth.AuthViewModel
import com.example.echoro.ui.theme.BackgroundGray
import com.example.echoro.ui.theme.NavyBlue
import com.example.echoro.ui.theme.Teal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = viewModel(),
    onNavigateToApp: (Int, String) -> Unit,
    onLoginClick: () -> Unit,
    onContinueAsGuest: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.authOSE.collect { ose ->
            if (ose is AuthScreenOSE.NavigateToApp) {
                onNavigateToApp(ose.userId, ose.role)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_echoro),
            contentDescription = "EchoRO Logo",
            modifier = Modifier.fillMaxWidth(0.35f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "EchoRO Pro",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = NavyBlue
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (authState.generalError != null) {
            Text(
                text = authState.generalError!!,
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        OutlinedTextField(
            value = fullName,
            onValueChange = {
                fullName = it
                viewModel.sendEvent(AuthScreenEvent.FullNameChanged(it))
            },
            placeholder = { Text("Full Name", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = authState.fullNameError != null,
            supportingText = { if (authState.fullNameError != null) Text(authState.fullNameError!!, color = Color.Red) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BackgroundGray, unfocusedContainerColor = BackgroundGray,
                focusedBorderColor = NavyBlue, unfocusedBorderColor = Color.Transparent,
                errorContainerColor = BackgroundGray, errorBorderColor = Color.Red
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                viewModel.sendEvent(AuthScreenEvent.EmailChanged(it))
            },
            placeholder = { Text("Email Address", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = authState.emailError != null,
            supportingText = { if (authState.emailError != null) Text(authState.emailError!!, color = Color.Red) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BackgroundGray, unfocusedContainerColor = BackgroundGray,
                focusedBorderColor = NavyBlue, unfocusedBorderColor = Color.Transparent,
                errorContainerColor = BackgroundGray, errorBorderColor = Color.Red
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                viewModel.sendEvent(AuthScreenEvent.PasswordChanged(it))
            },
            placeholder = { Text("Password", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = authState.passwordError != null,
            supportingText = { if (authState.passwordError != null) Text(authState.passwordError!!, color = Color.Red) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BackgroundGray, unfocusedContainerColor = BackgroundGray,
                focusedBorderColor = NavyBlue, unfocusedBorderColor = Color.Transparent,
                errorContainerColor = BackgroundGray, errorBorderColor = Color.Red
            ),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                }
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                viewModel.sendEvent(AuthScreenEvent.ConfirmPasswordChanged(it))
            },
            placeholder = { Text("Confirm Password", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = authState.confirmPasswordError != null,
            supportingText = { if (authState.confirmPasswordError != null) Text(authState.confirmPasswordError!!, color = Color.Red) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BackgroundGray, unfocusedContainerColor = BackgroundGray,
                focusedBorderColor = NavyBlue, unfocusedBorderColor = Color.Transparent,
                errorContainerColor = BackgroundGray, errorBorderColor = Color.Red
            ),
            singleLine = true,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.sendEvent(AuthScreenEvent.SignUpWithCredentials(fullName, email, password, confirmPassword))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
            enabled = !authState.isLoading
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Text(
                    text = "CREATE PRO ACCOUNT",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        val loginText = buildAnnotatedString {
            withStyle(style = SpanStyle(color = NavyBlue)) { append("Already have an account? ") }
            withStyle(style = SpanStyle(color = Teal, fontWeight = FontWeight.Bold)) { append("Login.") }
        }
        Text(text = loginText, fontSize = 14.sp, modifier = Modifier.clickable { onLoginClick() })

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Continue as Guest",
            color = Teal,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onContinueAsGuest() }
        )
    }
}