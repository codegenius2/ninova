package com.armutyus.ninova.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.MAIN_INTENT
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.ActivityLoginBinding
import com.armutyus.ninova.databinding.RegisterUserBottomSheetBinding
import com.armutyus.ninova.ui.main.MainActivity
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
    private lateinit var binding: ActivityLoginBinding
    private lateinit var bottomSheetBinding: RegisterUserBottomSheetBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setOnClickListener {
            loginUser()
        }

        binding.register.setOnClickListener {
            showRegisterDialog()
        }

        binding.withoutRegister.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }

    private fun loginUser() {
        email = binding.userEmailText.text.toString().trim()
        password = binding.userPasswordText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your information correctly!", Toast.LENGTH_LONG)
                .show()
        } else {
            viewModel.signInUser(email, password).observe(this) { response ->
                when (response) {
                    is Response.Loading -> binding.progressBar.show()
                    is Response.Success -> {
                        goToMainActivity()
                        binding.progressBar.hide()
                    }
                    is Response.Failure -> {
                        print(response.errorMessage)
                        binding.progressBar.hide()
                    }
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

    private var email = ""
    private var password = ""

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
                is Response.Loading -> binding.progressBar.show()
                is Response.Success -> {
                    createUserProfile()
                    binding.progressBar.hide()
                }
                is Response.Failure -> {
                    print(response.errorMessage)
                    binding.progressBar.hide()
                }
            }
        }
    }

    private fun createUserProfile() {

        viewModel.createUser().observe(this) { response ->
            when (response) {
                is Response.Loading -> binding.progressBar.show()
                is Response.Success -> {
                    goToMainActivity()
                    binding.progressBar.hide()
                }
                is Response.Failure -> {
                    print(response.errorMessage)
                    binding.progressBar.hide()
                }
            }
        }
    }

    private fun goToMainActivity() {
        startActivity(mainIntent)
        finish()
    }

}