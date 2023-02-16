package com.armutyus.ninova.ui.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.MenuProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Cache.currentShelf
import com.armutyus.ninova.constants.Constants
import com.armutyus.ninova.constants.Constants.MAIN_SHARED_PREF
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.ActivityMainBinding
import com.armutyus.ninova.fragmentfactory.NinovaFragmentFactoryEntryPoint
import com.armutyus.ninova.ui.books.BooksViewModel
import com.armutyus.ninova.ui.shelves.ShelvesViewModel
import com.armutyus.ninova.ui.splash.SplashViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Named(Constants.LOGIN_INTENT)
    @Inject
    lateinit var loginIntent: Intent

    private lateinit var binding: ActivityMainBinding
    private val booksViewModel by viewModels<BooksViewModel>()
    private lateinit var navController: NavController
    private val shelvesViewModel by viewModels<ShelvesViewModel>()
    private val splashViewModel by viewModels<SplashViewModel>()
    private val sharedPreferences: SharedPreferences
        get() = this.getSharedPreferences(MAIN_SHARED_PREF, Context.MODE_PRIVATE)
    private val themePreferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(this)

    override fun onCreate(savedInstanceState: Bundle?) {

        val entryPoint = EntryPointAccessors.fromActivity(
            this,
            NinovaFragmentFactoryEntryPoint::class.java
        )
        supportFragmentManager.fragmentFactory = entryPoint.getFragmentFactory()
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                checkUserThemePreference()
                !splashViewModel.isUserAuthenticated
            }
        }

        super.onCreate(savedInstanceState)

        if (splashViewModel.isUserAuthenticated) {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val navView: BottomNavigationView = binding.navView

            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
            navController = navHostFragment.navController

            if (sharedPreferences.getBoolean(
                    "first_time",
                    true
                ) && !splashViewModel.isUserAnonymous
            ) {
                fetchBooks()
                fetchShelves()
                fetchCrossRefs()
            }

            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menu.clear()
                    menuInflater.inflate(R.menu.settings_menu, menu)
                }

                override fun onMenuItemSelected(item: MenuItem): Boolean {
                    when (item.itemId) {

                        R.id.menu_search -> {
                            navController.navigate(R.id.action_main_to_search)
                        }

                        R.id.settings -> {
                            navController.navigate(R.id.action_main_to_settings)
                        }

                    }
                    return true
                }
            })

            destinationChangeListener(navView)

            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_books, R.id.navigation_discovery, R.id.navigation_shelves
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)

            bottomNavItemChangeListener(navView)
        } else {
            startActivity(loginIntent)
            finish()
        }


    }

    private fun bottomNavItemChangeListener(navView: BottomNavigationView) {
        navView.setOnItemSelectedListener { item ->
            if (item.itemId != navView.selectedItemId) {
                navController.popBackStack(item.itemId, inclusive = true, saveState = false)
                navController.navigate(item.itemId)
            }
            true
        }
    }

    private fun checkUserThemePreference() {
        when (themePreferences.getString("theme", Constants.SYSTEM_THEME)) {
            Constants.LIGHT_THEME -> {
                themePreferences.edit()?.putString("theme", Constants.LIGHT_THEME)?.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            Constants.DARK_THEME -> {
                themePreferences.edit()?.putString("theme", Constants.DARK_THEME)?.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            Constants.SYSTEM_THEME -> {
                themePreferences.edit()?.putString("theme", Constants.SYSTEM_THEME)?.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private fun destinationChangeListener(navView: BottomNavigationView) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mainSearchFragment -> {
                    supportActionBar?.hide()
                    navView.visibility = View.GONE
                }

                R.id.settingsFragment -> {
                    supportActionBar?.show()
                    navView.visibility = View.GONE
                }

                R.id.shelfWithBooksFragment -> {
                    supportActionBar?.show()
                    navView.visibility = View.VISIBLE
                    supportActionBar?.title = currentShelf?.shelfTitle
                }

                else -> {
                    supportActionBar?.show()
                    navView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun fetchBooks() {
        booksViewModel.collectBooksFromFirestore { response ->
            when (response) {
                is Response.Loading -> {
                    Toast.makeText(
                        this,
                        R.string.checking_library, Toast.LENGTH_SHORT
                    ).show()
                    Log.i("booksDownload", "Books downloading")
                }
                is Response.Success -> {
                    val firebaseBookList = response.data
                    if (firebaseBookList.isNotEmpty()) {
                        firebaseBookList.forEach {
                            booksViewModel.insertBook(it).invokeOnCompletion {
                                booksViewModel.loadBookList()
                            }
                            if (firebaseBookList.indexOf(it) == firebaseBookList.size - 1) {
                                Log.i("booksDownload", "Books downloaded")
                            }
                        }
                    } else {
                        Log.i("booksDownload", "No books")
                    }
                }
                is Response.Failure -> {
                    Log.e("Firebase Fetch Books Error:", response.errorMessage)
                    Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun fetchCrossRefs() {
        booksViewModel.collectCrossRefFromFirestore { response ->
            when (response) {
                is Response.Loading -> {
                    Log.i("crossRefsDownload", "CrossRefs downloading")
                }
                is Response.Success -> {
                    val firebaseCrossRefList = response.data
                    if (firebaseCrossRefList.isNotEmpty()) {
                        firebaseCrossRefList.forEach {
                            shelvesViewModel.insertBookShelfCrossRef(it)
                                .invokeOnCompletion {
                                    shelvesViewModel.loadShelfWithBookList()
                                }
                            if (firebaseCrossRefList.indexOf(it) == firebaseCrossRefList.size - 1) {
                                Log.i("crossRefsDownload", "CrossRefs downloaded")
                            }
                        }
                    } else {
                        Log.i("crossRefsDownload", "No CrossRefs")
                    }
                    with(sharedPreferences.edit()) {
                        putBoolean("first_time", false).apply()
                    }
                }
                is Response.Failure -> {
                    Log.e("Firebase Fetch CrossRefs Error:", response.errorMessage)
                    Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun fetchShelves() {
        booksViewModel.collectShelvesFromFirestore { response ->
            when (response) {
                is Response.Loading -> {
                    Log.i("shelvesDownload", "Shelves downloading")
                }
                is Response.Success -> {
                    val firebaseShelvesList = response.data
                    if (firebaseShelvesList.isNotEmpty()) {
                        firebaseShelvesList.forEach {
                            shelvesViewModel.insertShelf(it).invokeOnCompletion {
                                shelvesViewModel.loadShelfList()
                            }
                            if (firebaseShelvesList.indexOf(it) == firebaseShelvesList.size - 1) {
                                Log.i("shelvesDownload", "Shelves downloaded")
                                Toast.makeText(
                                    this,
                                    R.string.library_synced,
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }
                    } else {
                        Log.i("shelvesDownload", "No shelves")
                    }

                }
                is Response.Failure -> {
                    Log.e("Firebase Fetch CrossRefs Error:", response.errorMessage)
                    Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.popBackStack()
                || super.onSupportNavigateUp()
    }

}