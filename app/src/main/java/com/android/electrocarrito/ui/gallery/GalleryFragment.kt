package com.android.electrocarrito.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.electrocarrito.R
import com.android.electrocarrito.adapter.ProductAdapter
import com.android.electrocarrito.dao.AppDatabase
import com.android.electrocarrito.dao.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var adapter: ProductAdapter

    private var productList: List<Producto> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        searchBar = view.findViewById(R.id.search_bar)

        val activity = requireActivity()

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(activity.applicationContext)
            productList = db.productoDao().getAll()
            withContext(Dispatchers.Main) {
                adapter = ProductAdapter(productList)
                recyclerView.adapter = adapter

                // Set up search functionality
                searchBar.addTextChangedListener { text ->
                    filterProducts(text.toString())
                }
            }
        }

        recyclerView.layoutManager = GridLayoutManager(context, 2)

        return view
    }

    private fun filterProducts(query: String) {
        val filteredList = if (query.isEmpty()) {
            productList
        } else {
            productList.filter {
                it.nombre.contains(query, ignoreCase = true)
            }
        }
        adapter.updateData(filteredList)
    }
}