package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Query
import com.example.flightsearch.models.Airport
import com.example.flightsearch.models.FavoriteFlight
import com.example.flightsearch.models.Flight
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightSearchDao {
    @Query("SELECT * FROM favorite")
    fun getFavorites(): Flow<List<FavoriteFlight>>

    @Query("SELECT * FROM favorite, airport WHERE departure_code = iata_code OR destination_code = iata_code")
    fun getFavoritesWithAirports(): Flow<Map<FavoriteFlight, List<Airport>>>

    @Query("SELECT * FROM airport ORDER BY passengers DESC")
    fun getAllAirports(): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE name LIKE '%' || :query || '%' OR iata_code LIKE '%' || :query || '%'")
    fun searchAirports(query: String): Flow<List<Airport>>

    @Query("""SELECT a1.id AS departureId, a1.name AS departureName, a1.iata_code AS departureCode,
        a2.id AS arrivalId, a2.name AS arrivalName, a2.iata_code AS arrivalCode, 0 AS isFavorite
        FROM airport AS a1 JOIN airport AS a2 ON a1.id != a2.id WHERE a1.id = :airportId""")
    fun getAllPossibleFlights(airportId: Long): Flow<List<Flight>>

    @Query("DELETE FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode")
    suspend fun deleteFavorite(departureCode: String, destinationCode: String)

    @Query("INSERT INTO favorite (departure_code, destination_code) VALUES (:departureCode, :destinationCode)")
    suspend fun insertFavorite(departureCode: String, destinationCode: String)
}