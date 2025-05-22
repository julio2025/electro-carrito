package com.android.electrocarrito.ui.shopping

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.electrocarrito.R
import com.android.electrocarrito.adapter.CartAdapter
import com.android.electrocarrito.dao.AppDatabase
import com.android.electrocarrito.dto.CartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingFragment : Fragment() {

    private val cartItems = mutableListOf<CartItem>() // Replace with your data source
    private lateinit var totalPriceTextView: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shopping, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.cart_recycler_view)
        totalPriceTextView = view.findViewById(R.id.total_price)
        val checkoutButton: Button = view.findViewById(R.id.checkout_button)

        val activity = requireActivity()

        lifecycleScope.launch (Dispatchers.IO) {
            val db = AppDatabase.getDatabase(activity.applicationContext)

            val orderCurrent = db.ordenDao().getCurrentOrder()

            if (orderCurrent.isEmpty()) {
                return@launch
            }

            val orderDetalle = db.ordenDetalleDao().getByOrderId(orderCurrent[0].id)

            for (item in orderDetalle) {
                Log.i("===>", "Item: ${item.id_producto} - Cantidad: ${item.cantidad}")
                val product = db.productoDao().getById(item.id_producto)
                cartItems.add(CartItem(product, item.cantidad))
            }

            // Calcular el precio total
            var totalPrice = 0.0
            for (cartItem in cartItems) {
                totalPrice += cartItem.product.precio * cartItem.quantity
            }
            totalPriceTextView.text = "Total: $${"%.2f".format(totalPrice)}"

            val adapter = CartAdapter(cartItems, { newTotalPrice ->
                totalPriceTextView.text = "Total: $${"%.2f".format(newTotalPrice)}"
            }, { cartItem, newQuantity ->
                // update quantity logic
            }) { cartItem ->
                lifecycleScope.launch(Dispatchers.IO) {
                    val db = AppDatabase.getDatabase(requireContext())
                    val orderCurrent = db.ordenDao().getCurrentOrder()
                    if (orderCurrent.isNotEmpty()) {
                        val ordenDetalle = db.ordenDetalleDao().getByOrderId(orderCurrent[0].id)
                            .find { it.id_producto == cartItem.product.id }
                        if (ordenDetalle != null) {
                            db.ordenDetalleDao().delete(ordenDetalle)
                        }
                    }
                }
            }

            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
        }

        // Navegar al fragmento de Checkout
        checkoutButton.setOnClickListener {

            lifecycleScope.launch(Dispatchers.IO) {

                /*
                val db = AppDatabase.getDatabase(requireContext())
                val productos = db.productoDao().getAll()
                for (producto in productos) {
                    Log.d("DB_LOG", producto.toString())
                }

                val ordenes = db.ordenDao().getAll()
                for (orden in ordenes) {
                    Log.d("DB_LOG", orden.toString())
                }

                val ordenDetalles = db.ordenDetalleDao().getAll()
                for (ordenDetalle in ordenDetalles) {
                    Log.d("DB_LOG", ordenDetalle.toString())
                }
                */
            }

            val bundle = Bundle()
            view?.findNavController()?.navigate(R.id.action_nav_shopping_to_nav_checkout, bundle)
        }

        return view
    }
}