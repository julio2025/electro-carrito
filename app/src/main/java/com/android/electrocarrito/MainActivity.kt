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

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_logout -> {
                    AlertDialog.Builder(this)
                        .setTitle(getString(R.string.cerrar_sesion))
                        .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                        .setPositiveButton("Sí") { _, _ ->
                            val prefs = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                            prefs.edit { clear() }

                            // Clear all Room tables
                            lifecycleScope.launch (Dispatchers.IO) {
                                val db = Room.databaseBuilder(
                                    applicationContext,
                                    AppDatabase::class.java, "electrocarrito-db"
                                ).build()
                                db.clearAllTables()
                                // Repeat for other DAOs/tables if needed

                                // Switch to main thread to navigate
                                launch(Dispatchers.Main) {
                                    val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                }
                            }
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                    true
                }
                // Handle other navigation items...
                else -> false
            }
        }

        val badge = bottomNavigationView.getOrCreateBadge(R.id.nav_shopping)

        // Set up the badge for the shopping cart DAO
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "electrocarrito-db"
        ).build()

        lifecycleScope.launch(Dispatchers.IO) {
            // GEt lista de ordenes
            val orderCurrent = db.ordenDao().getCurrentOrder()
            val shoppingCartCount: Int

            if (orderCurrent.isEmpty()) {
                shoppingCartCount = 0
            } else {
                val orderId = orderCurrent[0].id
                shoppingCartCount = db.ordenDetalleDao().getByOrderId(orderId).count()
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