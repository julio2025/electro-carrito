package com.android.electrocarrito

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.android.electrocarrito.dao.AppDatabase
import com.android.electrocarrito.dao.Orden
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(applicationContext)

            val orderCurrent = db.ordenDao().getCurrentOrder()
            val shoppingCartCount: Int

            if (orderCurrent.isEmpty()) {
                shoppingCartCount = 0
            } else {
                val orderId = orderCurrent[0].id
                val ordenDetalle = db.ordenDetalleDao().getByOrderId(orderId)

                shoppingCartCount = ordenDetalle.sumOf { it.cantidad }
            }

            withContext(Dispatchers.Main) {
                if (shoppingCartCount > 0) {
                    badge.isVisible = true
                    badge.number = shoppingCartCount
                } else {
                    badge.isVisible = false
                }
            }
        }
    }

    fun addBadge(itemId: Int) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val badge = bottomNavigationView.getOrCreateBadge(itemId)

        badge.isVisible = true
        badge.number += 1
    }

    fun removeBadge(itemId: Int, many: Int) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val badge = bottomNavigationView.getOrCreateBadge(itemId)

        badge.number -= many

        if (badge.number < 0) {
            badge.number = 0
            badge.isVisible = false
        } else {
            badge.isVisible = true
        }
    }
}