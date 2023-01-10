package com.armutyus.ninova.ui.settings

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
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
import com.armutyus.ninova.constants.Constants.PRIVACY_POLICY_URL
import com.armutyus.ninova.constants.Constants.REGISTER
import com.armutyus.ninova.constants.Constants.REGISTER_INTENT
import com.armutyus.ninova.constants.Constants.SETTINGS_ACTION_KEY
import com.armutyus.ninova.constants.Constants.SYSTEM_THEME
import com.armutyus.ninova.constants.Constants.VERSION_NAME
import com.armutyus.ninova.constants.Response
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SettingsFragment @Inject constructor(
    auth: FirebaseAuth
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

    private val settingsViewModel by activityViewModels<SettingsViewModel>()
    private val user = auth.currentUser!!

    private val sharedPreferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(requireContext())

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        if (user.isAnonymous) {
            setPreferencesFromResource(R.xml.anonymous_preferences, "anonymous_preference")
        } else {
            setPreferencesFromResource(R.xml.root_preferences, "root_preference")
        }

        val aboutNinova = findPreference<Preference>("about_ninova")
        val changeEmail = findPreference<Preference>("change_email")
        val changePassword = findPreference<Preference>("change_password")
        val privacyPolicy = findPreference<Preference>("privacy_policy")
        val register = findPreference<Preference>("register")
        val signOut = findPreference<Preference>("sign_out")
        val switchAccount = findPreference<Preference>("switch_account")
        val uploadLibrary = findPreference<Preference>("upload_library")

        val changeEmailListener = Preference.OnPreferenceClickListener {
            registerIntent.putExtra(SETTINGS_ACTION_KEY, CHANGE_EMAIL)
            goToRegisterActivity()
            true
        }
        changeEmail?.onPreferenceClickListener = changeEmailListener

        val changePasswordListener = Preference.OnPreferenceClickListener {
            registerIntent.putExtra(SETTINGS_ACTION_KEY, CHANGE_PASSWORD)
            goToRegisterActivity()
            true
        }
        changePassword?.onPreferenceClickListener = changePasswordListener

        val aboutNinovaListener = Preference.OnPreferenceClickListener {
            goToAboutActivity()
            true
        }
        aboutNinova?.onPreferenceClickListener = aboutNinovaListener
        aboutNinova?.summary = "Version: $VERSION_NAME"

        val privacyPolicyListener = Preference.OnPreferenceClickListener {
            val privacyPolicyIntent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))
            ContextCompat.startActivity(requireContext(), privacyPolicyIntent, null)
            true
        }
        privacyPolicy?.onPreferenceClickListener = privacyPolicyListener

        val signOutListener = Preference.OnPreferenceClickListener {
            showSignOutDialog()
            true
        }
        signOut?.onPreferenceClickListener = signOutListener

        val uploadLibraryListener = Preference.OnPreferenceClickListener {
            uploadDataAndSignOut(shouldSignOut = false)
            true
        }
        uploadLibrary?.onPreferenceClickListener = uploadLibraryListener
        uploadLibrary?.summary = "Link your library with your account: ${user.email}"

        val registerListener = Preference.OnPreferenceClickListener {
            registerIntent.putExtra(SETTINGS_ACTION_KEY, REGISTER)
            goToRegisterActivity()
            true
        }
        register?.onPreferenceClickListener = registerListener

        val switchAccountListener = Preference.OnPreferenceClickListener {
            startActivity(loginIntent)
            true
        }
        switchAccount?.onPreferenceClickListener = switchAccountListener

    }

    override fun onResume() {
        super.onResume()
        removeBackButtonAndMenu()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        removeBackButtonAndMenu()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
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

    private fun removeBackButtonAndMenu() {
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.CREATED)
    }

    private fun uploadDataAndSignOut(
        dialog: DialogInterface? = null,
        shouldSignOut: Boolean = false
    ) {
        settingsViewModel.uploadUserData { response ->
            when (response) {
                is Response.Loading -> {
                    Toast.makeText(
                        requireContext(),
                        "Library uploading, please wait..",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("libraryUpload", "Library uploading")
                }
                is Response.Success -> {
                    Toast.makeText(
                        requireContext(),
                        "Library uploaded",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("libraryUpload", "Library uploaded")
                }
                is Response.Failure -> {
                    Log.e("Library Upload Error", response.errorMessage)
                    Toast.makeText(
                        requireContext(),
                        response.errorMessage,
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }.invokeOnCompletion {
            if (shouldSignOut) {
                signOut()
            }
            dialog?.dismiss()
        }
    }

    private fun signOut() {
        settingsViewModel.signOut { response ->
            when (response) {
                is Response.Loading ->
                    Toast.makeText(requireContext(), "Please wait..", Toast.LENGTH_SHORT).show()
                is Response.Success -> {
                    Toast.makeText(requireContext(), "Signed out!", Toast.LENGTH_SHORT).show()
                    Log.i("signOut", "Signed out successfully")
                    clearDatabase()
                    goToLogInActivity()
                }
                is Response.Failure -> {
                    Log.e("Sign Out Error", response.errorMessage)
                    Toast.makeText(requireContext(), response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun showSignOutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.sign_out))
            .setMessage(resources.getString(R.string.upload_library_message))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .setNegativeButton(resources.getString(R.string.decline)) { dialog, _ ->
                signOut()
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, _ ->
                uploadDataAndSignOut(dialog, true)
            }
            .show()
    }

    private fun clearDatabase() {
        settingsViewModel.clearDatabase()
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