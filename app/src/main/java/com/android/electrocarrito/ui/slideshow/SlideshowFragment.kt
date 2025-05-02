package com.android.electrocarrito.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.electrocarrito.R
import com.android.electrocarrito.adapter.OrderAdapter
import com.android.electrocarrito.dto.Order

class SlideshowFragment : Fragment() {

    private val orders = listOf(
        Order("1", listOf(), "45.00", "Delivered", "Credit Card", "2023-10-01"),
        Order("2", listOf(), "30.50", "Pending", "PayPal", "2023-10-02"),
        Order("3", listOf(), "60.00", "Shipped", "Debit Card", "2023-10-03"),
        Order("4", listOf(), "25.00", "Delivered", "Credit Card", "2023-10-04"),
        Order("5", listOf(), "80.00", "Cancelled", "Bank Transfer", "2023-10-05"),
        Order("6", listOf(), "15.00", "Pending", "Credit Card", "2023-10-06"),
        Order("7", listOf(), "50.00", "Shipped", "PayPal", "2023-10-07"),
        Order("8", listOf(), "100.00", "Delivered", "Debit Card", "2023-10-08"),
        Order("9", listOf(), "40.00", "Pending", "Credit Card", "2023-10-09"),
        Order("10", listOf(), "75.00", "Shipped", "Bank Transfer", "2023-10-10")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_slideshow, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.orders_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = OrderAdapter(orders)

        return view
    }
}