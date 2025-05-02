package com.android.electrocarrito.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.electrocarrito.R
import com.android.electrocarrito.adapter.ProductAdapter
import com.android.electrocarrito.dto.Product

class GalleryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var adapter: ProductAdapter
    private lateinit var productList: MutableList<Product>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        searchBar = view.findViewById(R.id.search_bar)

        // Sample data
        productList = mutableListOf(
            Product(R.drawable.product1, "Product 1", "Description 1", "$10"),
            Product(R.drawable.product2, "Product 2", "Description 2", "$20"),
            Product(R.drawable.product3, "Product 3", "Description 3", "$30"),
            Product(R.drawable.product4, "Product 4", "Description 4", "$40")
        )

        adapter = ProductAdapter(productList)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = adapter

        // Search functionality
        searchBar.addTextChangedListener { text ->
            val filteredList = productList.filter {
                it.name.contains(text.toString(), ignoreCase = true)
            }
            adapter = ProductAdapter(filteredList)
            recyclerView.adapter = adapter
        }

        return view
    }
}