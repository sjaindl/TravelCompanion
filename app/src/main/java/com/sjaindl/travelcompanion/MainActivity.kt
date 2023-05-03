package com.sjaindl.travelcompanion

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.sjaindl.travelcompanion.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val tag = "MainActivity"

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result: FirebaseAuthUIAuthenticationResult? ->
        val response = result?.idpResponse

        if (result?.resultCode == RESULT_OK) {
            // Successfully signed in
            // update UI
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                showMessage(getString(R.string.sign_in_cancelled))
                return@registerForActivityResult
            }

            if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                showMessage(getString(R.string.offline))
                return@registerForActivityResult
            }

            showMessage(getString(R.string.unknown_error))
            Timber.tag(tag).e(response.error, "Sign-in error: ${response.error}");
        }
    }

    private fun showMessage(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.imageViewUserIcon.setOnClickListener {
            startSignIn()
        }
    }

    private fun startSignIn() {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            // already signed in
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

            signInLauncher.launch(signInIntent)
        }
    }
}
