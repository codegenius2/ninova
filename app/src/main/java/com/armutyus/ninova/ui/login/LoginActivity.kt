package com.armutyus.ninova.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.armutyus.ninova.MainActivity
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.ActivityLoginBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.register.setOnClickListener {
            showRegisterDialog()
        }

        binding.withoutRegister.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

    }

    private fun showRegisterDialog() {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.register_user_bottom_sheet)
        val signUpButton = dialog.findViewById<MaterialButton>(R.id.signUpButton)

        signUpButton?.setOnClickListener {
            //To-Do --> Save user data to firebase
            startActivity(Intent(this,MainActivity::class.java))
        }

        dialog.show()
    }

}