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
        val emptyView: View = view.findViewById(R.id.emptyView)

        val activity = requireActivity()

        lifecycleScope.launch (Dispatchers.IO) {
            val db = AppDatabase.getDatabase(activity.applicationContext)

            val orderCurrent = db.ordenDao().getCurrentOrder()

            if (orderCurrent.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
                return@launch
            }

            val orderDetalle = db.ordenDetalleDao().getByOrderId(orderCurrent[0].id)

            if (orderDetalle.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
                return@launch
            }

            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE

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
            totalPriceTextView.text = "Total: S/ ${"%.2f".format(totalPrice)}"

            val adapter = CartAdapter(cartItems,
                { newTotalPrice -> totalPriceTextView.text = "Total: S/ ${"%.2f".format(newTotalPrice)}" },
                { cartItem, newQuantity -> },
                { cartItem ->
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
                },
                {
                    recyclerView.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                }
            )

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