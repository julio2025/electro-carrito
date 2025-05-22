package com.android.electrocarrito.ui.gallery

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.room.Room
import com.android.electrocarrito.MainActivity
import com.android.electrocarrito.R
import com.android.electrocarrito.dao.AppDatabase
import com.android.electrocarrito.dao.Orden
import com.android.electrocarrito.dao.OrdenDetalle
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProductDetailFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_detail, container, false)

        val backArrow: ImageView = view.findViewById(R.id.back_arrow)
        val productImage: ImageView = view.findViewById(R.id.product_image)
        val productName: TextView = view.findViewById(R.id.product_name)
        val productDescription: TextView = view.findViewById(R.id.product_description)
        val productPrice: TextView = view.findViewById(R.id.product_price)
        val addToCartButton: Button = view.findViewById(R.id.add_to_cart_button)

        // Obtiene los datos del producto desde el bundle
        val id_producto = arguments?.getInt("id")
        val imageUrl = arguments?.getString("image")
        val name = arguments?.getString("name")
        val description = arguments?.getString("description")
        val price = arguments?.getString("price")

        // Carga la imagen desde URL con Glide
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder) // mientras carga
            .error(R.drawable.placeholder)       // si falla
            .into(productImage)

        // Rellena los textos
        productName.text = name
        productDescription.text = description
        productPrice.text = "S/ $price"

        // Acción para volver
        backArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Acción para agregar al carrito
        addToCartButton.setOnClickListener {
            val activity = requireActivity()

            activity.lifecycleScope.launch (Dispatchers.IO) {
                val db = AppDatabase.getDatabase(activity.applicationContext)

                val orderCurrent = db.ordenDao().getCurrentOrder()
                if (orderCurrent.isEmpty()) {
                    // Si no hay orden actual, crea una nueva
                    // Get Id_Usuario for Shared prefs
                    val prefs = activity.getSharedPreferences("auth_prefs", AppCompatActivity.MODE_PRIVATE)
                    val idUsuario = prefs.getInt("id_usuario", 0)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val today = dateFormat.format(Date())

                    val newOrder = Orden(0, idUsuario, today, "Pendiente", price?.toDouble() ?: 0.0, true)
                    val newOrderId = db.ordenDao().insert(newOrder)
                    //Convertir newOrderId a Int
                    val newOrderIdInt = newOrderId.toInt()

                    val newOrdenDetalle = id_producto?.let { it1 ->
                        OrdenDetalle(0, newOrderIdInt,
                            it1, 1, price?.toDouble() ?: 0.0)
                    }

                    Log.i("DB_LOG", "Registro Orden Detalle: ${newOrdenDetalle.toString()}" )
                    db.ordenDetalleDao().insert(newOrdenDetalle!!)
                } else {

                    val currentOrder = orderCurrent[0]
                    currentOrder.total += price?.toDouble() ?: 0.0
                    db.ordenDao().update(currentOrder)

                    val orderDetalle = db.ordenDetalleDao().getByOrderId(currentOrder.id)
                    var found = false
                    var newOrdenDetalle: OrdenDetalle? = null

                    for (item in orderDetalle) {
                        if (item.id_producto == id_producto) {
                            found = true
                            newOrdenDetalle = item
                            break
                        }
                    }

                    if (found && newOrdenDetalle != null) {
                        // Si el producto ya existe en la orden, actualiza la cantidad
                        newOrdenDetalle.cantidad += 1
                        db.ordenDetalleDao().update(newOrdenDetalle)
                    } else {
                        // Si el producto no existe, crea un nuevo detalle de orden
                        newOrdenDetalle = id_producto?.let { it1 ->
                            OrdenDetalle(0, currentOrder.id,
                                it1, 1, price?.toDouble() ?: 0.0)
                        }

                        db.ordenDetalleDao().insert(newOrdenDetalle!!)
                    }
                }

                // Switch to main thread to update UI or navigate
                launch(Dispatchers.Main) {
                    (activity as MainActivity).addBadge(R.id.nav_shopping)
                    //view.findNavController().navigate(R.id.action_productDetailFragment_to_nav_shopping)
                    Toast.makeText(
                        activity,
                        "Producto agregado al carrito",
                        Toast.LENGTH_SHORT
                    ).show()
                    backArrow.callOnClick()
                }
            }
        }

        return view
    }
}
