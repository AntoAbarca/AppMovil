package com.duoc.ayanad.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.ayanad.viewmodel.LoginViewModel

@Composable
fun Login(
    viewModel: LoginViewModel,
    onLoginExitoso: () -> Unit = {},
    onRecuperarPassword: () -> Unit = {},
    onCrearCuenta: () -> Unit = {}
) {
    val form by viewModel.form.collectAsState()
    val errors by viewModel.errors.collectAsState()
    val loginExitoso by viewModel.loginExitoso.collectAsState()

    LaunchedEffect(loginExitoso) {
        if (loginExitoso) onLoginExitoso()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        // Franja superior azul con el logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(Color(0xFF2962FF)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(shape = RoundedCornerShape(24.dp), color = Color.White) {
                    // Reemplazar por Image(painterResource(R.drawable.logo_tecmundo), ...)
                    Text(
                        text = "TECMUNDO",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Productores y aplicadores", color = Color.White)
            }
        }

        // Formulario
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = form.usuario,
                onValueChange = { viewModel.onUsuarioChange(it) },
                label = { Text("Usuario") },
                isError = errors.usuario != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            if (errors.usuario != null) {
                Text(text = errors.usuario!!, color = Color.Red, fontSize = 12.sp)
            }

            OutlinedTextField(
                value = form.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = errors.password != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
            if (errors.password != null) {
                Text(text = errors.password!!, color = Color.Red, fontSize = 12.sp)
            }

            Button(
                onClick = { viewModel.submit() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF))
            ) {
                Text("Login", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("o")
            TextButton(onClick = onRecuperarPassword) {
                Text("Recuperar contraseña", color = Color(0xFF2962FF))
            }
            TextButton(onClick = onCrearCuenta) {
                Text("Crear una cuenta", color = Color(0xFF2962FF))
            }
        }
    }
}