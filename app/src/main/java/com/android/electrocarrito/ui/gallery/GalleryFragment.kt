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

        // Get for sqlite with room
        val db = AppDatabase.getDatabase(activity.applicationContext)

        lifecycleScope.launch(Dispatchers.IO) {
            productList = db.productoDao().getAll()
            Log.i("====>Count", productList.size.toString())
            withContext(Dispatchers.Main) {
                adapter = ProductAdapter(productList)
                recyclerView.adapter = adapter
            }
        }

        recyclerView.layoutManager = GridLayoutManager(context, 2)

        return view
    }
}