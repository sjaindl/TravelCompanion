package com.sjaindl.travelcompanion.auth

import android.content.Context
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.sjaindl.travelcompanion.BuildConfig
import com.sjaindl.travelcompanion.util.FireStoreUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStoreUtils: FireStoreUtils,
) : ViewModel() {

    private val tag = "AuthenticationViewModel"

    fun signInWithFacebook(
        activityResultRegistryOwner: ActivityResultRegistryOwner,
        callbackManager: CallbackManager,
        successAction: () -> Unit,
        onFailure: (Exception) -> Unit,
        onCompleted: () -> Unit,
    ) {
        LoginManager.getInstance().registerCallback(
            callbackManager = callbackManager,
            callback = object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    Timber.tag(tag = tag).d(message = "facebook onSuccess: $result")
                    handleFacebookAccessToken(token = result.accessToken, successAction = successAction, onFailure = onFailure) {
                        onCompleted()
                    }
                }

                override fun onCancel() {
                    Timber.tag(tag = tag).d(message = "Facebook onCancel")
                    onCompleted()
                }

                override fun onError(error: FacebookException) {
                    Timber.tag(tag = tag).d(t = error, message = "Facebook onError")
                    onFailure(error)
                }
            },
        )

        LoginManager.getInstance().logInWithReadPermissions(
            activityResultRegistryOwner = activityResultRegistryOwner,
            callbackManager = callbackManager,
            permissions = listOf("email", "public_profile"),
        )
    }

    fun signInWithGoogle(
        credentialManager: CredentialManager,
        context: Context,
        successAction: () -> Unit,
        onAuthenticated: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        val googleIdOption = GetSignInWithGoogleOption.Builder(serverClientId = BuildConfig.googleServerClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewModelScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = context,
                    request = request,

                    )
                handleGoogleSignIn(result = result, successAction = successAction, onFailure = onFailure) {
                    onAuthenticated()
                }
            } catch (exception: GetCredentialException) {
                onFailure(exception)
            }
        }
    }

    fun signInWithMail(email: String, password: String, onFailure: (Exception) -> Unit, onCompleted: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Timber.tag(tag = tag).d("Signed in user ${auth.currentUser?.displayName}")
            } else {
                task.exception?.let { exception ->
                    Timber.tag(tag = tag).e(t = exception, message = "signInWithCredential:failure")
                    onFailure(exception)
                }
            }
            onCompleted()
        }
    }

    fun signUpWithMail(email: String, password: String, name: String, onFailure: (Exception) -> Unit, onCompleted: () -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Timber.tag(tag = tag).d("Signed up user $email")

                val user = auth.currentUser
                requireNotNull(user)

                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }

                user.updateProfile(profileUpdates).addOnCompleteListener { setDisplayNameTask ->
                    if (setDisplayNameTask.isSuccessful) {
                        Timber.tag(tag = tag).d("Set display name ${auth.currentUser?.displayName}")
                    } else {
                        setDisplayNameTask.exception?.let { exception ->
                            Timber.tag(tag = tag).e(t = exception, message = "setDisplayNameTask:failure")
                            onFailure(exception)
                        }
                    }
                    onCompleted()
                }
            } else {
                task.exception?.let { exception ->
                    Timber.tag(tag = tag).e(t = exception, message = "signInWithCredential:failure")
                    onFailure(exception)
                }
                onCompleted()
            }
        }
    }

    fun preloadPlans() {
        fireStoreUtils.loadPlans(
            onLoaded = {
                Timber.d(message = "loaded plan: ${it?.name}")
            },
            onError = {
                Timber.d(message = "plan loading error: ${it.message}")
            },
            onInfo = {
                Timber.d(message = "plan loading info: $it")
            },
        )
    }

    private fun handleFacebookAccessToken(
        token: AccessToken,
        successAction: () -> Unit,
        onFailure: (Exception) -> Unit,
        onCompleted: () -> Unit,
    ) {
        Timber.tag(tag = tag).d(message = "handleFacebookAccessToken: $token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.tag(tag = tag).d("Signed in user ${auth.currentUser?.displayName}")

                preloadPlans()
                successAction()
            } else {
                task.exception?.let { exception ->
                    Timber.tag(tag = tag).e(t = task.exception, message = "signInWithCredential:failure")
                    onFailure(exception)
                }
            }
            onCompleted()
        }
    }

    private fun handleGoogleSignIn(
        result: GetCredentialResponse,
        successAction: () -> Unit,
        onFailure: (Exception) -> Unit,
        completed: () -> Unit,
    ) {
        when (val credential = result.credential) {
            is PublicKeyCredential -> {
                // Share responseJson such as a GetCredentialResponse on your server to validate and authenticate
                val responseJson = credential.authenticationResponseJson
                Timber.tag(tag = tag).d(message = "Received PublicKeyCredential: $responseJson")
            }

            is PasswordCredential -> {
                // Send ID and password to your server to validate and authenticate.
                val username = credential.id
                val password = credential.password
                Timber.tag(tag = tag).d(message = "Received PasswordCredential: $username / $password")
            }

            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(data = credential.data)
                        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                        auth.signInWithCredential(firebaseCredential).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Timber.tag(tag = tag).d("Signed in user ${auth.currentUser?.displayName}")

                                preloadPlans()
                                successAction()
                            } else {
                                task.exception?.let { exception ->
                                    Timber.tag(tag = tag).e(t = exception, message = "signInWithCredential failure")
                                    onFailure(exception)
                                }
                            }
                            completed()
                        }

                    } catch (exception: GoogleIdTokenParsingException) {
                        Timber.tag(tag = tag).e(t = exception, message = "Received an invalid google id token response")
                        onFailure(exception)
                        completed()
                    }
                } else {
                    Timber.tag(tag = tag).e(message = "Unexpected type of CustomCredential")
                    completed()
                }
            }

            else -> {
                Timber.tag(tag = tag).e(message = "Unexpected type of credential")
                completed()
            }
        }
    }
}
