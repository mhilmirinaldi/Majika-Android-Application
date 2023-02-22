package com.example.majika.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

@androidx.room.Database(entities = arrayOf(ItemKeranjangInDB::class), version = 1, exportSchema = false)
public abstract class Database : RoomDatabase() {
    abstract fun dao(): DatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: Database? = null

        fun getDatabase(context: Context): Database {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Database::class.java,
                    "majika"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
