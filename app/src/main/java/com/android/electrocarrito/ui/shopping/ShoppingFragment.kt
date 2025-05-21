package com.android.electrocarrito.ui.shopping

import android.os.Bundle
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
import androidx.room.Room
import com.android.electrocarrito.R
import com.android.electrocarrito.adapter.CartAdapter
import com.android.electrocarrito.dao.AppDatabase
import com.android.electrocarrito.dto.CartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingFragment : Fragment() {

    private val cartItems = mutableListOf<CartItem>() // Replace with your data source
    private lateinit var totalPriceTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shopping, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.cart_recycler_view)
        totalPriceTextView = view.findViewById(R.id.total_price)
        val checkoutButton: Button = view.findViewById(R.id.checkout_button)

        val activity = requireActivity()

        // Cargar CarItem con la orden vigente y los productos del detalle en sqlite mediante Room
        val db = AppDatabase.getDatabase(activity.applicationContext)

        lifecycleScope.launch (Dispatchers.IO) {
            val orderCurrent = db.ordenDao().getCurrentOrder()

            if (orderCurrent.isEmpty()) {
                return@launch
            }

            val orderDetalle = db.ordenDetalleDao().getByOrderId(orderCurrent[0].id)

            for (item in orderDetalle) {
                val product = db.productoDao().getById(item.id_producto)
                cartItems.add(CartItem(product, item.cantidad))
            }

            val adapter = CartAdapter(cartItems) { totalPrice ->
                totalPriceTextView.text = "Total: $${"%.2f".format(totalPrice)}"
            }

            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
        }

        // Navegar al fragmento de Checkout
        checkoutButton.setOnClickListener {
            val bundle = Bundle()
            view?.findNavController()?.navigate(R.id.action_nav_shopping_to_nav_checkout, bundle)
        }

        return view
    }
}