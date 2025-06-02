package com.sjaindl.travelcompanion.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import kotlinx.serialization.Serializable

@Serializable
data object SignInChooser : NavKey

@Serializable
data object MailSignInHome : NavKey

@Serializable
data class MailSignIn(val email: String) : NavKey

@Serializable
data class MailSignUp(val email: String) : NavKey

@Composable
fun EntryProviderBuilder<Any>.AuthNavigation(
    backStack: SnapshotStateList<Any>,
    signInWithGoogle: () -> Unit,
    signInWithFacebook: () -> Unit,
    signInWithMail: (email: String, password: String) -> Unit,
    signUpWithMail: (email: String, password: String, name: String) -> Unit,
) {
    entry<SignInChooser> {
        SignInChooserScreen(
            signInWithGoogle = signInWithGoogle,
            signInWithFacebook = signInWithFacebook,
            signInWithMail = {
                backStack.add(MailSignInHome)
            },
        )
    }

    entry<MailSignInHome> {
        SignInWithMailHomeScreen(
            signIn = { email ->
                backStack.add(MailSignIn(email = email))
            },
            signUp = { email ->
                backStack.add(MailSignUp(email = email))
            },
        )
    }

    entry<MailSignIn> {
        SignInWithMailScreen(email = it.email) { _, password ->
            signInWithMail(it.email, password)
        }
    }

    entry<MailSignUp> {
        SignUpWithMailScreen(email = it.email) { _, password, name ->
            signUpWithMail(it.email, password, name)
        }
    }
}
