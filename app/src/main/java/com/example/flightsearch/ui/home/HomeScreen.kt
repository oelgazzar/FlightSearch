package com.example.flightsearch.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.models.Airport
import com.example.flightsearch.models.Flight
import com.example.flightsearch.ui.theme.FlightSearchTheme
import com.example.test.close
import com.example.test.filledStar
import com.example.test.plane_contrails
import com.example.test.search
import com.example.test.star

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                UiEvent.HideKeyboard -> {
                    keyboardController?.hide()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Flight Search",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =
                        MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        HomeBody(
            uiState = uiState,
            onSearchQueryChanged = viewModel::updateQuery,
            onClearQuery = viewModel::clearQuery,
            onSuggestionClick = viewModel::selectSuggestion,
            onToggleFavoriteFlight = viewModel::toggleFavorite,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun HomeBody(
    uiState: UiState,
    onSearchQueryChanged: (String) -> Unit,
    onClearQuery: () -> Unit,
    onSuggestionClick: (Airport) -> Unit,
    onToggleFavoriteFlight: (Flight) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        SearchInput(
            value = uiState.query,
            onValueChange = onSearchQueryChanged,
            onClear = onClearQuery,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            when {
                uiState.searchResults.isNotEmpty() ->
                    FlightList(
                        uiState.searchResults,
                        toggleFavorite = onToggleFavoriteFlight,
                    )

                uiState.favoriteFlights.isNotEmpty() ->
                    FavoriteFlightList(
                        uiState.favoriteFlights,
                        toggleFavorite = onToggleFavoriteFlight
                    )

                else -> EmptySearchResultList(modifier = Modifier.fillMaxSize())
            }

            if (uiState.showSuggestions) {
                SuggestionList(
                    uiState.airportSuggestions,
                    onSuggestionClick = onSuggestionClick,
                    modifier = Modifier
                        .offset(0.dp, (-24).dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .zIndex(1f)
                )
            }
        }
    }
}

@Composable
fun EmptySearchResultList(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .wrapContentSize()
    ) {
        Icon(
            plane_contrails,
            null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Use Search Bar to look for your next journey",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
fun FavoriteFlightList(
    flights: List<Flight>,
    toggleFavorite: (Flight) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text("Favorite routes",
            modifier = Modifier.padding(bottom = 16.dp))
        FlightList(
            flights = flights,
            toggleFavorite = toggleFavorite,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun FlightList(
    flights: List<Flight>,
    toggleFavorite: (Flight) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(flights) { flight ->
            FlightItem(
                flight = flight,
                toggleFavorite = { toggleFavorite(flight) },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun FlightItem(
    flight: Flight,
    toggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("DEPART")
                Text(
                    "${flight.departureCode}  ${flight.departureName}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text("Arrival")
                Text("${flight.arrivalCode} ${flight.arrivalName}")
            }
            IconButton(
                onClick = toggleFavorite,
            ) {
                Icon(
                    imageVector = if (flight.isFavorite) filledStar else star,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                )
            }
        }
    }
}

@Composable
fun SuggestionList(
    airportSuggestionList: List<Airport>,
    modifier: Modifier = Modifier,
    onSuggestionClick: (Airport) -> Unit = {}
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        airportSuggestionList.forEach { airport ->
            SuggestionResult(
                airport = airport,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSuggestionClick(airport)
                    }
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun SuggestionResult(
    airport: Airport,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = airport.iataCode,
            modifier = Modifier.padding(end = 8.dp),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        )
        Text(
            text = airport.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SearchInput(
    value: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        shape = MaterialTheme.shapes.extraLarge,
        leadingIcon = {
            Icon(
                imageVector = search,
                contentDescription = null
            )
        },

        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = close,
                        contentDescription = null,
                    )
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            focusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
            focusedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        placeholder = {
            Text("Search")
        },
        modifier = modifier
            .zIndex(2f)
    )
}

//@Preview
//@Composable
//fun SearchResultListPreview() {
//    FlightSearchTheme {
//        SearchResultList(listOf())
//    }
//}

//@Preview()
//@Composable
//fun SearchResultItemPreview() {
//    FlightSearchTheme {
//        SearchResultItem(
//            flight = "SVO  Sheremetyevo - A.S. Pushkin international airport",
//            modifier = Modifier.padding(8.dp)
//        )
//    }
//}

//@Preview
//@Composable
//fun SuggestionListPreview() {
//    FlightSearchTheme {
//        SuggestionList(
//            airportSuggestionList = listOf(
//                "SVO  Sheremetyevo - A.S. Pushkin international airport",
//                "MUC  Munich International Airport",
//                "DUS  Düsseldorf International Airport",
//                "ATH  Athens International Airport",
//                "LYS  Lyon-Saint Exupéry Airport",
//                "FCO  Leonardo da Vinci International Airport",
//                "VIE  Vienna International Airport",
//                "KEF  Keflavik International Airport",
//            )
//        )
//    }
//}

//@Preview
//@Composable
//fun SuggestionResultPreview() {
//    FlightSearchTheme {
//        SuggestionResult(
//            airport = "SVO  Sheremetyevo - A.S. Pushkin international airport",
//            modifier = Modifier.fillMaxWidth()
//        )
//    }
//}


@Preview
@Composable
fun HomeBodyPreview() {
    FlightSearchTheme {
        HomeBody(
            uiState = UiState(),
            onSearchQueryChanged = {},
            onSuggestionClick = {},
            onClearQuery = {},
            onToggleFavoriteFlight = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}