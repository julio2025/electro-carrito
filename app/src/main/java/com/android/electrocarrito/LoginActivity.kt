package com.android.electrocarrito

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import androidx.core.content.edit
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editUser = findViewById<EditText>(R.id.username)
        val editPass = findViewById<EditText>(R.id.password)
        val btnLogin = findViewById<Button>(R.id.loginButton)

        val prefs = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("is_authenticated", false)) {
            goToMain()
            return
        }

        btnLogin.setOnClickListener {
            val user = editUser.text.toString()
            val pass = editPass.text.toString()
            val url = "https://i66aeqax65.execute-api.us-east-1.amazonaws.com/v1/usuario/autenticar?usuario=$user&clave_acceso=$pass"

            val queue = Volley.newRequestQueue(this)
            val request = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val json = JSONObject(response)
                    val exito = json.getBoolean("exito")

                    if (exito) {
                        prefs.edit { putBoolean("is_authenticated", true) }
                        goToMain()
                    } else {
                        Log.i("API_URL", url)
                        Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(request)
        }

        val registerLink = findViewById<TextView>(R.id.registerLink)
        registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}