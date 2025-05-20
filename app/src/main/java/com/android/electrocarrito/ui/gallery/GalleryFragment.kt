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
            Product(
                image = "https://oechsle.vteximg.com.br/arquivos/ids/20499023-1000-1000/2601631.jpg?v=638751197774430000",
                name = "Refrigeradora Samsung RT38K5932SL",
                description = "Refrigeradora No Frost de 384L con tecnología Twin Cooling Plus.",
                price = 1599.90
            ),
            Product(
                image = "https://www.lg.com/pe/images/lavadoras/md07505343/gallery/D01.jpg",
                name = "Lavadora LG WT19DSBP",
                description = "Lavadora automática de 19 kg con Smart Inverter y TurboDrum.",
                price = 1299.00
            ),
            Product(
                image = "https://mabe.vtexassets.com/arquivos/ids/158107-800-auto?v=638156737358300000&width=800&height=auto&aspect=true",
                name = "Cocina a Gas Mabe EM7640CFIX0",
                description = "Cocina de 76cm con horno amplio, 6 quemadores y tapa de cristal.",
                price = 1149.50
            ),
            Product(
                image = "https://panasonic.vtexassets.com/arquivos/ids/158905-800-auto?v=638160667895870000&width=800&height=auto&aspect=true",
                name = "Microondas Panasonic NN-ST27JW",
                description = "Microondas 20L con 9 niveles de potencia y función automática.",
                price = 389.90
            )
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