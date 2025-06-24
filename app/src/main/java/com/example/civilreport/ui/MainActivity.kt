package com.example.civilreport.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.civilreport.R
import com.example.civilreport.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels
import androidx.core.view.isVisible

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        // we only set the start destination if this is the first time the activity is created
        if (savedInstanceState == null) {
            navController.graph = navController.navInflater
                .inflate(R.navigation.nav_graph)
                .apply {
                    setStartDestination(
                        if (viewModel.isUserLoggedIn()) R.id.reportsFragment
                        else                               R.id.loginFragment
                    )
                }
        }



        binding.bottomNav.setupWithNavController(navController)

        // Hide bottom navigation on login fragment
        navController.addOnDestinationChangedListener { _, dest, _ ->
            binding.bottomNav.isVisible = dest.id != R.id.loginFragment
        }
    }
}