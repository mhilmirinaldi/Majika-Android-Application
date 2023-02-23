package com.example.majika.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.R
import com.example.majika.database.Database
import com.example.majika.databinding.FragmentMenuBinding
import com.example.majika.domain.ItemKeranjang
import com.example.majika.network.BackendApiItem
import com.example.majika.repository.KeranjangRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MenuFragmentViewModel: ViewModel() {
    lateinit var keranjang: LiveData<List<ItemKeranjang>>
}

class MenuFragment : Fragment() {
    private lateinit var repo: KeranjangRepository
    private lateinit var viewModel: MenuFragmentViewModel

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private var listItem: List<Item>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root

        repo = KeranjangRepository(Database.getDatabase(requireContext()))
        viewModel = ViewModelProvider(this).get(MenuFragmentViewModel::class.java)
        viewModel.keranjang = repo.keranjang

        val recyclerView = root.findViewById<RecyclerView>(R.id.recycler_view_makanan)
        val layoutManager = LinearLayoutManager(requireContext())

        recyclerView.layoutManager = layoutManager
        GlobalScope.launch {
            try {
                listItem = BackendApiItem.itemApi.getItems().listItem
                if (listItem != null) {
                    viewModel.keranjang.value?.forEach {
                        for (i in 0 until listItem!!.size) {
                            if (listItem!![i].title == it.name) {
                                listItem!![i].quantity = it.quantity
                            }
                        }
                    }
                    activity?.runOnUiThread {
                        recyclerView.adapter = RecyclerAdapterMenu(listItem!!, repo)
                    }
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.keranjang.observe(viewLifecycleOwner) {
            if (listItem != null) {
                it.forEach {
                    for (i in 0 until listItem!!.size) {
                        if (listItem!![i].title == it.name) {
                            listItem!![i].quantity = it.quantity
                        }
                    }
                }
                recyclerView.adapter = RecyclerAdapterMenu(listItem!!, repo)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}