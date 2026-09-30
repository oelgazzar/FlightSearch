package com.example.flightsearch.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
data class FavoriteFlight(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "departure_code")
    val departureCode: String,
    @ColumnInfo(name = "destination_code")
    val destinationCode: String
)

fun FavoriteFlight.toFlight(airports: List<Airport>) = Flight(
    departureId = airports.first().id,
    departureCode = departureCode,
    departureName = airports.first().name,
    arrivalId = airports.last().id,
    arrivalCode = destinationCode,
    arrivalName = airports.last().name,
    isFavorite = true
)

fun List<Flight>.updateFavoriteStatus(favoriteFlights: List<Flight>) = map { flight ->
    if (favoriteFlights.any { it.departureCode == flight.departureCode && it.arrivalCode == flight.arrivalCode }) {
        flight.copy(isFavorite = true)
    } else {
        flight
    }
}

