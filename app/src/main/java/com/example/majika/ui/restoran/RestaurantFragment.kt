package com.example.majika.ui.restoran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.R
import com.example.majika.databinding.FragmentRestoranBinding
import com.example.majika.network.BackendApiRestaurant
import kotlinx.coroutines.*

class RestaurantFragment : Fragment() {

    private var _binding: FragmentRestoranBinding? = null
    private val binding get() = _binding!!

    private var listRestaurant: List<Restaurant>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestoranBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = root.findViewById<RecyclerView>(R.id.recycler_view_restoran)
        val layoutManager = LinearLayoutManager(requireContext())

        recyclerView.layoutManager = layoutManager
        val coroutineScope = CoroutineScope(Job() + Dispatchers.Main)
        GlobalScope.launch {
            try {
                listRestaurant = BackendApiRestaurant.restaurantApi.getRestaurants().listRestaurant
                activity?.runOnUiThread{
                    recyclerView.adapter = RecyclerAdapterRestaurant(listRestaurant!!, coroutineScope)
                }
            } catch (e :Exception){
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                }
            }

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}