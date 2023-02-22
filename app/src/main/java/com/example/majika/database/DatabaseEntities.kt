package com.example.majika.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.majika.domain.ItemKeranjang

/**
 * Represent item keranjang entity in database
 */
@Entity(tableName = "item_keranjang")
data class ItemKeranjangInDB(
    @PrimaryKey
    val name: String,
    val price: Float,
    val quantity: Int
)

/**
 * Map ItemKeranjangInDB to ItemKeranjang model
 */
fun List<ItemKeranjangInDB>.asDomainModel(): List<ItemKeranjang> {
    return map {
        ItemKeranjang(
            name = it.name,
            price = it.price,
            quantity = it.quantity
        )
    }
}
