package com.example.majika.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.majika.domain.ItemKeranjang

/**
 * Represent item keranjang entity in database
 */
@Entity(tableName = "item_keranjang", primaryKeys = arrayOf("name", "price", "currency"))
data class ItemKeranjangInDB(
    val name: String,
    val price: Float,
    val currency: String,
    val quantity: Int
)

/**
 * Map ItemKeranjangInDB to ItemKeranjang model
 */
fun List<ItemKeranjangInDB>.asDomainModel(): List<ItemKeranjang> {
    return map {
        ItemKeranjang(
            name = it.name,
            currency = it.currency,
            price = it.price,
            quantity = it.quantity
        )
    }
}

fun ItemKeranjang.asDatabaseModel(): ItemKeranjangInDB {
    return ItemKeranjangInDB(
        name = this.name,
        currency = this.currency,
        price = this.price,
        quantity = this.quantity
    )
}
