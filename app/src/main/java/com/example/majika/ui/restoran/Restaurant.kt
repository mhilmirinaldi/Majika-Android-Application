package com.example.majika.ui.restoran

import com.squareup.moshi.Json

data class Restaurant(
    @Json (name = "name") val name_restaurant: String,
    @Json (name = "popular_food") val popular_food : String,
    @Json (name = "address") val address : String,
    @Json (name = "contact_person") val contact_person : String,
    @Json (name = "phone_number") val phone_number : String,
    @Json (name = "longitude") val longitude : Float,
    @Json (name = "latitude") val latitude : Float
)

data class RestaurantResponse (
    @Json(name = "data") val listRestaurant: List<Restaurant>,
    @Json(name = "size") val size : Int
)