package com.sjaindl.travelcompanion.explore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.databinding.ActivityExploreBinding

class ExploreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExploreBinding

    private val navController: NavController
        get() = (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment).navController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExploreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        navController.setGraph(R.navigation.navigation_explore, intent.extras)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val appBarConfiguration = AppBarConfiguration.Builder()
            .setFallbackOnNavigateUpListener {
                finish()
                super.onSupportNavigateUp()
            }
            .build()

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }
}
