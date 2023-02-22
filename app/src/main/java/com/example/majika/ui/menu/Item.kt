package com.example.majika.ui.menu

import com.squareup.moshi.Json

data class Item (
    @Json(name = "name") val title: String,
    @Json(name = "price") val price: String,
    @Json(name = "currency") val currency: String,
    @Json(name = "sold") val sold: String,
    @Json(name = "description") val description: String,
    @Json(name = "type") val type : String,
    var quantity: Int = 0
)

data class ItemResponse (
    @Json(name = "data") val listItem: List<Item>,
    @Json(name = "size") val size : Int
        )

// {
//    val formattedPrice: String
//        get() = "Rp $price"
//
//    val formattedSold: String
//        get() = "$sold Terjual"
//}
