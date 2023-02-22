package com.example.majika.ui.keranjang

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.R
import com.example.majika.databinding.CardKeranjangItemBinding
import com.example.majika.domain.ItemKeranjang
import com.example.majika.repository.KeranjangRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class KeranjangRecycler(private val items: List<ItemKeranjang>, private val repo: KeranjangRepository) : RecyclerView.Adapter<KeranjangRecycler.ViewHolder>() {
    class ViewHolder(val binding: CardKeranjangItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardKeranjangItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with (item) {
            with (holder) {
                binding.itemName.text = name
                binding.itemCurrency.text = currency
                binding.itemPrice.text = price.toString()
                binding.itemQuantity.text = quantity.toString()

                binding.btnMinus.setOnClickListener {
                    if (quantity == 1) {
                        GlobalScope.launch {
                            repo.deleteItemInKeranjang(item)
                        }
                    } else {
                        GlobalScope.launch {
                            repo.updateItemInKeranjang(ItemKeranjang(item.name, item.currency, item.price, item.quantity-1))
                        }
                    }
                }
                binding.btnPlus.setOnClickListener {
                    GlobalScope.launch {
                        repo.updateItemInKeranjang(ItemKeranjang(item.name, item.currency, item.price, item.quantity+1))
                    }
                }
            }
        }
    }
}
