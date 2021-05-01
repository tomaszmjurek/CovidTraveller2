package com.example.covidtraveller2.map

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.covidtraveller2.model.Country
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.IOException
import java.lang.Exception

/**
 * Wczytywanie lokalizacji z CSV
 *
 */

class MapsViewModel : ViewModel() {
    private val TAG = MapsViewModel::class.java.simpleName

    var event = MutableLiveData<MapsEvent>()

    fun readCountriesFromCsv() {
        val tsvReader = csvReader {
            charset = "UTF-8"
            delimiter = ';'
            skipMissMatchedRow = true
        }

        try {
            //todo wczytywanie z assets
            tsvReader.open("countries_locations.csv") {
                var countries = ArrayList<Country>()
                readAllAsSequence().forEach { row ->
                    var country = Country(
                        row[0],
                        row[1].toDouble(),
                        row[2].toDouble()
                    )
                    Log.i(TAG, country.toString())
                    countries.add(country)
                }
                event.postValue(MapsEvent.CountriesReadSuccess(countries))
                Log.i(TAG, "Finished reading countries from file")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while reading countries from file", e)
            event.postValue(MapsEvent.CountriesReadFailed)
        }
    }
}