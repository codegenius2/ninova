package com.armutyus.ninova.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.armutyus.ninova.MainActivity
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants
import com.armutyus.ninova.constants.Status
import com.armutyus.ninova.databinding.ActivityLoginBinding
import com.armutyus.ninova.databinding.RegisterUserBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var bottomSheetBinding: RegisterUserBottomSheetBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        /*val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }*/

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
            viewModel.signInUser(email, password).observe(this) {

                when (it.status) {

                    Status.SUCCESS -> {

                        startActivity(Intent(this, MainActivity::class.java))
                        finish()

                    }

                    Status.ERROR -> {

                        Toast.makeText(this, Constants.ERROR_MESSAGE, Toast.LENGTH_LONG).show()

                    }

                    Status.LOADING -> {

                        Toast.makeText(this, "Please wait..", Toast.LENGTH_LONG).show()
                        binding.progressBar.show()

                    }

                }

            }

            /*auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }*/
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

    private var userName = ""
    private var email = ""
    private var password = ""

    private fun registerUser() {
        userName = bottomSheetBinding.registerUsernameText.text.toString().trim()
        email = bottomSheetBinding.registerEmailText.text.toString().trim()
        password = bottomSheetBinding.registerPasswordText.text.toString().trim()
        val confirmPassword = bottomSheetBinding.registerConfirmPasswordText.text.toString().trim()

        if (userName.isEmpty() || email.isEmpty() || password.isEmpty() || password != confirmPassword) {
            Toast.makeText(this, "Please enter your information correctly!", Toast.LENGTH_LONG)
                .show()
        } else {
            createUserProfile()
        }

    }

    private fun createUserProfile() {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                saveUserInfo()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Failed to create account due to ${it.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun saveUserInfo() {

        val timeStamp = com.google.firebase.Timestamp.now()
        val userId = auth.uid

        val user = hashMapOf(
            "userId" to userId,
            "userName" to userName,
            "email" to email,
            "password" to password,
            "profileImage" to "",
            "timeStamp" to timeStamp
        )

        db.collection("users")
            .add(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Failed to save user info due to ${it.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

}