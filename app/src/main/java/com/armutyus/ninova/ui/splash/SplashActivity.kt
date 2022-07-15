package com.armutyus.ninova.ui.splash

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.armutyus.ninova.constants.Constants.DARK_THEME
import com.armutyus.ninova.constants.Constants.LIGHT_THEME
import com.armutyus.ninova.constants.Constants.LOGIN_INTENT
import com.armutyus.ninova.constants.Constants.MAIN_INTENT
import com.armutyus.ninova.constants.Constants.SYSTEM_THEME
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    @Named(LOGIN_INTENT)
    @Inject
    lateinit var loginIntent: Intent

    @Named(MAIN_INTENT)
    @Inject
    lateinit var mainIntent: Intent

    private val viewModel by viewModels<SplashViewModel>()
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        checkUserThemePreference()

        super.onCreate(savedInstanceState)
        checkIfUserIsAuthenticated()
    }

    private fun checkUserThemePreference() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val themePref = sharedPreferences?.getString("theme", SYSTEM_THEME)

        when (themePref) {
            LIGHT_THEME -> {
                sharedPreferences?.edit()?.putString("theme", LIGHT_THEME)?.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            DARK_THEME -> {
                sharedPreferences?.edit()?.putString("theme", DARK_THEME)?.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            SYSTEM_THEME -> {
                sharedPreferences?.edit()?.putString("theme", SYSTEM_THEME)?.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private fun checkIfUserIsAuthenticated() {
        if (viewModel.isUserAuthenticated) {
            goToMainActivity()
        } else {
            goToLogInActivity()
        }
    }

    private fun goToMainActivity() {
        startActivity(mainIntent)
        finish()
    }

    private fun goToLogInActivity() {
        startActivity(loginIntent)
        finish()
    }
}