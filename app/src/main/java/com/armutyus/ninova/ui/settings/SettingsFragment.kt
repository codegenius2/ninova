package com.armutyus.ninova.ui.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.ABOUT_INTENT
import com.armutyus.ninova.constants.Constants.CHANGE_EMAIL
import com.armutyus.ninova.constants.Constants.CHANGE_PASSWORD
import com.armutyus.ninova.constants.Constants.DARK_THEME
import com.armutyus.ninova.constants.Constants.LIGHT_THEME
import com.armutyus.ninova.constants.Constants.LOGIN_INTENT
import com.armutyus.ninova.constants.Constants.MAIN_INTENT
import com.armutyus.ninova.constants.Constants.REGISTER
import com.armutyus.ninova.constants.Constants.REGISTER_INTENT
import com.armutyus.ninova.constants.Constants.SETTINGS_ACTION_KEY
import com.armutyus.ninova.constants.Constants.SYSTEM_THEME
import com.armutyus.ninova.constants.Response
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SettingsFragment @Inject constructor(
    private val auth: FirebaseAuth
) : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    @Named(LOGIN_INTENT)
    @Inject
    lateinit var loginIntent: Intent

    @Named(MAIN_INTENT)
    @Inject
    lateinit var mainIntent: Intent

    @Named(REGISTER_INTENT)
    @Inject
    lateinit var registerIntent: Intent

    @Named(ABOUT_INTENT)
    @Inject
    lateinit var aboutIntent: Intent

    private var sharedPreferences: SharedPreferences? = null
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        val user = auth.currentUser!!

        if (user.isAnonymous) {
            setPreferencesFromResource(R.xml.anonymous_preferences, rootKey)
        } else {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        settingsViewModel = ViewModelProvider(requireActivity())[SettingsViewModel::class.java]
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val changeEmailListener = Preference.OnPreferenceClickListener {
            registerIntent.putExtra(SETTINGS_ACTION_KEY, CHANGE_EMAIL)
            goToRegisterActivity()
            true
        }
        findPreference<Preference>("change_email")?.onPreferenceClickListener = changeEmailListener

        val changePasswordListener = Preference.OnPreferenceClickListener {
            registerIntent.putExtra(SETTINGS_ACTION_KEY, CHANGE_PASSWORD)
            goToRegisterActivity()
            true
        }
        findPreference<Preference>("change_password")?.onPreferenceClickListener =
            changePasswordListener

        val aboutNinovaListener = Preference.OnPreferenceClickListener {
            goToAboutActivity()
            true
        }
        findPreference<Preference>("about_ninova")?.onPreferenceClickListener = aboutNinovaListener

        val privacyPolicyListener = Preference.OnPreferenceClickListener {
            //intent to privacy policy
            println("Privacy policy")
            true
        }
        findPreference<Preference>("privacy_policy")?.onPreferenceClickListener =
            privacyPolicyListener

        val signOutListener = Preference.OnPreferenceClickListener {
            signOut()
            true
        }
        findPreference<Preference>("sign_out")?.onPreferenceClickListener = signOutListener

        val registerListener = Preference.OnPreferenceClickListener {
            registerIntent.putExtra(SETTINGS_ACTION_KEY, REGISTER)
            goToRegisterActivity()
            true
        }
        findPreference<Preference>("register")?.onPreferenceClickListener = registerListener

        val switchAccountListener = Preference.OnPreferenceClickListener {
            startActivity(loginIntent)
            true
        }
        findPreference<Preference>("switch_account")?.onPreferenceClickListener =
            switchAccountListener

    }

    override fun onResume() {
        super.onResume()
        sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        val themePref = p0?.getString("theme", "system")

        if (p1 == "theme") {
            when (themePref) {
                LIGHT_THEME -> {
                    p0.edit().putString("theme", LIGHT_THEME).apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                DARK_THEME -> {
                    p0.edit().putString("theme", DARK_THEME).apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

                SYSTEM_THEME -> {
                    p0.edit().putString("theme", SYSTEM_THEME).apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        }
    }

    private fun signOut() {
        settingsViewModel.signOut().observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> println("Loading")
                is Response.Success -> goToLogInActivity()
                is Response.Failure -> {
                    println("Create Error: " + response.errorMessage)
                    Toast.makeText(requireContext(), response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun goToLogInActivity() {
        startActivity(loginIntent)
        activity?.finish()
    }

    private fun goToRegisterActivity() {
        startActivity(registerIntent)
    }

    private fun goToAboutActivity() {
        startActivity(aboutIntent)
    }

}