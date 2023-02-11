package com.example.majika.ui.restoran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.majika.databinding.FragmentRestoranBinding

class RestoranFragment : Fragment() {

    private var _binding: FragmentRestoranBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val restoranViewModel =
            ViewModelProvider(this).get(RestoranViewModel::class.java)

        _binding = FragmentRestoranBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textRestoran
        restoranViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}