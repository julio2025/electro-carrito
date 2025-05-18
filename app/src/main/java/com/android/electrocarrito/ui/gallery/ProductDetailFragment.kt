package com.android.electrocarrito.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.electrocarrito.MainActivity
import com.android.electrocarrito.R

class ProductDetailFragment : Fragment() {
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

        // Obtén los datos del producto desde los argumentos
        val args = arguments
        productImage.setImageResource(args?.getInt("image") ?: R.drawable.placeholder)
        productName.text = args?.getString("name")
        productDescription.text = args?.getString("description")
        productPrice.text = args?.getString("price")

        // Acción para volver
        backArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Acción para agregar al carrito
        addToCartButton.setOnClickListener {
            // Lógica para agregar al carrito
            (requireActivity() as MainActivity).addBadge(R.id.nav_shopping)
            // Navegar al carrito
            view.findNavController().navigate(R.id.action_productDetailFragment_to_nav_shopping)
        }

        return view
    }
}