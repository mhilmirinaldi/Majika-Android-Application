package com.example.majika.ui.keranjang

import android.content.res.Resources
import android.icu.text.NumberFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.databinding.CardKeranjangItemBinding
import com.example.majika.domain.ItemKeranjang
import com.example.majika.repository.KeranjangRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class KeranjangRecycler(private val repo: KeranjangRepository) : RecyclerView.Adapter<KeranjangRecycler.ViewHolder>() {
    class ViewHolder(val binding: CardKeranjangItemBinding) : RecyclerView.ViewHolder(binding.root)

    private val items: MutableList<ItemKeranjang> = mutableListOf()

    // Update items with a new list
    fun updateItems(newList: List<ItemKeranjang>) {
        this.items.clear()
        this.items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardKeranjangItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val res: Resources = holder.itemView.context.resources
        val item = items[position]
        with (item) {
            with (holder) {
                // Setting up UI
                binding.itemName.text = name
                val formattedNumber = NumberFormat.getNumberInstance(res.configuration.locales[0]).format(price)
                binding.itemPrice.text =  "${currency} ${formattedNumber}"
                binding.itemQuantity.text = quantity.toString()

                // Button minus on click, update repo
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
                // Button plus on click, update repo
                binding.btnPlus.setOnClickListener {
                    GlobalScope.launch {
                        repo.updateItemInKeranjang(ItemKeranjang(item.name, item.currency, item.price, item.quantity+1))
                    }
                }
            }
        }
    }
}
