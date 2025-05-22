package com.android.electrocarrito.ui.shopping

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.electrocarrito.R
import com.android.electrocarrito.dao.AppDatabase
import com.android.electrocarrito.dao.Pago
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
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

        val cardTypeGroup: RadioGroup = view.findViewById(R.id.card_type_group)
        val cardNumberLayout = view.findViewById<TextInputLayout>(R.id.card_number_input)
        val cardNumber = view.findViewById<TextInputEditText>(R.id.card_number)
        val cardExpiryLayout = view.findViewById<TextInputLayout>(R.id.card_expiry_input)
        val cardExpiry = view.findViewById<TextInputEditText>(R.id.card_expiry)
        val cardCvv = view.findViewById<TextInputEditText>(R.id.card_cvv)
        val confirmPaymentButton: Button = view.findViewById(R.id.confirm_payment_button)

        cardNumber.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                // Remove all non-digit chars
                val digits = s.toString().replace("\\D".toRegex(), "")
                // Format with dashes every 4 digits
                val formatted = digits.chunked(4).joinToString("-")
                if (formatted != s.toString()) {
                    cardNumber.setText(formatted)
                    cardNumber.setSelection(formatted.length)
                }

                // Validation: must be 16 digits
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

                // Validation: MM/AA, month 01-12, length 5
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

        confirmPaymentButton.setOnClickListener {
            val selectedCardTypeId = cardTypeGroup.checkedRadioButtonId
            val cardType = when (selectedCardTypeId) {
                R.id.radio_visa -> "VISA"
                R.id.radio_mastercard -> "MASTERCARD"
                R.id.radio_amex -> "AMEX"
                else -> null
            }

            val number = cardNumber.text.toString()
            val expiry = cardExpiry.text.toString()
            val cvv = cardCvv.text.toString()

            if (cardType.isNullOrEmpty() || number.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch (Dispatchers.IO) {
                // 1. Save payment in SQLite
                val db = AppDatabase.getDatabase(requireContext())
                val currentOrden = db.ordenDao().getCurrentOrder()
                val pago = Pago(
                    id = 0, // or auto-generated
                    id_orden = currentOrden[0].id,
                    red_pago = cardType,
                    numero_tarjeta = number,
                    exp_date = expiry,
                    cvv = cvv,
                    monto = currentOrden[0].total,
                    estado_pago = "Pagado"
                )
                db.pagoDao().insert(pago)

                // 2. Prepare JSON body
                val prefs = requireActivity().getSharedPreferences("auth_prefs", AppCompatActivity.MODE_PRIVATE)
                val id_usuario = prefs.getInt("id_usuario", 0)

                val jsonBody = JSONObject().apply {
                    put("id_usuario", id_usuario)
                    put("total", pago.monto)

                    put("productos", JSONArray().apply {
                        val orderDetalle = db.ordenDetalleDao().getByOrderId(currentOrden[0].id)
                        for (item in orderDetalle) {
                            put(JSONObject().apply {
                                put("id_producto", item.id_producto)
                                put("cantidad", item.cantidad)
                            })
                        }
                    })

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
                            Toast.makeText(context, "Pago procesado y enviado", Toast.LENGTH_SHORT).show()
                        },
                        { error ->
                            Toast.makeText(context, "Error al enviar el pago", Toast.LENGTH_SHORT).show()
                        }
                    )
                    queue.add(request)
                }
            }
        }

        return view
    }
}