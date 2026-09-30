package com.example.flightsearch.models

data class Flight(
    val departureId: Long,
    val departureCode: String,
    val departureName: String,
    val arrivalId: Long,
    val arrivalCode: String,
    val arrivalName: String,
    val isFavorite: Boolean = false
)