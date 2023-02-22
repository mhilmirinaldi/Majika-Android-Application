package com.example.majika.domain

/**
 * Data class that represent item in keranjang
 */
data class ItemKeranjang(
    val name: String,
    val currency: String,
    val price: Float,
    val quantity: Int
)
