package com.android.electrocarrito

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameField = findViewById<TextInputEditText>(R.id.name)
        val usernameField = findViewById<TextInputEditText>(R.id.username)
        val passwordField = findViewById<TextInputEditText>(R.id.password)
        val registerButton = findViewById<MaterialButton>(R.id.registerButton)
        val loginLink = findViewById<TextView>(R.id.loginLink)

        registerButton.setOnClickListener {
            val user = usernameField.text.toString()
            val pass = passwordField.text.toString()
            val name = nameField.text.toString()

            val url = "https://i66aeqax65.execute-api.us-east-1.amazonaws.com/v1/usuario"
            val jsonObject = JSONObject()
            jsonObject.put("usuario", user)
            jsonObject.put("clave_acceso", pass)
            jsonObject.put("nombre_completo", name)

            val request = object : JsonObjectRequest(
                Method.POST, url, jsonObject,
                Response.Listener { response ->

                    val errorMessage = response.getString("errorMessage")

                    if (errorMessage.isNotEmpty()) {
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("API_ERROR", error.toString())
                    error.networkResponse?.let {
                        Log.e("API_STATUS_CODE", it.statusCode.toString())
                    }
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["User-Agent"] = "Mozilla/5.0"
                    return headers
                }
            }

            // Agregar la solicitud a la cola de Volley
            Volley.newRequestQueue(this).add(request)
        }

        loginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}