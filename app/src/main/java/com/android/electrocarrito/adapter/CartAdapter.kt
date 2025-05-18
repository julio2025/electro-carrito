package com.android.electrocarrito.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.electrocarrito.MainActivity
import com.android.electrocarrito.R
import com.android.electrocarrito.dto.CartItem

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val onQuantityChanged: (Double) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.cart_product_image)
        val productName: TextView = itemView.findViewById(R.id.cart_product_name)
        val productPrice: TextView = itemView.findViewById(R.id.cart_product_price)
        val productQuantity: TextView = itemView.findViewById(R.id.cart_product_quantity)
        val increaseButton: Button = itemView.findViewById(R.id.increase_quantity)
        val decreaseButton: Button = itemView.findViewById(R.id.decrease_quantity)
        val deleteIcon: ImageView = itemView.findViewById(R.id.delete_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.productName.text = cartItem.product.name
        holder.productPrice.text = "$${cartItem.product.price}"
        holder.productQuantity.text = "Qty: ${cartItem.quantity}"
        holder.productImage.setImageResource(cartItem.product.image)

        holder.increaseButton.setOnClickListener {
            showConfirmationDialog(holder.itemView, "incrementar un producto") {
                cartItem.quantity++
                holder.productQuantity.text = "Qty: ${cartItem.quantity}"
                onQuantityChanged(calculateTotalPrice())
                (holder.itemView.context as MainActivity).addBadge(R.id.nav_shopping)
            }
        }

        holder.decreaseButton.setOnClickListener {
            if (cartItem.quantity > 1) {
                showConfirmationDialog(holder.itemView, "quitar un producto") {
                    cartItem.quantity--
                    holder.productQuantity.text = "Qty: ${cartItem.quantity}"
                    onQuantityChanged(calculateTotalPrice())
                    (holder.itemView.context as MainActivity).removeBadge(R.id.nav_shopping)
                }
            }
        }

        holder.deleteIcon.setOnClickListener {
            showConfirmationDialog(holder.itemView, "remover el producto del carrito") {
                cartItems.removeAt(position)
                notifyItemRemoved(position)
                updateTotalPrice()
                (holder.itemView.context as MainActivity).removeBadge(R.id.nav_shopping)
            }
        }
    }

    override fun getItemCount(): Int = cartItems.size

    private fun updateTotalPrice() {
        val totalPrice = cartItems.sumOf { it.product.price.toDouble() * it.quantity }
        onQuantityChanged(totalPrice)
    }

    private fun calculateTotalPrice(): Double {
        return cartItems.sumOf { it.product.price.toDouble() * it.quantity }
    }

    private fun showConfirmationDialog(view: View, action: String, onConfirm: () -> Unit) {
        val context = view.context
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder.setTitle("Confirmar acción")
        builder.setMessage("¿Está seguro que desea $action?")
        builder.setPositiveButton("Yes") { _, _ -> onConfirm() }
        builder.setNegativeButton("No", null)
        builder.show()
    }
}