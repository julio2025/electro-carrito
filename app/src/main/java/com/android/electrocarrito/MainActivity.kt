package com.android.electrocarrito

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)

        val badge = bottomNavigationView.getOrCreateBadge(R.id.nav_shopping)
        badge.isVisible = true
        badge.number = 6
    }

    fun addBadge(itemId: Int) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val badge = bottomNavigationView.getOrCreateBadge(itemId)

        badge.isVisible = true
        badge.number += 1
    }

    fun removeBadge(itemId: Int) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val badge = bottomNavigationView.getOrCreateBadge(itemId)

        if (badge.number > 0) {
            badge.number -= 1
        } else {
            badge.isVisible = false
        }
    }
}