package com.android.electrocarrito.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.electrocarrito.R
import com.android.electrocarrito.dto.Order

class OrderAdapter(private val orderList: List<Order>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderId: TextView = itemView.findViewById(R.id.order_id)
        val orderTotal: TextView = itemView.findViewById(R.id.order_total)
        val orderStatus: TextView = itemView.findViewById(R.id.order_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.orderId.text = "Order ID: ${order.id}"
        holder.orderTotal.text = "Total: $${order.total}"
        holder.orderStatus.text = "Status: ${order.status}"
        holder.itemView.findViewById<TextView>(R.id.payment_method).text = "Payment: ${order.paymentMethod}"
        holder.itemView.findViewById<TextView>(R.id.payment_date).text = "Date: ${order.paymentDate}"
    }

    override fun getItemCount(): Int = orderList.size
}