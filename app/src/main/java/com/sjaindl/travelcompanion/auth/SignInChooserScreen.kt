package com.sjaindl.travelcompanion.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun SignInChooserScreen(
    signInWithGoogle: () -> Unit,
    signInWithFacebook: () -> Unit,
    signInWithMail: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        SignInProviderButton(
            containerColor = Color(red = 219, green = 68, blue = 55),
            text = "Sign in with Google",
            onClick = signInWithGoogle,
        ) {
            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Sign in with Google",
                modifier = Modifier.size(24.dp),
            )
        }

        SignInProviderButton(
            containerColor = Color(red = 66, green = 103, blue = 178),
            text = "Sign in with Facebook",
            onClick = signInWithFacebook,
        ) {
            Image(
                imageVector = Icons.Default.Facebook,
                contentDescription = "Sign in with Facebook",
                modifier = Modifier.size(24.dp),
            )
        }

        SignInProviderButton(
            containerColor = colorScheme.primary,
            text = "Sign in with email",
            onClick = signInWithMail,
        ) {
            Image(
                imageVector = Icons.Default.Email,
                contentDescription = "Sign in with email",
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun SignInProviderButton(
    containerColor: Color,
    text: String,
    onClick: () -> Unit,
    image: @Composable() (() -> Unit)
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
        ),
        onClick = onClick,
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            image()
            Spacer(modifier = Modifier.weight(0.1f))
            Text(text = text)
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview
@Composable
fun SignInChooserScreenPreview() {
    TravelCompanionTheme {
        SignInChooserScreen(
            signInWithGoogle = {},
            signInWithFacebook = {},
            signInWithMail = {},
        )
    }
}
