package com.example.majika.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.map
import com.example.majika.database.Database
import com.example.majika.database.ItemKeranjangInDB
import com.example.majika.database.asDatabaseModel
import com.example.majika.database.asDomainModel
import com.example.majika.domain.ItemKeranjang
import com.example.majika.network.BackendApiItem
import kotlinx.coroutines.*

class KeranjangRepository(private val database: Database) {
    val keranjang: LiveData<List<ItemKeranjang>> = Transformations.map(database.dao().getItemKeranjang()) {
        it.asDomainModel()
    }

    suspend fun refreshKeranjang() {
        withContext(Dispatchers.IO) {
            val menus = BackendApiItem.itemApi.getItems()
            val itemsKeranjang = keranjang.value
            val latestItemsKeranjang = mutableListOf<ItemKeranjangInDB>()

            // Only retain items in keranjang database with corresponding menu on network
            itemsKeranjang?.forEach {itemKeranjang ->
                for (i in 0 until menus.listItem.size) {
                    if (itemKeranjang.name == menus.listItem[i].title) {
                        val menu = menus.listItem[i]
                        latestItemsKeranjang.add(ItemKeranjangInDB(name = menu.title, currency = menu.currency, price = menu.price, quantity = itemKeranjang.quantity))
                        break
                    }
                }
            }

            // Clear and insert to database
            database.dao().deleteAll()
            database.dao().insertAll(latestItemsKeranjang)
        }
    }

    suspend fun addItemToKeranjang(itemKeranjang: ItemKeranjang) {
        withContext(Dispatchers.IO) {
            database.dao().insert(itemKeranjang.asDatabaseModel())
        }
    }

    suspend fun updateItemInKeranjang(itemKeranjang: ItemKeranjang) {
        withContext(Dispatchers.IO) {
            database.dao().update(itemKeranjang.asDatabaseModel())
        }
    }

    suspend fun deleteItemInKeranjang(itemKeranjang: ItemKeranjang) {
        withContext(Dispatchers.IO) {
            database.dao().delete(itemKeranjang.asDatabaseModel())
        }
    }
}
