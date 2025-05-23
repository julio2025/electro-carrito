package com.android.electrocarrito

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val prefs = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("is_authenticated", false)) {
            goToMain()
        }

        // Botón "Iniciar Sesión"
        val loginButton: Button = findViewById(R.id.btn_login)
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Hipervínculo "Regístrate aquí"
        val registerLink: TextView = findViewById(R.id.tv_register_link)
        registerLink.setOnClickListener {
            // Aquí puedes redirigir a una actividad de registro o mostrar un mensaje
            val intent = Intent(this, RegisterActivity::class.java) // Cambia a tu actividad de registro
            startActivity(intent)
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}