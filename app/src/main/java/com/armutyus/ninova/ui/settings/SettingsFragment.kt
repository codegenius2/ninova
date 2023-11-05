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
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.ABOUT_INTENT
import com.armutyus.ninova.constants.Constants.ALEXANDRIA_DARK_THEME
import com.armutyus.ninova.constants.Constants.ALEXANDRIA_LIGHT_THEME
import com.armutyus.ninova.constants.Constants.BERGAMA_DARK_THEME
import com.armutyus.ninova.constants.Constants.BERGAMA_LIGHT_THEME
import com.armutyus.ninova.constants.Constants.CHANGE_EMAIL
import com.armutyus.ninova.constants.Constants.CHANGE_PASSWORD
import com.armutyus.ninova.constants.Constants.LOGIN_INTENT
import com.armutyus.ninova.constants.Constants.MAIN_INTENT
import com.armutyus.ninova.constants.Constants.NINOVA_DARK_THEME
import com.armutyus.ninova.constants.Constants.NINOVA_LIGHT_THEME
import com.armutyus.ninova.constants.Constants.PRIVACY_POLICY_URL
import com.armutyus.ninova.constants.Constants.REGISTER
import com.armutyus.ninova.constants.Constants.REGISTER_INTENT
import com.armutyus.ninova.constants.Constants.SETTINGS_ACTION_KEY
import com.armutyus.ninova.constants.Constants.VERSION_NAME
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.CustomDialogPasswordLayoutBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.EmailAuthProvider
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
    private lateinit var customDialogPasswordLayoutBinding: CustomDialogPasswordLayoutBinding

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
        val deleteAccount = findPreference<Preference>("delete_account")
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
        aboutNinova?.summary = context?.getString(R.string.app_version, VERSION_NAME)

        val privacyPolicyListener = Preference.OnPreferenceClickListener {
            val privacyPolicyIntent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))
            ContextCompat.startActivity(requireContext(), privacyPolicyIntent, null)
            true
        }
        privacyPolicy?.onPreferenceClickListener = privacyPolicyListener

        val deleteAccountListener = Preference.OnPreferenceClickListener {
            showDeleteAccountDialog()
            true
        }
        deleteAccount?.onPreferenceClickListener = deleteAccountListener

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
        uploadLibrary?.summary = context?.getString(R.string.link_your_library, user.email)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        removeBackButtonAndMenu()
    }

    override fun onResume() {
        super.onResume()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        val themePref = p0?.getString("theme", NINOVA_LIGHT_THEME)

        if (p1 == "theme") {
            when (themePref) {
                NINOVA_LIGHT_THEME -> {
                    p0.edit().putString("theme", NINOVA_LIGHT_THEME).apply()
                    requireActivity().theme.applyStyle(R.style.Theme_Ninova, true)
                    requireActivity().recreate()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                NINOVA_DARK_THEME -> {
                    p0.edit().putString("theme", NINOVA_DARK_THEME).apply()
                    requireActivity().theme.applyStyle(R.style.Theme_Ninova, true)
                    requireActivity().recreate()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

                BERGAMA_LIGHT_THEME -> {
                    p0.edit().putString("theme", BERGAMA_LIGHT_THEME).apply()
                    requireActivity().theme.applyStyle(R.style.Theme_Bergama, true)
                    requireActivity().recreate()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                BERGAMA_DARK_THEME -> {
                    p0.edit().putString("theme", BERGAMA_DARK_THEME).apply()
                    requireActivity().theme.applyStyle(R.style.Theme_Bergama, true)
                    requireActivity().recreate()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

                ALEXANDRIA_LIGHT_THEME -> {
                    p0.edit().putString("theme", ALEXANDRIA_LIGHT_THEME).apply()
                    requireActivity().theme.applyStyle(R.style.Theme_Alexandria, true)
                    requireActivity().recreate()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                ALEXANDRIA_DARK_THEME -> {
                    p0.edit().putString("theme", ALEXANDRIA_DARK_THEME).apply()
                    requireActivity().theme.applyStyle(R.style.Theme_Alexandria, true)
                    requireActivity().recreate()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
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
                        R.string.library_uploading,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("libraryUpload", "Library uploading")
                }

                is Response.Success -> {
                    Toast.makeText(
                        requireContext(),
                        R.string.library_uploaded,
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
                    Toast.makeText(requireContext(), R.string.please_wait, Toast.LENGTH_SHORT)
                        .show()

                is Response.Success -> {
                    Toast.makeText(requireContext(), R.string.signed_out, Toast.LENGTH_SHORT).show()
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

    private fun deleteAccountPermanently(password: String) {
        val credential = EmailAuthProvider.getCredential(user.email!!, password)
        settingsViewModel.deleteUserPermanently(credential) { response ->
            when (response) {
                is Response.Loading -> Toast.makeText(
                    requireContext(),
                    R.string.please_wait,
                    Toast.LENGTH_SHORT
                ).show()

                is Response.Success -> {
                    Log.i("User Deleted", "User deleted successfully")
                    Toast.makeText(
                        requireContext(),
                        R.string.account_deleted,
                        Toast.LENGTH_LONG
                    ).show()
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    clearDatabase()
                    goToLogInActivity()
                }

                is Response.Failure -> {
                    Log.e("SettingsFragment", "Delete User Error: " + response.errorMessage)
                    Toast.makeText(requireContext(), response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun showDeleteAccountDialog() {
        var password: String
        customDialogPasswordLayoutBinding =
            CustomDialogPasswordLayoutBinding.inflate(layoutInflater)
        val passwordTextField = customDialogPasswordLayoutBinding.customDialogPasswordText
        password = passwordTextField.text.toString()
        val builder =
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.delete_account))
                .setMessage(resources.getString(R.string.delete_account_warning))
                .setView(customDialogPasswordLayoutBinding.root)
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                }
                .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                    deleteAccountPermanently(password)
                }
        val createShelfDialog = builder.create()
        passwordTextField.doAfterTextChanged {
            password = passwordTextField.text.toString()
            createShelfDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
                password.isNotEmpty()
        }
        createShelfDialog.setCanceledOnTouchOutside(false)
        createShelfDialog.show()
        createShelfDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
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