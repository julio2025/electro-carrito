package com.android.electrocarrito.ui.slideshow

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.electrocarrito.R
import com.android.electrocarrito.adapter.OrderAdapter
import com.android.electrocarrito.dao.AppDatabase
import com.android.electrocarrito.dto.CartItem
import com.android.electrocarrito.dto.Order
import com.android.electrocarrito.dto.OrderStatus
import com.android.electrocarrito.dto.PaymentMethod
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SlideshowFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_slideshow, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.orders_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)

        fun loadOrdersFromDb() {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(requireContext())
                val ordenes = db.ordenDao().getAll()

                val orders = ordenes.map { orden ->
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val dateTime = LocalDateTime.parse(orden.fecha, formatter)
                    val date = dateTime.toLocalDate()

                    val pago = db.pagoDao().getByOrderId(orden.id)
                    val ordenDetalle = db.ordenDetalleDao().getByOrderId(orden.id)
                    val cartItems = ordenDetalle.map { item ->
                        val producto = db.productoDao().getById(item.id_producto)
                        CartItem(producto, item.cantidad)
                    }
                    Order(
                        id = orden.id.toString(),
                        items = cartItems,
                        total = orden.total,
                        status = when (orden.estado) {
                            "ATENDIDO" -> OrderStatus.ATENDIDO
                            "RECHAZADO" -> OrderStatus.RECHAZADO
                            else -> OrderStatus.ATENDIDO
                        },
                        paymentMethod = when (pago.getOrNull(0)?.red_pago) {
                            "Visa" -> PaymentMethod.VISA
                            "Mastercard" -> PaymentMethod.MASTERCARD
                            "Amex" -> PaymentMethod.AMEX
                            else -> PaymentMethod.VISA
                        },
                        paymentDate = date
                    )
                }
                withContext(Dispatchers.Main) {
                    recyclerView.adapter = OrderAdapter(orders) { selectedOrder ->
                        Toast.makeText(
                            requireContext(),
                            "Ver detalle de orden #${selectedOrder.id}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    swipeRefresh.isRefreshing = false
                }
            }
        }

        swipeRefresh.setOnRefreshListener {
            val queue = Volley.newRequestQueue(requireContext())
            val prefs = requireActivity().getSharedPreferences("auth_prefs", 0)
            val id_usuario = prefs.getInt("id_usuario", 0)
            val url = "https://i66aeqax65.execute-api.us-east-1.amazonaws.com/v1/mis-ordenes?id_usuario=$id_usuario"

            val getOrdersRequest = JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                { response ->
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        val db = AppDatabase.getDatabase(requireContext())
                        val ordenesArray = response.getJSONArray("data")
                        db.ordenDao().deleteAll()
                        for (i in 0 until ordenesArray.length()) {
                            val ordenJson = ordenesArray.getJSONObject(i)
                            val orden = com.android.electrocarrito.dao.Orden(
                                id = ordenJson.getInt("id_orden"),
                                id_usuario = id_usuario,
                                fecha = ordenJson.getString("fecha"),
                                estado = ordenJson.getString("estado"),
                                total = ordenJson.getDouble("total"),
                                vigente = false
                            )
                            db.ordenDao().insert(orden)
                        }
                        withContext(Dispatchers.Main) {
                            loadOrdersFromDb()
                        }
                    }
                },
                { error ->
                    swipeRefresh.isRefreshing = false
                    Toast.makeText(context, "Error fetching orders", Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(getOrdersRequest)
        }

        loadOrdersFromDb()
        return view
    }
}