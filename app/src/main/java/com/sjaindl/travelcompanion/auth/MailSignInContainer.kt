package com.sjaindl.travelcompanion.auth

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@Composable
fun MailSignInContainer(
    signInWithMail: (email: String, password: String) -> Unit,
    signUpWithMail: (email: String, password: String, name: String) -> Unit,
) {
    val navController = rememberNavController()

    MailSignInNavHost(
        navController = navController,
        modifier = Modifier
            .fillMaxSize(),
        signInWithMail = signInWithMail,
        signUpWithMail = signUpWithMail,
    )
}

@Preview
@Composable
fun MailSignInContainerPreview() {
    MailSignInContainer(
        signInWithMail = { _, _ -> },
        signUpWithMail = { _, _, _ -> },
    )
}
