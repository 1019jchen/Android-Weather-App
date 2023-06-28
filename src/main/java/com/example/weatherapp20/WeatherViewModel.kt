package com.example.weatherapp20

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.converter.gson.GsonConverterFactory

//Business logic => ViewModel


class WeatherViewModel: ViewModel() {

    //mutablelivedata = read and write, livedata = only read
    private val _weatherLiveData = MutableLiveData<WeatherModel>()
    val weatherLiveData: LiveData<WeatherModel> = _weatherLiveData

    private val _geoLiveData = MutableLiveData<GeoModel>()
    val geoLiveData: LiveData<GeoModel> = _geoLiveData

    private val _cityList = MutableLiveData<MutableList<String>>()
    val cityList: MutableLiveData<MutableList<String>> = _cityList


    val weatherApi = RetrofitHelper
        .getInstance()
        .newBuilder()
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherInterface::class.java)

    val geoApi = RetrofitHelper
        .getInstance()
        .newBuilder()
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GeoInterface::class.java)


    private fun fetchWeather(lat: Double?, lon: Double?){
        viewModelScope.launch {
            val result = weatherApi.fetchData(lat, lon, "a1c661878193772742f70e661bcd22f8", "imperial")
            _weatherLiveData.postValue(result)
        }
    }

    fun fetchGeography(city: String?){

        viewModelScope.launch {
            val result = city?.let { geoApi.fetchData(it, 1, "a1c661878193772742f70e661bcd22f8") }
            _geoLiveData.postValue(result?.get(0) ?: null)
            fetchWeather(result?.get(0)?.lat, result?.get(0)?.lon)
        }
    }

    fun addCity(city: String){
        _cityList.value?.let{list ->
            list.add(city)
            _cityList.postValue(list)
        } ?:  (
                _cityList.postValue(mutableListOf(city))
                )

        }

    fun removeCity(city: String){
        _cityList.value?.let{cityList ->
            cityList.remove(city)
            _cityList.postValue(cityList)
        }
    }

}