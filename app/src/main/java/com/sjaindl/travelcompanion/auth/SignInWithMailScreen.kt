package com.sjaindl.travelcompanion.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun SignInWithMailScreen(
    email: String,
    onProceed: (email: String, password: String) -> Unit,
) {
    var password by remember {
        mutableStateOf("")
    }

    val colors = ButtonDefaults.buttonColors(
        containerColor = colorResource(id = R.color.colorMain),
    )

    Column(
        modifier = Modifier
            .padding(all = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.End,
    ) {

        OutlinedTextField(
            value = email,
            onValueChange = { },
            enabled = false,
            label = {
                Text(text = stringResource(id = R.string.email))
            },
            isError = !email.isValidMail()
        )

        OutlinedTextField(
            value = password,
            label = {
                Text(text = stringResource(id = R.string.enter_password))
            },
            onValueChange = {
                password = it
            },
            isError = !password.isValidPassword(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
        )

        FilledTonalButton(
            onClick = {
                onProceed(email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            enabled = email.isValidMail() && password.isValidPassword(),
            colors = colors,
        ) {
            Text(text = stringResource(id = R.string.signIn))
        }
    }
}

@Preview
@Composable
fun SignInWithMailScreenPreview() {
    TravelCompanionTheme {
        SignInWithMailScreen(
            email = "demo@test.com",
            onProceed = { _, _ -> },
        )
    }
}
