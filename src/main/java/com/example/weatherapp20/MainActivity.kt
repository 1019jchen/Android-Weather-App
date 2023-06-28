package com.example.weatherapp20

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


//1. Query
//2. Nullable
//3. Search

class MainActivity : ComponentActivity() {
    lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        //map class name to view model
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        setContent {
            MaterialTheme {

                var weatherState = viewModel.weatherLiveData.observeAsState()
                var geoState = viewModel.geoLiveData.observeAsState()
                var cities = viewModel.cityList.observeAsState()
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                        Weather(weatherState.value, geoState.value, cities.value,
                            onLocationClick = {city ->
                                if (city.isNotEmpty()) {
                                    viewModel.fetchGeography(city)
                                }
                            },
                            onSearchClicked = {city ->
                                viewModel.addCity(city)
                            },
                            removeCity = {city ->
                                viewModel.removeCity(city)
                            })
                    }


            }
        }
    }

}




@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun Weather(weatherState: WeatherModel?, geoState: GeoModel?, cityList: MutableList<String>?, onLocationClick: (city: String) -> Unit, onSearchClicked: (city:String) -> Unit, removeCity: (city:String)->Unit) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { searchView(weatherState, geoState, cityList,
            onLocationClick=onLocationClick, onSearchClicked, removeCity = removeCity, navController) }
        composable("PDP") { productDescriptionPage(geoState, weatherState, navController) }
    }

    }

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun productDescriptionPage(geo: GeoModel?, weather: WeatherModel?, navController: NavHostController){
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("James's Incredible Weather Project") }
            )
        },

        ) {
        Column(
            modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top
        ) {
            if (geo != null) {
                if (!geo.name.isNullOrEmpty()) {
                    if (geo.state == null){
                        Text(
                            text = "" + geo.name + ", " + geo.country,
                            style = MaterialTheme.typography.h4,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    else{Text(
                        text = "" + geo.name + ", " + geo.state + ", " + geo.country,
                        style = MaterialTheme.typography.h4,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )}
                }
            }
            if (weather != null) {
//                        Text(
//                            text = "Weather: ${weather.main?.temp} F",
//                            style = MaterialTheme.typography.subtitle1
//                        )
            }
            Text("Coordinates: ${geo?.lat}º, ${geo?.lon}º.")
            Text(
        text = "The temperature in ${geo?.name} is ${weather?.main?.temp}ºF!" ,
    )
            Text("If you step outside, you can expect to see ${weather?.weather?.get(0)?.description}.")
            Text("It currently feels like ${weather?.main?.feels_like}ºF.")
            Text("The temperature today will range from ${weather?.main?.temp_min} to ${weather?.main?.temp_max}ºF.")
            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(text = "Back")
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun searchView(weatherState: WeatherModel?, geoState: GeoModel?, cityList: MutableList<String>?, onLocationClick: (city: String) -> Unit, onSearchClicked: (city:String) -> Unit, removeCity: (city:String)->Unit, navController: NavHostController) {
    val searchQueryState = remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("James's Incredible Weather Project") }
            )
        },

    ){
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = searchQueryState.value,
                placeholder = {Text("Enter your city name")},
                onValueChange = {searchQueryState.value = it},
            )

            Spacer(modifier = Modifier.padding(2.dp))

            Button(
                onClick = {onSearchClicked(searchQueryState.value) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(text = "Search")
            }
            Spacer(modifier = Modifier.padding(15.dp))
            Text("Cities searched will show up here:")
            CenteredTextBoxListView(cities = cityList, onLocationClick = onLocationClick, removeCity = removeCity, navController)
//            Text(
//                text = "The temperature in ${geoState?.name} is ${weatherState?.main?.temp}ºF! Coordinates: ${geoState?.lat}º, ${geoState?.lon}º." ,
//            )
//            ListItem("${geoState?.name}",
//                onDelete = {},
//                onClick = {})
        }
}



//    Text(
//        text = "The temperature in ${geoState?.name} is ${weatherState?.main?.temp}ºF! Coordinates: ${geoState?.lat}º, ${geoState?.lon}º." ,
//    )
}

@Composable
fun ListItem(item: String, onDelete: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick.invoke() }
                .padding(16.dp)
        ) {
            Text(
                text = item,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { onDelete.invoke() }
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun CenteredTextBoxListView(cities: MutableList<String>?, onLocationClick: (city:String)->Unit, removeCity: (city:String)-> Unit, navController: NavHostController) {

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        if(!cities.isNullOrEmpty()){
            items(cities) { textBox ->
                ListItem(textBox,
                    onDelete = {removeCity(textBox)},
                    onClick = {
                        onLocationClick(textBox)
                        navController.navigate("PDP")},
                    )
            }
        }

    }
}




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MaterialTheme {
        //Weather()
    }
}