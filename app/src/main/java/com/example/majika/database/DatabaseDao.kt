package com.example.majika.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DatabaseDao {
    @Query("SELECT * from item_keranjang")
    fun getItemKeranjang(): LiveData<List<ItemKeranjangInDB>>

    @Update
    fun update(itemKeranjang: ItemKeranjangInDB)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(itemKeranjang: ItemKeranjangInDB)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(itemKeranjangs: List<ItemKeranjangInDB>)
}
