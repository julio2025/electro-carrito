package com.android.electrocarrito

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.android.electrocarrito.dao.AppDatabase
import com.android.electrocarrito.dao.Producto
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editUser = findViewById<EditText>(R.id.username)
        val editPass = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)

        val loadingOverlay = findViewById<View>(R.id.loadingOverlay)

        loginButton.setOnClickListener {
            loadingOverlay.visibility = View.VISIBLE
            loginButton.isEnabled = false

            val user = editUser.text.toString()
            val pass = editPass.text.toString()
            val url = "https://i66aeqax65.execute-api.us-east-1.amazonaws.com/v1/usuario/autenticar?usuario=$user&clave_acceso=$pass"

            val queue = Volley.newRequestQueue(this)
            val request = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val json = JSONObject(response)
                    val exito = json.getBoolean("exito")
                    val id = json.getInt("id")

                    if (exito) {
                        val prefs = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                        prefs.edit {
                            putBoolean("is_authenticated", true)
                            putInt("id_usuario", id)
                        }

                        getProductsByAPI()
                    } else {
                        Log.i("API_URL", url)
                        Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                        loadingOverlay.visibility = View.GONE
                        loginButton.isEnabled = true
                    }
                },
                { error ->
                    Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show()
                    loadingOverlay.visibility = View.GONE
                    loginButton.isEnabled = true
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

    private fun getProductsByAPI() {
        val loadingOverlay = findViewById<View>(R.id.loadingOverlay)
        val queue = Volley.newRequestQueue(this)
        val url = "https://i66aeqax65.execute-api.us-east-1.amazonaws.com/v1/productos"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val db = AppDatabase.getDatabase(applicationContext)
                        val jsonArray = response.getJSONArray("data")

                        db.productoDao().deleteAll()
                        for (i in 0 until jsonArray.length()) {

                            val item = jsonArray.getJSONObject(i)
                            val producto = Producto(
                                id = item.getInt("id_producto"),
                                nombre = item.getString("nombre"),
                                descripcion = item.getString("descripcion"),
                                precio = item.getDouble("precio"),
                                stock = item.getInt("stock"),
                                imagen = item.getString("imagen")
                            )
                            db.productoDao().insert(producto)
                        }
                    }

                    loadingOverlay.visibility = View.GONE
                    goToMain()
                }
            },
            { error ->
                // Handle error, then continue
                Log.e("===>Error: ", error.toString())

            }
        )

        queue.add(request)
    }


}