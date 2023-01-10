package com.armutyus.ninova.ui.about

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.armutyus.ninova.constants.Constants.FIREBASE_URL
import com.armutyus.ninova.constants.Constants.GLIDE_URL
import com.armutyus.ninova.constants.Constants.GOOGLE_BOOKS_API_URL
import com.armutyus.ninova.constants.Constants.LINK_BUILDER_URL
import com.armutyus.ninova.constants.Constants.LOTTIE_FILES_URL
import com.armutyus.ninova.constants.Constants.RETROFIT_URL
import com.armutyus.ninova.constants.Constants.VERSION_NAME
import com.armutyus.ninova.databinding.ActivityAboutBinding
import com.klinker.android.link_builder.Link
import com.klinker.android.link_builder.applyLinks
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appVersion.text = VERSION_NAME

        val creditsLinkText = binding.aboutCreditsLinksTextView
        creditsLinkText.applyLinks(getLinks())

    }

    private fun getLinks(): List<Link> {

        val firebaseUrl = Link("Firebase")
            .setTextColor(Color.parseColor("#005FAF"))
            .setTextColorOfHighlightedLink(Color.parseColor("#001C3A"))
            .setHighlightAlpha(0f)
            .setOnClickListener { openLink(FIREBASE_URL) }

        val glideUrl = Link("Glide Library")
            .setTextColor(Color.parseColor("#005FAF"))
            .setTextColorOfHighlightedLink(Color.parseColor("#001C3A"))
            .setHighlightAlpha(0f)
            .setOnClickListener { openLink(GLIDE_URL) }

        val googleBooksApiUrl = Link("Google Books API")
            .setTextColor(Color.parseColor("#005FAF"))
            .setTextColorOfHighlightedLink(Color.parseColor("#001C3A"))
            .setHighlightAlpha(0f)
            .setOnClickListener { openLink(GOOGLE_BOOKS_API_URL) }

        val linkBuilder = Link("Link Builder")
            .setTextColor(Color.parseColor("#005FAF"))
            .setTextColorOfHighlightedLink(Color.parseColor("#001C3A"))
            .setHighlightAlpha(0f)
            .setOnClickListener { openLink(LINK_BUILDER_URL) }

        val lottieAnimations = Link("Lottie Animations")
            .setTextColor(Color.parseColor("#005FAF"))
            .setTextColorOfHighlightedLink(Color.parseColor("#001C3A"))
            .setHighlightAlpha(0f)
            .setOnClickListener { openLink(LOTTIE_FILES_URL) }

        val retrofitUrl = Link("Retrofit")
            .setTextColor(Color.parseColor("#005FAF"))
            .setTextColorOfHighlightedLink(Color.parseColor("#001C3A"))
            .setHighlightAlpha(0f)
            .setOnClickListener { openLink(RETROFIT_URL) }

        return listOf(
            firebaseUrl,
            glideUrl,
            googleBooksApiUrl,
            linkBuilder,
            lottieAnimations,
            retrofitUrl
        )
    }

    private fun openLink(link: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(browserIntent)
    }

}