package com.example.flightsearch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flightsearch.models.Airport
import com.example.flightsearch.models.FavoriteFlight

@Database(entities = [Airport::class, FavoriteFlight::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun flightSearchDao(): FlightSearchDao

    companion object {
        @Volatile
        var Instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = synchronized(this) {
            Instance ?: Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                .createFromAsset("database/flight_search.db")
                .build().also {
                    Instance = it
                }
        }
    }
}