package com.example.majika.ui.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.R

class RecyclerAdapter(private val items: List<com.example.majika.ui.menu.Item>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tittleView: TextView = itemView.findViewById(R.id.card_tittle)
        val priceView: TextView = itemView.findViewById(R.id.card_price)
        val soldView: TextView = itemView.findViewById(R.id.card_sold)
        val descriptionView: TextView = itemView.findViewById(R.id.card_description)
        val quantityView: TextView = itemView.findViewById(R.id.card_quantity)
        val minusButton: Button = view.findViewById(R.id.btn_minus)
        val plusButton: Button = view.findViewById(R.id.btn_plus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_menu_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = items[position]
        holder.tittleView.text = item.title
        holder.priceView.text = item.price
        holder.soldView.text = item.sold
        holder.descriptionView.text = item.description
        holder.quantityView.text = item.quantity.toString()

        holder.minusButton.setOnClickListener{
            if(item.quantity > 0){
                item.quantity--
                holder.quantityView.text = item.quantity.toString()
            }
        }

        holder.plusButton.setOnClickListener{
            item.quantity++
            holder.quantityView.text = item.quantity.toString()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
