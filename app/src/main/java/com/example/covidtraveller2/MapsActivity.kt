package com.example.covidtraveller2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.covidtraveller2.map.MapsEvent
import com.example.covidtraveller2.map.MapsViewModel
import com.example.covidtraveller2.model.Country

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

private const val TAG = "MAP_ACTIVITY"
private val POLAND = LatLng(51.919438, 19.145136)

/**
 * Ustawianie znaczników na mapie z MapsVM
 * Przejścia do innych ekranów
 * Obsługa wyboru kraju destynacji
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var viewModel: MapsViewModel
    private lateinit var mMap: GoogleMap
    private var selectedMarker : Marker? = null
    private var positionTO : LatLng? = null
    private var countries = arrayListOf<Country>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val factory = ViewModelFactory()
        viewModel = ViewModelProvider(this,factory).get(MapsViewModel::class.java) //todo doczytać dlaczego

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initListeners()
        initData()
    }

    private fun initData() {
        val inputStream = applicationContext.assets.open("countries_locations.csv")
        viewModel.readCountriesFromCsv(inputStream)
    }

    private fun initListeners() {
        viewModel.event.observe(this, Observer { event ->
            when(event) {
                is MapsEvent.CountriesReadSuccess -> {
                    countries = event.countries
                    countries.forEach { c ->
                        //todo warunek mapReady
                        mMap.addMarker(MarkerOptions().position(LatLng(c.latitude, c.longitude)).title(c.name))
                    }
                    Log.i(TAG, "Finished pinning counties on map")
                }
                is MapsEvent.CountriesReadFailed -> {
                    // todo Dialog and close the app
                }
            }
        })

//        resultBtn.setOnClickListener {  triggerResult() }
//        articlesBtn.setOnClickListener { openArticlesActivity() }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(POLAND, 5F))
        mMap.setOnMarkerClickListener {
            onMarkerClick(it)
            // Return false to indicate that we have not consumed the event and that we wish
            // for the default behavior to occur (which is for the camera to move such that the
            // marker is centered and for the marker's info window to open, if it has one).
            false
        }
    }

    private fun onMarkerClick(marker : Marker) {
        val markerLatLng = marker.position

        if /* not selected */ (positionTO != null) {
            if /* clicked selected */ (markerLatLng == positionTO) {
                positionTO = null
                selectedMarker = null
                displayMarkerAsDefault(marker)
                Log.i(TAG, "Marker at TO deselected")
            } /* clicked other */ else {
                displayMarkerAsDefault(selectedMarker!!)
                Log.i(TAG, "Deselected previous TO: " + selectedMarker!!.position.latitude + ", " + selectedMarker!!.position.longitude)
                positionTO = markerLatLng
                selectedMarker = marker
                displayMarkerToSelect(marker)
                Log.i(TAG, "Selected TO: " + positionTO.toString())
            }
        } /* clicked first */ else {
            positionTO = markerLatLng
            selectedMarker = marker
            displayMarkerToSelect(marker)
            Log.i(TAG, "Selected TO: " + positionTO.toString())
        }
    }

    private fun displayMarkerAsDefault(marker: Marker) {
        marker.title = ""
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
    }

    private fun displayMarkerToSelect(marker: Marker) {
        marker.title = "TO"
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
    }


//todo maybe move to other class
    private fun getCountryByLatLng(searched: LatLng) : String {
        countries.forEach { country ->
            if (country.latitude.equals(searched.latitude))
                if (country.longitude.equals(searched.longitude))
                    return country.name
        }
        return "incorrect" //todo manage
    }

    //todo manage null
    private fun triggerResult() {
        if (positionTO != null) {
            val countryTO = getCountryByLatLng(positionTO!!)
            Log.i(TAG, "Selected TO: $countryTO")

//            val i = Intent(this@MapsActivity, ResultActivity::class.java)
//            i.putExtra("countryTO", countryTO)
//            startActivity(i)
        }
    }

    private fun openArticlesActivity() {
//        val i = Intent(this@MapsActivity, ArticlesActivity::class.java)
//        startActivity(i)
    }
}