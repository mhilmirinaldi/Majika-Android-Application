package com.example.majika.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.majika.database.Database
import com.example.majika.database.asDomainModel
import com.example.majika.domain.ItemKeranjang
import kotlinx.coroutines.*

class KeranjangRepository(private val database: Database) {
    val keranjang: LiveData<List<ItemKeranjang>> = Transformations.map(database.dao().getItemKeranjang(), {
        it.asDomainModel()
    })

    suspend fun refreshKeranjang() {
        withContext(Dispatchers.IO) {
            // TODO: update keranjang info from backend
        }
    }
}
