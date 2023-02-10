package com.armutyus.ninova.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.CHANGE_EMAIL
import com.armutyus.ninova.constants.Constants.CHANGE_PASSWORD
import com.armutyus.ninova.constants.Constants.FORGOT_PASSWORD
import com.armutyus.ninova.constants.Constants.LOGIN_INTENT
import com.armutyus.ninova.constants.Constants.MAIN_INTENT
import com.armutyus.ninova.constants.Constants.REGISTER
import com.armutyus.ninova.constants.Constants.SETTINGS_ACTION_KEY
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.ActivityRegisterBinding
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    @Named(LOGIN_INTENT)
    @Inject
    lateinit var loginIntent: Intent

    @Named(MAIN_INTENT)
    @Inject
    lateinit var mainIntent: Intent

    private val auth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when (intent.getStringExtra(SETTINGS_ACTION_KEY)) {
            REGISTER -> binding.registerUserLayout.visibility = View.VISIBLE
            CHANGE_EMAIL -> binding.changeEmailLayout.visibility = View.VISIBLE
            CHANGE_PASSWORD -> binding.changePasswordLayout.visibility = View.VISIBLE
            FORGOT_PASSWORD -> binding.forgotPasswordLayout.visibility = View.VISIBLE
        }

        binding.registerButton.setOnClickListener {
            confirmRegisterInfo()
        }

        binding.changeEmailButton.setOnClickListener {
            password = binding.reAuthPasswordText.text.toString().trim()
            reAuthUserAndChangeEmail()
        }

        binding.changePasswordButton.setOnClickListener {
            password = binding.userCurrentPasswordText.text.toString().trim()
            reAuthUserAndChangePassword()
        }

        binding.sendResetPasswordButton.setOnClickListener {
            sendPasswordEmail()
        }
    }

    private var email = ""
    private var password = ""

    private fun reAuthUserAndChangeEmail() {
        email = binding.changeEmailText.text.toString().trim()
        if (email.isEmpty()) {
            Toast.makeText(this, R.string.wrong_information, Toast.LENGTH_LONG)
                .show()
        } else {
            if (password.isNotEmpty()) {
                val credential =
                    EmailAuthProvider.getCredential(auth.currentUser!!.email!!, password)
                viewModel.reAuthUserAndChangeEmail(credential, email) { response ->
                    when (response) {
                        is Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is Response.Success -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                this,
                                R.string.successful_email_update,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        is Response.Failure -> {
                            Log.e(
                                "RegisterActivity",
                                "ReAuth & Change E-mail Error: " + response.errorMessage
                            )
                            Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG).show()
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            } else {
                Toast.makeText(this, R.string.wrong_information, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun reAuthUserAndChangePassword() {
        val newPassword = binding.newPasswordText.text.toString().trim()
        val confirmNewPassword = binding.confirmNewPasswordText.text.toString().trim()

        if (newPassword.isEmpty() || confirmNewPassword.isEmpty() || newPassword != confirmNewPassword) {
            Toast.makeText(this, R.string.wrong_information, Toast.LENGTH_LONG)
                .show()
        } else {
            if (password.isNotEmpty()) {
                val credential =
                    EmailAuthProvider.getCredential(auth.currentUser!!.email!!, password)
                viewModel.reAuthUserAndChangePassword(credential, newPassword) { response ->
                    when (response) {
                        is Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is Response.Success -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                this,
                                R.string.password_changed_login,
                                Toast.LENGTH_LONG
                            ).show()
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            signOut()
                        }
                        is Response.Failure -> {
                            Log.e(
                                "RegisterActivity",
                                "ReAuth & Change Password Error: " + response.errorMessage
                            )
                            Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG).show()
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            } else {
                Toast.makeText(this, R.string.wrong_information, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun confirmRegisterInfo() {
        email = binding.registerEmailText.text.toString().trim()
        password = binding.registerPasswordText.text.toString().trim()
        val confirmPassword = binding.registerConfirmPasswordText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || password != confirmPassword) {
            Toast.makeText(this, R.string.wrong_information, Toast.LENGTH_LONG)
                .show()
        } else {
            val credential = EmailAuthProvider.getCredential(email, password)
            registerAnonymousUser(credential)
        }
    }

    private fun registerAnonymousUser(credential: AuthCredential) {
        viewModel.registerAnonymousUser(credential) { response ->
            when (response) {
                is Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Response.Success -> {
                    createUserProfile()
                }
                is Response.Failure -> {
                    Log.e("RegisterActivity", "AnonymousSignUp Error: " + response.errorMessage)
                    Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun createUserProfile() {
        viewModel.createUser { response ->
            when (response) {
                is Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Response.Success -> {
                    mainIntent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                                or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                or Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                    goToMainActivity()
                    binding.progressBar.visibility = View.GONE
                }
                is Response.Failure -> {
                    Log.e("RegisterActivity", "CreateProfile Error: " + response.errorMessage)
                    Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun sendPasswordEmail() {
        email = binding.forgotPasswordEmailText.text.toString().trim()

        if (email.isNotEmpty()) {
            viewModel.sendPasswordEmail(email) { response ->
                when (response) {
                    is Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Response.Success -> {
                        Toast.makeText(this, R.string.reset_email_sent, Toast.LENGTH_LONG)
                            .show()
                        binding.progressBar.visibility = View.GONE
                        goToLogInActivity()
                    }
                    is Response.Failure -> {
                        Log.e(
                            "RegisterActivity",
                            "SendPassword Error: " + response.errorMessage
                        )
                        Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG)
                            .show()
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        } else {
            Toast.makeText(this, R.string.wrong_information, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun signOut() {
        viewModel.signOut { response ->
            when (response) {
                is Response.Loading -> Log.i("RegisterActivity", "Loading")
                is Response.Success -> goToLogInActivity()
                is Response.Failure -> {
                    Log.e("RegisterActivity", "SignOut Error: " + response.errorMessage)
                    Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun goToMainActivity() {
        finishAffinity()
        startActivity(mainIntent)
        finish()
    }

    private fun goToLogInActivity() {
        finishAffinity()
        startActivity(loginIntent)
        finish()
    }

}