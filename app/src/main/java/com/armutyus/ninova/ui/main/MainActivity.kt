package com.armutyus.ninova.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.armutyus.ninova.MobileNavigationDirections
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.DETAILS_INT_EXTRA
import com.armutyus.ninova.constants.Constants.FROM_DETAILS_TO_NOTES_EXTRA
import com.armutyus.ninova.constants.Constants.currentBook
import com.armutyus.ninova.constants.Constants.currentShelf
import com.armutyus.ninova.databinding.ActivityMainBinding
import com.armutyus.ninova.fragmentfactory.NinovaFragmentFactoryEntryPoint
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        val entryPoint = EntryPointAccessors.fromActivity(
            this,
            NinovaFragmentFactoryEntryPoint::class.java
        )
        supportFragmentManager.fragmentFactory = entryPoint.getFragmentFactory()

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        if (currentBook?.bookId != null) {
            when (intent.getIntExtra(DETAILS_INT_EXTRA, -1)) {
                currentBook!!.bookId -> {
                    val action =
                        MobileNavigationDirections.actionMainToBookToShelfFragment(currentBook!!.bookId)
                    navController.navigate(action)
                }

                FROM_DETAILS_TO_NOTES_EXTRA -> {
                    val action = MobileNavigationDirections.actionMainToBookUserNotesFragment()
                    navController.navigate(action)
                }

                else -> {}
            }
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
    }

    private fun bottomNavItemChangeListener(navView: BottomNavigationView) {
        navView.setOnItemSelectedListener { item ->
            if (item.itemId != navView.selectedItemId) {
                navController.navigate(item.itemId)
            }
            true
        }

        navView.setOnItemReselectedListener { selectedItem ->
            if (selectedItem.itemId == navView.selectedItemId) {
                navController.navigate(navView.selectedItemId)
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

                R.id.bookUserNotesFragment -> {
                    supportActionBar?.show()
                    navView.visibility = View.GONE
                    supportActionBar?.title = currentBook?.bookTitle
                }

                R.id.bookToShelfFragment -> {
                    supportActionBar?.show()
                    navView.visibility = View.GONE
                    supportActionBar?.title = currentBook?.bookTitle
                }

                else -> {
                    supportActionBar?.show()
                    navView.visibility = View.VISIBLE
                }

            }

        }
    }

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()

        menuInflater.inflate(R.menu.settings_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.menu_search -> {
                navController.navigate(R.id.action_main_to_search)
            }

            R.id.settings -> {
                navController.navigate(R.id.action_main_to_settings)
            }

        }

        return super.onOptionsItemSelected(item)
    }*/

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }
}