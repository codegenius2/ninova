package com.armutyus.ninova.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.armutyus.ninova.MainActivity
import com.armutyus.ninova.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.withoutRegister.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

    }


}