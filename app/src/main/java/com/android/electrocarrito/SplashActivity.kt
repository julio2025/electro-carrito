package com.android.electrocarrito

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.android.electrocarrito.dao.AppDatabase
import com.android.electrocarrito.dao.Producto
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

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
                    // Continue to next activity after data is saved
                    startNextActivity()
                }
            },
            { error ->
                // Handle error, then continue
                Log.e("===>Error: ", error.toString())
                startNextActivity()
            }
        )

        queue.add(request)
    }

    private fun startNextActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}