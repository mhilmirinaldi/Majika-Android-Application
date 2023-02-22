package com.example.majika.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.R
import com.example.majika.databinding.FragmentMenuBinding
import com.example.majika.network.BackendApiItem
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

//    val itemList = listOf(
//        Items("Makanan A", 10000, 1, "Makanan sehat dan bergizi", "Makanan"),
//        Items("Makanan V", 10000, 2, "Makanan sehat dan bergizi", "Makanan"),
//        Items("Makanan D", 10000, 3, "Makanan sehat dan bergizi", "Makanan"),
//        Items("Makanan W", 10000, 5,  "Makanan sehat dan bergizi", "Makanan"),
//        Items("Makanan X", 10000, 5, "Makanan sehat dan bergizi", "Makanan"),
//        Items("Makanan W", 10000, 2, "Makanan sehat dan bergizi", "Makanan")
//    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = root.findViewById<RecyclerView>(R.id.recycler_view_makanan)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        GlobalScope.launch {
            val itemList = BackendApiItem.itemApi.getItems()
            val adapter = RecyclerAdapter(itemList.listItem)
            activity?.runOnUiThread({
                recyclerView.adapter = adapter
            })
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}