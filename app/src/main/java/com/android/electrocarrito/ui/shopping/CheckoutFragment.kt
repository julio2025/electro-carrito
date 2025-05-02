package com.android.electrocarrito.ui.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.electrocarrito.R

class CheckoutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_checkout, container, false)

        val cardTypeGroup: RadioGroup = view.findViewById(R.id.card_type_group)
        val cardNumber: EditText = view.findViewById(R.id.card_number)
        val cardExpiry: EditText = view.findViewById(R.id.card_expiry)
        val cardCvv: EditText = view.findViewById(R.id.card_cvv)
        val confirmPaymentButton: Button = view.findViewById(R.id.confirm_payment_button)

        confirmPaymentButton.setOnClickListener {
            val selectedCardTypeId = cardTypeGroup.checkedRadioButtonId
            val cardType = when (selectedCardTypeId) {
                R.id.radio_visa -> "VISA"
                R.id.radio_mastercard -> "MASTERCARD"
                else -> null
            }

            val number = cardNumber.text.toString()
            val expiry = cardExpiry.text.toString()
            val cvv = cardCvv.text.toString()

            if (cardType.isNullOrEmpty() || number.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Pago procesado con $cardType", Toast.LENGTH_SHORT).show()
                // Aquí puedes agregar la lógica para procesar el pago
            }
        }

        return view
    }
}