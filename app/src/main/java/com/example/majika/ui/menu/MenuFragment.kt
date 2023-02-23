package com.example.majika.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.majika.database.Database
import com.example.majika.databinding.FragmentMenuBinding
import com.example.majika.domain.ItemKeranjang
import com.example.majika.network.BackendApiItem
import com.example.majika.repository.KeranjangRepository
import kotlinx.coroutines.launch
import java.util.*

class MenuFragmentViewModel: ViewModel() {
    lateinit var keranjang: LiveData<List<ItemKeranjang>>
}

class MenuFragment : Fragment() {
    private lateinit var repo: KeranjangRepository
    private lateinit var viewModel: MenuFragmentViewModel
    private lateinit var adapter: RecyclerAdapterMenu

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private var listItem: List<Item> = listOf()

    private var isMakananSelected = false
    private var isMinumanSelected = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize repo and viewModel
        repo = KeranjangRepository(Database.getDatabase(requireContext()))
        viewModel = ViewModelProvider(this).get(MenuFragmentViewModel::class.java)
        viewModel.keranjang = repo.keranjang

        // Set up adapter
        adapter = RecyclerAdapterMenu(repo)
        binding.recyclerViewMakanan.adapter = adapter
        binding.recyclerViewMakanan.layoutManager = LinearLayoutManager(requireContext())

        // Fetch data
        lifecycleScope.launch {
            try {
                // Call API
                listItem = BackendApiItem.itemApi.getItems().listItem

                // Update list item quantity from keranjang
                viewModel.keranjang.value?.forEach {
                    for (i in 0 until listItem.size) {
                        if (listItem[i].title == it.name) {
                            listItem[i].quantity = it.quantity
                        }
                    }
                }

                // Update adapter
                adapter.updateItems(listItem)
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // If keranjang change, update item's quantity in Menu
        viewModel.keranjang.observe(viewLifecycleOwner) {
            it.forEach {
                for (i in 0 until listItem.size) {
                    if (listItem[i].title == it.name) {
                        listItem[i].quantity = it.quantity
                    }
                }
            }
            adapter.updateItems(listItem)
        }

        // Filtering button on click
        binding.btnAll.setOnClickListener {
            isMakananSelected = false
            isMinumanSelected = false
            adapter.updateItems(filterListItems())
        }

        binding.btnMakanan.setOnClickListener {
            isMakananSelected = true
            isMinumanSelected = false
            adapter.updateItems(filterListItems("Food"))
        }

        binding.btnMinuman.setOnClickListener {
            isMakananSelected = false
            isMinumanSelected = true
            adapter.updateItems(filterListItems("Drink"))
        }

        // Search query
        binding.cariMenu.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0 != null) {
                    searchItems(p0.lowercase(Locale.getDefault()))
                }
                return true
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
        })

        return root
    }

    private fun filterListItems(filter: String? = null): List<Item> {
        return if (filter.isNullOrEmpty()) {
            listItem
        } else {
            listItem.filter { it.type == filter }
        }
    }

    private fun searchItems(query: String, filter: String? = null) {
        val filteredList = if (isMakananSelected) {
            listItem.filter { it.type == "Food" && it.title.lowercase(Locale.getDefault()).contains(query) }
        } else if (isMinumanSelected){
            listItem.filter { it.type == "Drink" && it.title.lowercase(Locale.getDefault()).contains(query) }
        } else{
            listItem.filter { it.title.lowercase(Locale.getDefault()).contains(query) }
        }

        adapter.updateItems(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}