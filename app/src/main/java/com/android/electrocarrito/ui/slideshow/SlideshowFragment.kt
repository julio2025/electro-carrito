package com.android.electrocarrito.ui.slideshow

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.electrocarrito.R
import com.android.electrocarrito.adapter.OrderAdapter
import com.android.electrocarrito.dto.CartItem
import com.android.electrocarrito.dto.Order
import com.android.electrocarrito.dto.OrderStatus
import com.android.electrocarrito.dto.PaymentMethod
import com.android.electrocarrito.dto.Product
import java.time.LocalDate

class SlideshowFragment : Fragment() {

    private val sampleProduct = Product(
        image = "https://www.lg.com/content/dam/channel/wcms/pe/images/refrigeradoras/gs66sgp/gallery/DZ-01.jpg/_jcr_content/renditions/thum-1600x1062.jpeg", // usa un drawable genérico o de prueba
        name = "Refrigeradora Samsung",
        description = "Frío inteligente de 500L",
        price = 45.0
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private val orders = listOf(
        Order("1", listOf(CartItem(sampleProduct, 1)), 45.00, OrderStatus.ATENDIDO, PaymentMethod.VISA, LocalDate.of(2023, 10, 1)),
        Order("2", listOf(CartItem(sampleProduct, 2)), 90.00, OrderStatus.RECHAZADO, PaymentMethod.AMEX, LocalDate.of(2024, 8, 4)),
        Order("3", listOf(CartItem(sampleProduct, 1)), 60.00, OrderStatus.RECHAZADO, PaymentMethod.MASTERCARD, LocalDate.of(2025, 3, 23)),
        Order("4", listOf(CartItem(sampleProduct, 1)), 25.00, OrderStatus.RECHAZADO, PaymentMethod.MASTERCARD, LocalDate.of(2023, 5, 16)),
        Order("5", listOf(CartItem(sampleProduct, 1)), 80.00, OrderStatus.ATENDIDO, PaymentMethod.VISA, LocalDate.of(2024, 7, 19))
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_slideshow, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.orders_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = OrderAdapter(orders) { selectedOrder ->
            // Aquí puedes abrir una nueva pantalla con los detalles
            Toast.makeText(requireContext(), "Ver detalle de orden #${selectedOrder.id}", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}