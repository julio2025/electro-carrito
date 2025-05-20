package com.android.electrocarrito.ui.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.electrocarrito.R
import com.android.electrocarrito.adapter.CartAdapter
import com.android.electrocarrito.dto.CartItem
import com.android.electrocarrito.dto.Product

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

        // Datos ficticios
        val sampleProducts = listOf(
            CartItem(Product(
                image = "https://images.samsung.com/is/image/samsung/p6pim/pe/rt38k5932sl-pe/gallery/pe-top-mount-freezer-rt38k5932sl-pe-002-front-silver-zoom.jpg",
                name = "Refrigeradora Samsung RT38K5932SL",
                description = "Refrigeradora No Frost de 384L con tecnología Twin Cooling Plus.",
                price = 1599.90
            ), 2),
            CartItem(Product(
                image = "https://images.samsung.com/is/image/samsung/p6pim/pe/rt38k5932sl-pe/gallery/pe-top-mount-freezer-rt38k5932sl-pe-002-front-silver-zoom.jpg",
                name = "Refrigeradora Samsung RT38K5932SL",
                description = "Refrigeradora No Frost de 384L con tecnología Twin Cooling Plus.",
                price = 1599.90
            ), 1),
            CartItem(Product(
                image = "https://images.samsung.com/is/image/samsung/p6pim/pe/rt38k5932sl-pe/gallery/pe-top-mount-freezer-rt38k5932sl-pe-002-front-silver-zoom.jpg",
                name = "Refrigeradora Samsung RT38K5932SL",
                description = "Refrigeradora No Frost de 384L con tecnología Twin Cooling Plus.",
                price = 1599.90
            ), 3)
        )
        cartItems.addAll(sampleProducts)

        val adapter = CartAdapter(cartItems) { totalPrice ->
            totalPriceTextView.text = "Total: $${"%.2f".format(totalPrice)}"
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // Navegar al fragmento de Checkout
        checkoutButton.setOnClickListener {
            val bundle = Bundle()
            view?.findNavController()?.navigate(R.id.action_nav_shopping_to_nav_checkout, bundle)
        }

        return view
    }
}