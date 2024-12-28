package com.sjaindl.travelcompanion.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun SignInWithMailHomeScreen(
    signIn: (email: String) -> Unit,
    signUp: (email: String) -> Unit,
) {
    var email by remember {
        mutableStateOf("")
    }

    val colors = ButtonDefaults.buttonColors(
        containerColor = colorResource(id = R.color.colorMain),
    )

    Column(
        modifier = Modifier
            .padding(all = 32.dp),
        horizontalAlignment = Alignment.End
    ) {

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(id = R.string.enter_mail)) },
            isError = !email.isValidMail(),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            FilledTonalButton(
                onClick = {
                    signIn(email)
                },
                enabled = email.isValidMail(),
                colors = colors,
            ) {
                Text(text = stringResource(id = R.string.signIn))
            }

            Spacer(modifier = Modifier.weight(1f))

            FilledTonalButton(
                onClick = {
                    signUp(email)
                },
                enabled = email.isValidMail(),
                colors = colors,
            ) {
                Text(text = stringResource(id = R.string.signUp))
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview
@Composable
fun SignInWithMailLandingScreenPreview() {
    TravelCompanionTheme {
        SignInWithMailHomeScreen(
            signIn = { _ -> },
            signUp = { _ -> },
        )
    }
}
