package com.example.covidtraveller2.map

import com.example.covidtraveller2.model.Country

sealed class MapsEvent {

    data class CountriesReadSuccess(val countries: ArrayList<Country>) : MapsEvent()
    object CountriesReadFailed : MapsEvent()
}