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
import com.bumptech.glide.Glide

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

        // Obtiene los datos del producto desde el bundle
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
        productPrice.text = price

        // Acción para volver
        backArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Acción para agregar al carrito
        addToCartButton.setOnClickListener {
            // Lógica para agregar al carrito (puedes implementarla si tienes un ViewModel o repo)
            (requireActivity() as MainActivity).addBadge(R.id.nav_shopping)

            // Navegar al carrito
            view.findNavController().navigate(R.id.action_productDetailFragment_to_nav_shopping)
        }

        return view
    }
}
