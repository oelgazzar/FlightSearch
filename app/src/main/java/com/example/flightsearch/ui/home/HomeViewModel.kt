package com.example.flightsearch.ui.home

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.data.AppDatabase
import com.example.flightsearch.data.FlightSearchDao
import com.example.flightsearch.models.Airport
import com.example.flightsearch.models.Flight
import com.example.flightsearch.models.toFlight
import com.example.flightsearch.models.updateFavoriteStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class UiState(
    val query: String = "",
    val showSuggestions: Boolean = false,
    val airportSuggestions: List<Airport> = listOf(),
    val searchResults: List<Flight> = listOf(),
    val favoriteFlights: List<Flight> = listOf()
)

class HomeViewModel(
    private val flightSearchDao: FlightSearchDao,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    /* Query */
    private val _query = MutableStateFlow("")

    /* Suggestion List */
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val airportSuggestions = _query
        .debounce(300.milliseconds)
        .flatMapLatest { q ->
            flightSearchDao.searchAirports(q)
        }.stateIn(
            viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = listOf()
        )

    /* Show Suggestion flag */
    private val _showSuggestions = MutableStateFlow(false)

    /* Search Trigger */
    private val _searchTrigger = MutableSharedFlow<Long?>(
        extraBufferCapacity = 1
    )

    /* Favorite Flights */
    private val favoriteFlights: StateFlow<List<Flight>> =
        flightSearchDao.getFavoritesWithAirports().map { favoritesWithAirports ->
            favoritesWithAirports.map { (favorite, airports) -> favorite.toFlight(airports) }
        }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf()
        )

    /* Search Results */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _searchResults = _searchTrigger.flatMapLatest { id ->
        id?.let {
            flightSearchDao.getAllPossibleFlights(it)
        } ?: flowOf(emptyList())
    }
        .combine(favoriteFlights) { searchResults, favoriteFlights ->
            searchResults.updateFavoriteStatus(favoriteFlights)
        }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    /* Ui State */
    val uiState = combine(
        _query, _showSuggestions, airportSuggestions, _searchResults, favoriteFlights
    ) { query, showSuggestions, airportSuggestions, searchResults, favoriteFlights ->
        UiState(
            query = query,
            showSuggestions = showSuggestions,
            airportSuggestions = airportSuggestions,
            searchResults = searchResults,
            favoriteFlights = favoriteFlights
        )
    }.stateIn(
        viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = UiState()
    )

    /* Events */
    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    init {
        viewModelScope.launch {
            val preferences = dataStore.data.first()
            _query.value = preferences[QUERY_KEY]?:""
            _searchTrigger.tryEmit(preferences[ID_KEY])
        }
    }


    /* ---------------------------------- Methods ----------------------------- */
    fun updateQuery(query: String) {
        _query.value = query
        _showSuggestions.value = query.isNotBlank()
        if (query.isBlank()) {
            hideSuggestions()
            // Clear search results when query is empty
            _searchTrigger.tryEmit(null)
            saveQuery(null)
        }
    }

    fun clearQuery() {
        updateQuery("")
    }

    fun selectSuggestion(departureAirport: Airport) {
        _searchTrigger.tryEmit(departureAirport.id)
        updateQuery(departureAirport.iataCode)
        hideSuggestions()
        saveQuery(departureAirport.id)
    }

    fun saveQuery(id: Long?) {
        viewModelScope.launch {
            dataStore.edit {
                it[QUERY_KEY] = _query.value
                it[ID_KEY] = id?: Long.MIN_VALUE
            }
        }
    }

    fun hideSuggestions() {
        _showSuggestions.value = false
        viewModelScope.launch {
            _uiEvents.send(UiEvent.HideKeyboard)
        }
    }

    fun toggleFavorite(flight: Flight) {
        viewModelScope.launch {
            if (flight.isFavorite) {
                flightSearchDao.deleteFavorite(flight.departureCode, flight.arrivalCode)
            } else {
                flightSearchDao.insertFavorite(
                    departureCode = flight.departureCode, destinationCode = flight.arrivalCode
                )
                // Update search list with new favorite
                _searchTrigger.tryEmit(flight.departureId)
            }
        }
    }

    companion object {
        val QUERY_KEY = stringPreferencesKey("query")
        val ID_KEY = longPreferencesKey("id")

        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!
                val flightSearchDao =
                    AppDatabase.getInstance(application.applicationContext).flightSearchDao()
                HomeViewModel(flightSearchDao, application.dataStore)
            }
        }
    }
}

val Context.dataStore by preferencesDataStore(USER_PREFERENCES)
const val USER_PREFERENCES = "user_preferences"

sealed interface UiEvent {
    object HideKeyboard : UiEvent
}