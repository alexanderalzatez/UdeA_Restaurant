package com.alexanderalzate.udea_restaurant

import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_maps.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onMarkerClick(p0: Marker?) = false

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.getUiSettings().setZoomControlsEnabled(true)
        setUpMapMiUbicacion()
        map.setOnMarkerClickListener(this)

        var McDonalds : ArrayList<Restaurant> = ArrayList()

        McDonalds.add(Restaurant("Bello, Puerta del norte",LatLng(6.339509, -75.544344)))
        McDonalds.add(Restaurant("Envigado, Viva Envigado",LatLng(6.178337, -75.589473)))
        McDonalds.add(Restaurant("Mayorca, Mega plaza 2",LatLng(6.160176, -75.603803)))
        McDonalds.add(Restaurant("Envigado, JUMBO",LatLng(6.187420, -75.582337)))
        McDonalds.add(Restaurant("Medellín, Santa fé",LatLng(6.197361, -75.573539)))
        McDonalds.add(Restaurant("Medellín, Los molinos",LatLng(6.2329665,-75.606626)))
        McDonalds.add(Restaurant("Itagüí, JR",LatLng(6.1715571,-75.6192542)))
        McDonalds.add(Restaurant("Sabaneta, Polideportivo Sur",LatLng(6.1555283,-75.6198519)))
        McDonalds.add(Restaurant("Medellín, Bosque Plaza",LatLng(6.2691315,-75.5650466)))

        for(restaurant in McDonalds){
           //var markerRestaurant =  map.addMarker(MarkerOptions().position(restaurant.latlng).title(restaurant.nombre))
            val markerOptionsRestaurant = MarkerOptions().position(restaurant.latlng)
            markerOptionsRestaurant.title(restaurant.nombre)
            markerOptionsRestaurant.icon(
                BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(resources, R.drawable.mcdonalds)))
            map.addMarker(markerOptionsRestaurant)

            /*val markerInfoWindowAdapter = InfoWndowAdapter(applicationContext)
            map.setInfoWindowAdapter(markerInfoWindowAdapter)*/
        }

        //map.setOnInfoWindowClickListener(this)

        val latLngOrigin = LatLng(6.268194, -75.568760) // Universidad De Antioquia 6.268194, -75.568760
        val latLngDestination = LatLng(6.3394457,-75.5435257) // Puerta del norte

        map.addMarker(MarkerOptions().position(latLngDestination).title("Puerta del norte").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
        map.addMarker(MarkerOptions().position(latLngOrigin).title("Universidad de Antioquia").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOrigin, 12f))
        bn_Buscar.setOnClickListener {

            var geocoder = Geocoder(this)
            var ubicacionOrigen = atv_Origen.text.toString()
            var ubicacionDestino = atv_Destino.text.toString()

            //  var ubicacion = "Cra 2B N 19-04 Campestre B Dosquebradas Risaralda"
            lateinit var listOrigen : MutableList<Address>
            lateinit var listDestino : MutableList<Address>


            try {
                listOrigen = geocoder.getFromLocationName(ubicacionOrigen,1)
                listDestino = geocoder.getFromLocationName(ubicacionDestino,1)

            }catch (e: IOException){

            }
            if (listOrigen.size > 0 && listDestino.size>0) {

                var addressOrigen = listOrigen.get(0)
                var addressDestino = listDestino.get(0)
                var positionOrigen = LatLng(addressOrigen.latitude, addressOrigen.longitude)
                var positionDestino = LatLng(addressDestino.latitude, addressDestino.longitude)
                var markerOrigen = MarkerOptions().title(ubicacionOrigen).position(positionOrigen)
                var markerDestino = MarkerOptions().title(ubicacionDestino).position(positionDestino)
                map.addMarker(markerOrigen)
                map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(positionOrigen, 15F)
                )
                map.addMarker(markerDestino)
                map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(positionDestino, 15F)
                )
                val URL = getDirectionURL(positionOrigen,positionDestino)
                GetDirection(URL).execute()
            } else
                Toast.makeText(this, "Direccion no encontrada", Toast.LENGTH_SHORT).show()

        }


        val URL = getDirectionURL(latLngOrigin,latLngDestination)
        GetDirection(URL).execute()


    }

    private fun getDirectionURL(origin:LatLng,dest:LatLng):String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&key=AIzaSyBoV3rGS2S-f_8-FB5bHa23f9Buhdx5f4I&mode=driving"
    }

    override fun onInfoWindowClick(p0: Marker?) {
        Toast.makeText(this,"Info window Tapped",Toast.LENGTH_SHORT).show()
    }


    inner class GetDirection(val url:String) : AsyncTask<Void,Void,List<List<LatLng>>>(){
        override fun doInBackground(vararg p0: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data  = response.body()!!.string()

            val result = ArrayList<List<LatLng>>()

            try{

                val respObj = Gson().fromJson(data,GoogleMapDTO::class.java)
                val path = ArrayList<LatLng>()

                for(i in 0..(respObj.routes[0].legs[0].steps.size-1)){
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)


            }catch (e:Exception){
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>?) {
            val lineoption = PolylineOptions()
            for (i in result!!.indices){
                lineoption.addAll(result[i])
                lineoption.width(5f)
                lineoption.color(Color.BLUE)
                lineoption.geodesic(true)
            }
            map.addPolyline(lineoption)
        }

    }

    fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
    }

    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location)
        markerOptions.title("Estoy aquí!!")
        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(resources, R.mipmap.ic_user_location)))
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
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
