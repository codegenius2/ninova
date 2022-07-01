package com.armutyus.ninova.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.MAIN_INTENT
import com.armutyus.ninova.constants.Constants.REGISTER_INTENT
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.ActivityLoginBinding
import com.armutyus.ninova.databinding.RegisterUserBottomSheetBinding
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
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    private var email = ""
    private var password = ""

    private fun loginUser() {
        email = binding.userEmailText.text.toString().trim()
        password = binding.userPasswordText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your information correctly!", Toast.LENGTH_LONG)
                .show()
        } else {
            viewModel.signInUser(email, password).observe(this) { response ->
                when (response) {
                    is Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Response.Success -> {
                        goToMainActivity()
                        binding.progressBar.visibility = View.GONE
                    }
                    is Response.Failure -> {
                        println("SignIn Error: " + response.errorMessage)
                        Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG)
                            .show()
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun anonymousSignIn() {
        viewModel.signInAnonymously().observe(this) { response ->
            when (response) {
                is Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Response.Success -> {
                    createUserProfile()
                    goToMainActivity()
                    binding.progressBar.visibility = View.GONE
                }
                is Response.Failure -> {
                    println("SignIn Error: " + response.errorMessage)
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
            Toast.makeText(this, "Please enter your information correctly!", Toast.LENGTH_LONG)
                .show()
        } else {
            signUpUser()
        }

    }

    private fun signUpUser() {

        viewModel.signUpUser(email, password).observe(this) { response ->
            when (response) {
                is Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Response.Success -> {
                    createUserProfile()
                    binding.progressBar.visibility = View.GONE
                }
                is Response.Failure -> {
                    println("SignUp Error: " + response.errorMessage)
                    Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

    }

    private fun createUserProfile() {

        viewModel.createUser().observe(this) { response ->
            when (response) {
                is Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Response.Success -> {
                    goToMainActivity()
                    binding.progressBar.visibility = View.GONE
                }
                is Response.Failure -> {
                    println("Create Error: " + response.errorMessage)
                    Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun goToMainActivity() {
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(mainIntent)
        finish()
    }

    private fun goToForgotPasswordPage() {
        registerIntent.putExtra("action", "forgot_password")
        startActivity(registerIntent)
    }

}