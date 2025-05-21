package com.android.electrocarrito.ui.logout

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.electrocarrito.R
import com.android.electrocarrito.WelcomeActivity
import com.android.electrocarrito.dao.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.core.content.edit

class LogoutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_logout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.cerrar_sesion))
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                val prefs = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                prefs.edit { clear() }

                lifecycleScope.launch(Dispatchers.IO) {
                    val db = AppDatabase.getDatabase(requireContext())
                    db.clearAllTables()

                    launch(Dispatchers.Main) {
                        val intent = Intent(requireContext(), WelcomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            }
            .setNegativeButton("Cancelar") { _, _ ->
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            .show()
    }
}