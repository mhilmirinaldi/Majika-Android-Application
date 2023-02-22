package com.example.majika.ui.keranjang

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.camera.core.ExperimentalGetImage
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.majika.database.Database
import com.example.majika.databinding.FragmentKeranjangBinding
import com.example.majika.domain.ItemKeranjang
import com.example.majika.repository.KeranjangRepository
import com.example.majika.ui.pembayaran.PembayaranActivity

class KeranjangViewModel : ViewModel() {
    lateinit var keranjang: LiveData<List<ItemKeranjang>>
}

@ExperimentalGetImage class KeranjangFragment : Fragment() {
    private lateinit var repo: KeranjangRepository
    private lateinit var viewModel: KeranjangViewModel

    private var _binding: FragmentKeranjangBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKeranjangBinding.inflate(inflater, container, false)
        val root: View = binding.root

        repo = KeranjangRepository(Database.getDatabase(requireContext()))
        viewModel = ViewModelProvider(this).get(KeranjangViewModel::class.java)
        viewModel.keranjang = repo.keranjang

        binding.keranjangRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        viewModel.keranjang.observe(viewLifecycleOwner) {
            binding.keranjangRecyclerview.adapter = KeranjangRecycler(it, repo)
        }

        binding.payButtonFloating.setOnClickListener {
            val intent = Intent(this.context, PembayaranActivity::class.java)
            this.startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}