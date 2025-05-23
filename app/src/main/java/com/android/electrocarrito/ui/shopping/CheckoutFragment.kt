package com.android.electrocarrito.ui.shopping

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.android.electrocarrito.MainActivity
import com.android.electrocarrito.R
import com.android.electrocarrito.dao.AppDatabase
import com.android.electrocarrito.dao.Orden
import com.android.electrocarrito.dao.Pago
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class CheckoutFragment : Fragment() {

    @SuppressLint("CutPasteId", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_checkout, container, false)

        val radioVisa = view.findViewById<RadioButton>(R.id.radio_visa)
        val radioMastercard = view.findViewById<RadioButton>(R.id.radio_mastercard)
        val radioAmex = view.findViewById<RadioButton>(R.id.radio_amex)
        val cardNumberLayout = view.findViewById<TextInputLayout>(R.id.card_number_input)
        val cardNumber = view.findViewById<TextInputEditText>(R.id.card_number)
        val cardExpiryLayout = view.findViewById<TextInputLayout>(R.id.card_expiry_input)
        val cardExpiry = view.findViewById<TextInputEditText>(R.id.card_expiry)
        val cardCvv = view.findViewById<TextInputEditText>(R.id.card_cvv)
        val confirmPaymentButton: Button = view.findViewById(R.id.confirm_payment_button)
        val loadingOverlay = view.findViewById<View>(R.id.loadingOverlay)

        cardNumber.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                val digits = s.toString().replace("\\D".toRegex(), "")

                val formatted = digits.chunked(4).joinToString("-")
                if (formatted != s.toString()) {
                    cardNumber.setText(formatted)
                    cardNumber.setSelection(formatted.length)
                }

                cardNumberLayout.error = if (digits.length == 16) null else "Debe tener 16 dígitos"

                isFormatting = false
            }
        })

        cardExpiry.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                val digits = s.toString().replace("[^\\d]".toRegex(), "")
                var formatted = digits
                if (digits.length > 2) {
                    formatted = digits.substring(0, 2) + "/" + digits.substring(2, Math.min(4, digits.length))
                }
                if (formatted != s.toString()) {
                    cardExpiry.setText(formatted)
                    cardExpiry.setSelection(formatted.length)
                }

                cardExpiryLayout.error = null
                if (formatted.length == 5) {
                    val month = formatted.substring(0, 2).toIntOrNull()
                    if (month == null || month !in 1..12) {
                        cardExpiryLayout.error = "Mes inválido"
                    }
                } else if (formatted.isNotEmpty()) {
                    cardExpiryLayout.error = "Formato MM/AA"
                }

                isFormatting = false
            }
        })

        var cardType: String? = null

        confirmPaymentButton.setOnClickListener {
            if (radioVisa.isChecked) {
                cardType = "VISA"
            } else if (radioMastercard.isChecked) {
                cardType = "MASTERCARD"
            } else if (radioAmex.isChecked) {
                cardType = "AMEX"
            }

            val number = cardNumber.text.toString()
            val expiry = cardExpiry.text.toString()
            val cvv = cardCvv.text.toString()

            if (cardType.isNullOrEmpty() || number.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Confirmar pago")
                .setMessage("¿Desea confirmar el pago y procesar la orden?")
                .setPositiveButton("Sí") { _, _ ->
                    viewLifecycleOwner.lifecycleScope.launch (Dispatchers.IO) {
                        withContext(Dispatchers.Main) {
                            loadingOverlay.visibility = View.VISIBLE
                            confirmPaymentButton.isEnabled = false
                        }

                        // 1. Save payment in SQLite
                        val db = AppDatabase.getDatabase(requireContext())
                        val currentOrden = db.ordenDao().getCurrentOrder()
                        val pago = Pago(
                            id = 0, // or auto-generated
                            id_orden = currentOrden[0].id,
                            red_pago = cardType!!,
                            numero_tarjeta = number,
                            exp_date = expiry,
                            cvv = cvv,
                            monto = currentOrden[0].total,
                            estado_pago = "Pagado"
                        )

                        withContext(Dispatchers.IO) {
                            db.pagoDao().insert(pago)
                        }

                        // 2. Prepare JSON body
                        val prefs = requireActivity().getSharedPreferences("auth_prefs", AppCompatActivity.MODE_PRIVATE)
                        val id_usuario = prefs.getInt("id_usuario", 0)

                        val productosArray = withContext(Dispatchers.IO) {
                            val orderDetalle = db.ordenDetalleDao().getByOrderId(currentOrden[0].id)
                            JSONArray().apply {
                                for (item in orderDetalle) {
                                    put(JSONObject().apply {
                                        put("id_producto", item.id_producto)
                                        put("cantidad", item.cantidad)
                                    })
                                }
                            }
                        }

                        val jsonBody = JSONObject().apply {
                            put("id_usuario", id_usuario)
                            put("total", pago.monto)
                            put("productos", productosArray)
                            put("red_pago", pago.red_pago)
                            put("numero_tarjeta", pago.numero_tarjeta)
                            put("exp_date", pago.exp_date)
                            put("cvv", cvv)
                            put("monto", pago.monto)
                        }

                        // 3. POST to API
                        withContext(Dispatchers.Main) {
                            val queue = Volley.newRequestQueue(requireContext())
                            val url = "https://i66aeqax65.execute-api.us-east-1.amazonaws.com/v1/orden"

                            val request = JsonObjectRequest(
                                Request.Method.POST,
                                url,
                                jsonBody,
                                { response ->
                                    // Get orders by API
                                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                        db.ordenDao().deleteAll()
                                        db.ordenDetalleDao().deleteAll()
                                        withContext(Dispatchers.Main) {
                                            val mainActivity = activity as MainActivity
                                            mainActivity.clearBadge(R.id.nav_shopping)

                                            getOrdersByAPI(id_usuario)
                                        }
                                    }
                                },
                                { error ->
                                    loadingOverlay.visibility = View.GONE
                                    confirmPaymentButton.isEnabled = true
                                    Toast.makeText(context, "Error al enviar el pago", Toast.LENGTH_SHORT).show()
                                }
                            )
                            queue.add(request)
                        }
                    }
                }
                .setNegativeButton("No", null)
                .show()


        }

        return view
    }

    private fun getOrdersByAPI(id_usuario: Int) {
        val loadingOverlay = view?.findViewById<View>(R.id.loadingOverlay)
        val confirmPaymentButton = view?.findViewById<Button>(R.id.confirm_payment_button)

        val queue = Volley.newRequestQueue(requireContext())
        val url = "https://i66aeqax65.execute-api.us-east-1.amazonaws.com/v1/mis-ordenes?id_usuario=$id_usuario"

        val getOrdersRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { getResponse ->
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    val db = AppDatabase.getDatabase(requireContext())
                    val ordenesArray = getResponse.getJSONArray("data")

                    db.ordenDao().deleteAll()

                    for (i in 0 until ordenesArray.length()) {
                        val ordenJson = ordenesArray.getJSONObject(i)
                        // Map JSON to your Orden entity
                        val orden = Orden(
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
                        if (loadingOverlay != null) {
                            loadingOverlay.visibility = View.GONE
                        }
                        if (confirmPaymentButton != null) {
                            confirmPaymentButton.isEnabled = true
                        }

                        Toast.makeText(context, "Pago procesado y enviado", Toast.LENGTH_SHORT).show()

                        //Retornar a ShoppingFragment
                        val navController = view?.findNavController()
                        if (navController != null) {
                            navController.popBackStack(R.id.nav_shopping, false)
                        }

                    }
                }
            },
            { error ->
                Toast.makeText(context, "Error al obtener órdenes", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(getOrdersRequest)
    }
}