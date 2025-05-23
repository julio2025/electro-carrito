package com.android.electrocarrito.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.android.electrocarrito.R
import com.android.electrocarrito.dto.Order
import com.bumptech.glide.Glide
import java.time.format.DateTimeFormatter

class OrderAdapter(
    private val orderList: List<Order>,
    private val onOrderClick: (Order) -> Unit
) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderId: TextView = itemView.findViewById(R.id.textOrderId)
        val date: TextView = itemView.findViewById(R.id.textDate)
        val itemCount: TextView = itemView.findViewById(R.id.textItemCount)
        val total: TextView = itemView.findViewById(R.id.textTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]

        val totalItems = order.items.sumOf { it.quantity }
        val totalAmount = order.total
        val dateFormatted = order.paymentDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))

        holder.orderId.text = "Orden #${order.id}"
        holder.date.text = "Pagado el $dateFormatted"
        holder.itemCount.text = "$totalItems productos"
        holder.total.text = "Total: S/ ${"%.2f".format(totalAmount)}"

        holder.itemView.setOnClickListener { onOrderClick(order) }
    }

    override fun getItemCount(): Int = orderList.size
}