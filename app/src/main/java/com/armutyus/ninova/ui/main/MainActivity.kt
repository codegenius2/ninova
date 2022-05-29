package com.armutyus.ninova.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.ActivityMainBinding
import com.armutyus.ninova.ui.splash.SplashActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_discovery, R.id.navigation_readList
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.settings_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings) {
            TODO("Implement settings page")
        } else if (item.itemId == R.id.sign_out) {
            signOut()
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun signOut() {
        viewModel.signOut().observe(this) { response ->
            when (response) {
                is Response.Loading -> binding.progressBar.show()
                is Response.Success -> binding.progressBar.hide()
                is Response.Failure -> {
                    println("Create Error: " + response.errorMessage)
                    Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                    binding.progressBar.hide()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}