package com.alexanderalzate.udea_restaurant

import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {


    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onMarkerClick(p0: Marker?) = false

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
        map = googleMap

        data class Restaurant(var nombre:String ,var latlng: LatLng)
        var McDonalds : ArrayList<Restaurant> = ArrayList()

        McDonalds.add(Restaurant("Bello, Puerta del norte",LatLng(6.339509, -75.544344)))
        McDonalds.add(Restaurant("Envigado, Viva Envigado",LatLng(6.178337, -75.589473)))
        McDonalds.add(Restaurant("Mayorca, Mega plaza 2",LatLng(6.160176, -75.603803)))
        McDonalds.add(Restaurant("Envigado, JUMBO",LatLng(6.187420, -75.582337)))
        McDonalds.add(Restaurant("Medellín, Santa fé",LatLng(6.197361, -75.573539)))
        McDonalds.add(Restaurant("Medellín, Los molinos",LatLng(6.2329665,-75.606626)))
        McDonalds.add(Restaurant("Itagüí, JR",LatLng(6.1715571,-75.6192542)))
        McDonalds.add(Restaurant("Sabaneta, Polideportivo Sur",LatLng(6.1555283,-75.6198519)))
        McDonalds.add(Restaurant("Medellín, Bosque Plaza",LatLng(6.2592477,-75.5669041)))



        for(restaurant in McDonalds){
            map.addMarker(MarkerOptions().position(restaurant.latlng).title(restaurant.nombre))
        }

        // Add a marker in Sydney and move the camera
        /*val sydney = LatLng(-34.0, 151.0)
        val McDonaldPuertaNorte=LatLng(6.339509, -75.544344)
        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))*/


        map.getUiSettings().setZoomControlsEnabled(true)
        map.setOnMarkerClickListener(this)
        /*val myPlace = LatLng(40.73, -73.99)  // this is New York
        map.addMarker(MarkerOptions().position(myPlace).title("My Favorite City"))
        map.moveCamera(CameraUpdateFactory.newLatLng(myPlace))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, 12.0f))*/
        setUpMapMiUbicacion()
        // 1


    }

    private fun placeMarkerOnMap(location: LatLng) {
        // 1
        val markerOptions = MarkerOptions().position(location)
        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(resources, R.mipmap.ic_user_location)))
        // 2
        map.addMarker(markerOptions)
        map.moveCamera(CameraUpdateFactory.newLatLng(location))
    }


    private fun setUpMapMiUbicacion() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        map.isMyLocationEnabled = true

// 2
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            // 3
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                //map.addMarker(MarkerOptions().position(currentLatLng).title("Aquí estoy yo!"))

            }
        }
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
