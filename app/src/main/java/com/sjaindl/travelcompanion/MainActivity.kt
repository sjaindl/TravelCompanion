package com.sjaindl.travelcompanion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.sjaindl.travelcompanion.databinding.ActivityMainBinding
import timber.log.Timber
import kotlin.random.Random
import com.sjaindl.travelcompanion.shared.R as SharedR

data class OpenAddPlan(val open: Boolean, val destination: String)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val tag = "MainActivity"

    private fun showMessage(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.composeView.setContent {
            var openProfile by remember {
                mutableStateOf(false)
            }

            var openPlan by remember {
                mutableStateOf(false)
            }

            var openAddPlan by remember {
                mutableStateOf(OpenAddPlan(open = false, destination = ""))
            }

            MainContainer(
                onClickedProfile = {
                    openProfile = startSignIn {
                        Timber.d(tag, "Successfully signed in")
                    }
                },
                openProfile = openProfile,
                profileOpened = {
                    openProfile = false
                },
                onAuthenticateAndOpenPlan = {
                    openPlan = startSignIn {
                        Timber.d(tag, "Successfully signed in")
                        openPlan = true
                    }
                },
                onAuthenticateAndOpenAddPlan = { destination ->
                    val isOpen = startSignIn {
                        Timber.d(tag, "Successfully signed in")
                        openAddPlan = OpenAddPlan(open = true, destination = destination)
                    }

                    openAddPlan = OpenAddPlan(open = isOpen, destination = destination)
                },
                openPlan = openPlan,
                openAddPlan = if (openAddPlan.open) openAddPlan.destination else null,
                openedPlan = {
                    openPlan = false
                },
                openedAddPlan = {
                    openAddPlan = OpenAddPlan(open = false, destination = "")
                },
            )
        }
    }

    private fun provideSignInLauncher(successAction: () -> Unit): ActivityResultLauncher<Intent> {
        return activityResultRegistry.register(
            generateUniqueActivityResultsKey(),
            FirebaseAuthUIActivityResultContract(),
        ) { result: FirebaseAuthUIAuthenticationResult? ->
            val response = result?.idpResponse

            if (result?.resultCode == RESULT_OK) {
                // Successfully signed in
                successAction()
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showMessage(getString(SharedR.string.sign_in_cancelled))
                    return@register
                }

                if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    showMessage(getString(SharedR.string.offline))
                    return@register
                }

                showMessage(getString(SharedR.string.unknown_error))
                Timber.tag(tag).e(response.error, "Sign-in error: ${response.error}")
            }
        }
    }

    private fun startSignIn(successAction: () -> Unit): Boolean {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            // already signed in
            return true
        } else {
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.AppTheme)
                .setAvailableProviders(
                    listOf(
                        AuthUI.IdpConfig.GoogleBuilder().build(),
                        AuthUI.IdpConfig.FacebookBuilder().build(),
                        AuthUI.IdpConfig.EmailBuilder().build(),
                    ),
                )
                .build()

            provideSignInLauncher(successAction = successAction).launch(signInIntent)

            return false
        }
    }

    private fun generateUniqueActivityResultsKey(): String {
        return "launcher_${Random.nextInt()}"
    }
}
