package com.armutyus.ninova.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants
import com.armutyus.ninova.constants.Constants.MAIN_INTENT
import com.armutyus.ninova.constants.Constants.REGISTER_INTENT
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.constants.Util.Companion.checkAndApplyTheme
import com.armutyus.ninova.constants.Util.Companion.fadeIn
import com.armutyus.ninova.databinding.ActivityLoginBinding
import com.armutyus.ninova.databinding.RegisterUserBottomSheetBinding
import com.armutyus.ninova.ui.settings.SettingsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Named(MAIN_INTENT)
    @Inject
    lateinit var mainIntent: Intent

    @Named(REGISTER_INTENT)
    @Inject
    lateinit var registerIntent: Intent

    private lateinit var binding: ActivityLoginBinding
    private lateinit var bottomSheetBinding: RegisterUserBottomSheetBinding
    private val sharedPreferences: SharedPreferences
        get() = this.getSharedPreferences(Constants.MAIN_SHARED_PREF, Context.MODE_PRIVATE)

    private val themePreferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(this)

    private val loginViewModel by viewModels<LoginViewModel>()
    private val settingsViewModel by viewModels<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setWelcomeTextView()
        binding.loginLayout.fadeIn(2000)

        binding.forgotPasswordText.setOnClickListener {
            goToForgotPasswordPage()
        }

        binding.login.setOnClickListener {
            loginUser()
        }

        binding.register.setOnClickListener {
            showRegisterDialog()
        }

        binding.withoutRegister.setOnClickListener {
            anonymousSignIn()
        }

    }

    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()
        checkAndApplyTheme(themePreferences, theme)
        return theme
    }

    private fun setWelcomeTextView() {
        val height = resources.displayMetrics.heightPixels / resources.displayMetrics.density

        if (height < 700.0f) {
            binding.smallWelcomeTextView.visibility = View.VISIBLE
            binding.smallWelcomeTextView.fadeIn(1500)
        } else {
            binding.welcomeTextView.visibility = View.VISIBLE
            binding.welcomeTextView.fadeIn(1500)
        }
    }

    private var email = ""
    private var password = ""

    private fun loginUser() {
        email = binding.userEmailText.text.toString().trim()
        password = binding.userPasswordText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.wrong_information, Toast.LENGTH_LONG)
                .show()
        } else {
            loginViewModel.signInUser(email, password) { response ->
                when (response) {
                    is Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Response.Success -> {
                        goToMainActivity()
                        binding.progressBar.visibility = View.GONE
                    }

                    is Response.Failure -> {
                        Log.e("LoginActivity", "SignIn Error: " + response.errorMessage)
                        Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG)
                            .show()
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun anonymousSignIn() {
        loginViewModel.signInAnonymously { response ->
            when (response) {
                is Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Response.Success -> {
                    createUserProfile()
                }

                is Response.Failure -> {
                    Log.e("LoginActivity", "AnonymousSignIn Error: " + response.errorMessage)
                    Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun showRegisterDialog() {
        val dialog = BottomSheetDialog(this)
        bottomSheetBinding = RegisterUserBottomSheetBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)
        val signUpButton = dialog.findViewById<MaterialButton>(R.id.signUpButton)

        signUpButton?.setOnClickListener {
            registerUser()
        }

        dialog.show()
    }

    private fun registerUser() {
        email = bottomSheetBinding.registerEmailText.text.toString().trim()
        password = bottomSheetBinding.registerPasswordText.text.toString().trim()
        val confirmPassword = bottomSheetBinding.registerConfirmPasswordText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || password != confirmPassword) {
            Toast.makeText(this, R.string.wrong_information, Toast.LENGTH_LONG)
                .show()
        } else {
            signUpUser()
        }

    }

    private fun signUpUser() {
        loginViewModel.signUpUser(email, password) { response ->
            when (response) {
                is Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Response.Success -> {
                    createUserProfile()
                }

                is Response.Failure -> {
                    Log.e("LoginActivity", "SignUp Error: " + response.errorMessage)
                    Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun createUserProfile() {
        loginViewModel.createUser { response ->
            when (response) {
                is Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Response.Success -> {
                    goToMainActivity()
                    binding.progressBar.visibility = View.GONE
                }

                is Response.Failure -> {
                    Log.e("LoginActivity", "CreateProfile Error: " + response.errorMessage)
                    Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                    binding.progressBar.visibility = View.GONE
                }
            }

        }
    }

    private fun goToMainActivity() {
        with(sharedPreferences.edit()) {
            putBoolean("first_time", true).apply()
        }
        clearDatabase()
        mainIntent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    or Intent.FLAG_ACTIVITY_NEW_TASK
        )
        finishAffinity()
        startActivity(mainIntent)
        finish()
    }

    private fun goToForgotPasswordPage() {
        registerIntent.putExtra("action", "forgot_password")
        startActivity(registerIntent)
    }

    private fun clearDatabase() {
        settingsViewModel.clearDatabase()
    }

}