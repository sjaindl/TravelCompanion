package com.sjaindl.travelcompanion

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.credentials.CredentialManager
import com.facebook.CallbackManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.sjaindl.travelcompanion.auth.AuthenticationViewModel
import com.sjaindl.travelcompanion.auth.MailSignInContainer
import com.sjaindl.travelcompanion.auth.SignInChooserScreen
import com.sjaindl.travelcompanion.databinding.ActivityMainBinding
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import timber.log.Timber
import com.sjaindl.travelcompanion.shared.R as SharedR

data class OpenAddPlan(val open: Boolean, val destination: String)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val tag = "MainActivity"

    private lateinit var auth: FirebaseAuth

    private val credentialManager by lazy {
        CredentialManager.create(this)
    }

    private val callbackManager by lazy {
        CallbackManager.Factory.create()
    }

    private val authenticationViewModel by viewModels<AuthenticationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        auth = Firebase.auth

        if (auth.currentUser != null) {
            authenticationViewModel.preloadPlans()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContent()
    }

    private fun setContent() {
        binding.composeView.setContent {
            var authenticationAction: (() -> Unit)? by remember {
                mutableStateOf(null)
            }

            var openProfile by remember {
                mutableStateOf(false)
            }

            var openPlan by remember {
                mutableStateOf(false)
            }

            var openAddPlan by remember {
                mutableStateOf(OpenAddPlan(open = false, destination = ""))
            }

            var signInWithMail by remember {
                mutableStateOf(false)
            }

            val successAction = authenticationAction
            if (successAction != null) {
                val context = LocalContext.current

                TravelCompanionTheme {

                    if (signInWithMail) {
                        MailSignInContainer(
                            signInWithMail = { email, password ->
                                authenticationViewModel.signInWithMail(
                                    email = email,
                                    password = password,
                                    onFailure = { exception ->
                                        handleFailure(exception = exception)
                                    },
                                    onCompleted = {
                                        signInWithMail = false
                                        authenticationAction = null
                                    }
                                )
                            },
                            signUpWithMail = { email, password, name ->
                                authenticationViewModel.signUpWithMail(
                                    email = email,
                                    password = password,
                                    name = name,
                                    onFailure = { exception ->
                                        handleFailure(exception = exception)
                                    },
                                    onCompleted = {
                                        signInWithMail = false
                                        authenticationAction = null
                                    }
                                )
                            }
                        )
                    } else {
                        SignInChooserScreen(
                            signInWithGoogle = {
                                authenticationViewModel.signInWithGoogle(
                                    credentialManager = credentialManager,
                                    context = context,
                                    successAction = successAction,
                                    onAuthenticated = {
                                        authenticationAction = null
                                    },
                                    onFailure = { exception ->
                                        handleFailure(exception = exception)
                                    },
                                )
                            },
                            signInWithFacebook = {
                                authenticationViewModel.signInWithFacebook(
                                    activityResultRegistryOwner = this,
                                    callbackManager = callbackManager,
                                    successAction = successAction,
                                    onFailure = { exception ->
                                        handleFailure(exception = exception)
                                    },
                                    onCompleted = {
                                        authenticationAction = null
                                    }
                                )
                            },
                            signInWithMail = {
                                signInWithMail = true
                            },
                        )
                    }
                }
            } else {
                TravelCompanionTheme {
                    MainContainer(
                        onClickedProfile = {
                            if (auth.currentUser != null) {
                                // already signed in
                                openProfile = true
                            } else {
                                authenticationAction = {
                                    Timber.tag(tag = tag).d(message = "Successfully signed in")
                                }
                            }
                        },
                        openProfile = openProfile,
                        profileOpened = {
                            openProfile = false
                        },
                        onAuthenticateAndOpenPlan = {
                            if (auth.currentUser != null) {
                                // already signed in
                                openPlan = true
                            } else {
                                authenticationAction = {
                                    Timber.tag(tag = tag).d(message = "Successfully signed in")
                                    openPlan = true
                                }
                            }
                        },
                        onAuthenticateAndOpenAddPlan = { destination ->
                            if (auth.currentUser != null) {
                                // already signed in
                                openAddPlan = OpenAddPlan(open = true, destination = destination)
                            } else {
                                authenticationAction = {
                                    Timber.tag(tag = tag).d(message = "Successfully signed in")
                                    openAddPlan = OpenAddPlan(open = true, destination = destination)
                                }
                            }
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
        }
    }

    private fun handleFailure(exception: Exception) {
        Toast.makeText(
            this,
            exception.message ?: getString(SharedR.string.unknown_error),
            Toast.LENGTH_SHORT,
        ).show()

        Timber.tag(tag = tag).e(t = exception, message = "Sign-in exception: $exception")
    }
}
