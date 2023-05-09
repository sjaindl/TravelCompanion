package com.sjaindl.travelcompanion.plan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.databinding.ActivityPlanBinding

class PlanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlanBinding

    private val navController: NavController
        get() = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_plan) as NavHostFragment).navController

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
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
