package com.example.majika.ui.keranjang

import android.content.Intent
import android.icu.text.NumberFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.majika.database.Database
import com.example.majika.databinding.FragmentKeranjangBinding
import com.example.majika.domain.ItemKeranjang
import com.example.majika.repository.KeranjangRepository
import com.example.majika.ui.pembayaran.PembayaranActivity

class KeranjangViewModel : ViewModel() {
    lateinit var keranjang: LiveData<List<ItemKeranjang>>
    var hargaTotal: Float = 0.0F
    var currency: String = ""
}

class KeranjangFragment : Fragment() {
    companion object {
        val HARGA_TOTAL_KEY = "HARGA_TOTAL_KEY"
        val CURRENCY_KEY = "CURRENCY_KEY"
    }

    private lateinit var viewModel: KeranjangViewModel
    private lateinit var repo: KeranjangRepository

    private var _binding: FragmentKeranjangBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKeranjangBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize repo and viewModel
        repo = KeranjangRepository(Database.getDatabase(requireContext()))
        viewModel = ViewModelProvider(this).get(KeranjangViewModel::class.java)
        viewModel.keranjang = repo.keranjang

        // Set up adapter
        val adapter = KeranjangRecycler(repo)
        adapter.updateItems(viewModel.keranjang.value ?: listOf())
        binding.keranjangRecyclerview.adapter = adapter
        binding.keranjangRecyclerview.layoutManager = LinearLayoutManager(requireContext())

        // Set up keranjang observer
        viewModel.keranjang.observe(viewLifecycleOwner) {
            adapter.updateItems(it)     // Update adapter items

            var hargaTotal = 0.0F
            var currency = "IDR"
            viewModel.keranjang.value?.forEach {
                hargaTotal += it.price * it.quantity
                currency = it.currency
            }
            viewModel.hargaTotal = hargaTotal
            viewModel.currency = currency
            val formattedNumber = NumberFormat.getNumberInstance(resources.configuration.locales[0]).format(viewModel.hargaTotal)
            binding.keranjangHargatotal.text = "${viewModel.currency} ${formattedNumber}"
        }

        // Pay button on click
        binding.keranjangPay.setOnClickListener {
            val intent = Intent(this.context, PembayaranActivity::class.java)
            intent.putExtra(CURRENCY_KEY, viewModel.currency)
            intent.putExtra(HARGA_TOTAL_KEY, viewModel.hargaTotal)
            this.startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}