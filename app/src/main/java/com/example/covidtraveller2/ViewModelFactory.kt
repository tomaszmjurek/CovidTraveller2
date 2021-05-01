package com.example.covidtraveller2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.covidtraveller2.map.MapsViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel() as T
        }
        throw IllegalArgumentException("UnknownViewModel")
    }
}