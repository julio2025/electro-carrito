package com.android.electrocarrito.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.electrocarrito.R
import com.android.electrocarrito.dao.Producto
import com.android.electrocarrito.dto.Product
import com.bumptech.glide.Glide

class ProductAdapter(private var productList: List<Producto>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productDescription: TextView = itemView.findViewById(R.id.product_description)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        Glide.with(holder.itemView.context)
            .load(product.imagen)
            .placeholder(R.drawable.placeholder)
            .into(holder.productImage)

        holder.productName.text = product.nombre
        holder.productDescription.text = product.descripcion
        holder.productPrice.text = "S/ %.2f".format(product.precio)

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("id", product.id)
                putString("image", product.imagen)
                putString("name", product.nombre)
                putString("description", product.descripcion)
                putString("price", product.precio.toString())
            }

            holder.itemView.findNavController().navigate(
                R.id.action_nav_gallery_to_product_detail_fragment,
                bundle
            )
        }
    }

    override fun getItemCount(): Int = productList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<Producto>) {
        productList = newList
        notifyDataSetChanged()
    }
}