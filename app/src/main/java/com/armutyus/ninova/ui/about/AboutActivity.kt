package com.armutyus.ninova.ui.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.armutyus.ninova.constants.Constants.VERSION_NAME
import com.armutyus.ninova.databinding.ActivityAboutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appVersion.text = VERSION_NAME

    }
}