package com.example.echoro.ui.screens.login

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echoro.R
import com.example.echoro.ui.theme.BackgroundGray
import com.example.echoro.ui.theme.NavyBlue
import com.example.echoro.ui.theme.Teal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterClick: (String, String, String) -> Unit,
    onLoginClick: () -> Unit,
    onContinueAsGuest: () -> Unit
) {

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

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

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = {
                fullName = it
                fullNameError = null
            },
            placeholder = { Text("Full Name", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = fullNameError != null,
            supportingText = { if (fullNameError != null) Text(fullNameError!!, color = Color.Red) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BackgroundGray,
                unfocusedContainerColor = BackgroundGray,
                focusedBorderColor = NavyBlue,
                unfocusedBorderColor = Color.Transparent,
                errorContainerColor = BackgroundGray,
                errorBorderColor = Color.Red
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
            },
            placeholder = { Text("Email Address", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = emailError != null,
            supportingText = { if (emailError != null) Text(emailError!!, color = Color.Red) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BackgroundGray,
                unfocusedContainerColor = BackgroundGray,
                focusedBorderColor = NavyBlue,
                unfocusedBorderColor = Color.Transparent,
                errorContainerColor = BackgroundGray,
                errorBorderColor = Color.Red
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
            },
            placeholder = { Text("Password", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = passwordError != null,
            supportingText = { if (passwordError != null) Text(passwordError!!, color = Color.Red) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BackgroundGray,
                unfocusedContainerColor = BackgroundGray,
                focusedBorderColor = NavyBlue,
                unfocusedBorderColor = Color.Transparent,
                errorContainerColor = BackgroundGray,
                errorBorderColor = Color.Red
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
                confirmPasswordError = null
            },
            placeholder = { Text("Confirm Password", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = confirmPasswordError != null,
            supportingText = { if (confirmPasswordError != null) Text(confirmPasswordError!!, color = Color.Red) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BackgroundGray,
                unfocusedContainerColor = BackgroundGray,
                focusedBorderColor = NavyBlue,
                unfocusedBorderColor = Color.Transparent,
                errorContainerColor = BackgroundGray,
                errorBorderColor = Color.Red
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
                var isValid = true

                if (fullName.isBlank()) {
                    fullNameError = "Full Name is required"
                    isValid = false
                }

                if (email.isBlank()) {
                    emailError = "Email is required"
                    isValid = false
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailError = "Invalid email format"
                    isValid = false
                }

                if (password.isBlank()) {
                    passwordError = "Password is required"
                    isValid = false
                } else if (password.length < 6) {
                    passwordError = "Password must be at least 6 characters"
                    isValid = false
                }

                if (confirmPassword.isBlank()) {
                    confirmPasswordError = "Please confirm your password"
                    isValid = false
                } else if (confirmPassword != password) {
                    confirmPasswordError = "Passwords do not match"
                    isValid = false
                }

                if (isValid) {
                    onRegisterClick(fullName, email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
        ) {
            Text(
                text = "CREATE PRO ACCOUNT",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        val loginText = buildAnnotatedString {
            withStyle(style = SpanStyle(color = NavyBlue)) {
                append("Already have an account? ")
            }
            withStyle(style = SpanStyle(color = Teal, fontWeight = FontWeight.Bold)) {
                append("Login.")
            }
        }
        Text(
            text = loginText,
            fontSize = 14.sp,
            modifier = Modifier.clickable { onLoginClick() }
        )

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
