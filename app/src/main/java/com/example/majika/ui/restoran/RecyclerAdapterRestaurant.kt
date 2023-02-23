package com.example.majika.ui.restoran

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class RecyclerAdapterRestaurant(private val restaurants : List<Restaurant>, private val coroutineScope : CoroutineScope): RecyclerView.Adapter<RecyclerAdapterRestaurant.ViewHolder>(){
    private val compareName = Comparator<Restaurant>{restaurant1, restaurant2 ->
        restaurant1.name_restaurant.compareTo(restaurant2.name_restaurant, ignoreCase = true)
    }
    private val sortedList = restaurants.sortedWith(compareName)
    
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val nameRestaurantView: TextView = itemView.findViewById(R.id.card_name)
        val addressView: TextView = itemView.findViewById(R.id.card_address)
        val phoneNumberView: TextView = itemView.findViewById(R.id.card_contact)
        val buttonMaps: Button = itemView.findViewById(R.id.btn_maps)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_restoran, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var restaurant = sortedList[position]
        holder.nameRestaurantView.text = restaurant.name_restaurant
        holder.addressView.text = restaurant.address
        holder.phoneNumberView.text = restaurant.phone_number

        holder.buttonMaps.setOnClickListener{
            val uri = Uri.parse("geo:${restaurant.latitude},${restaurant.longitude}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            coroutineScope.launch {
                ContextCompat.startActivity(holder.itemView.context, intent, null)
            }
        }
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

}