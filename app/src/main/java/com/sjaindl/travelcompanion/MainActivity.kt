package com.sjaindl.travelcompanion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.credentials.CredentialManager
import com.facebook.CallbackManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.sjaindl.travelcompanion.auth.AuthenticationViewModel
import com.sjaindl.travelcompanion.databinding.ActivityMainBinding
import com.sjaindl.travelcompanion.repository.DataRepository
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

data class OpenAddPlan(val open: Boolean, val destination: String)

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val tag = "MainActivity"

    private lateinit var auth: FirebaseAuth

    @Inject
    lateinit var credentialManager: CredentialManager

    @Inject
    lateinit var callbackManager: CallbackManager

    @Inject
    lateinit var dataRepository: DataRepository

    private val authenticationViewModel by viewModels<AuthenticationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        credentialManager.toString()

        auth = Firebase.auth

        if (auth.currentUser != null) {
            authenticationViewModel.preloadPlans()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContent()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setContent(deeplinkIntent = intent)
    }

    @OptIn(ExperimentalComposeUiApi::class)
    private fun setContent(deeplinkIntent: Intent? = null) {
        binding.composeView.setContent {
            val context = LocalContext.current

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

            TravelCompanionTheme {
                Surface(
                    modifier = Modifier
                        .semantics {
                            testTagsAsResourceId = true
                        }
                ) {
                    MainContainer(
                        signInWithGoogle = {
                            authenticationViewModel.signInWithGoogle(
                                credentialManager = credentialManager,
                                context = context,
                                successAction = authenticationAction ?: { },
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
                                successAction = authenticationAction ?: { },
                                onFailure = { exception ->
                                    handleFailure(exception = exception)
                                },
                                onCompleted = {
                                    authenticationAction = null
                                }
                            )
                        },
                        signInWithMail = { email, password ->
                            authenticationViewModel.signInWithMail(
                                email = email,
                                password = password,
                                onFailure = { exception ->
                                    handleFailure(exception = exception)
                                },
                                onCompleted = {
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
                                    authenticationAction = null
                                }
                            )
                        },
                        openAuthentication = authenticationAction != null,
                        authenticationOpened = {
                            authenticationAction = null
                        },
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
                        deeplinkIntent = deeplinkIntent,
                    )
                }
            }
        }
    }

    private fun handleFailure(exception: Exception) {
        Toast.makeText(
            this,
            exception.message ?: getString(R.string.unknown_error),
            Toast.LENGTH_SHORT,
        ).show()

        Timber.tag(tag = tag).e(t = exception, message = "Sign-in exception: $exception")
    }
}
