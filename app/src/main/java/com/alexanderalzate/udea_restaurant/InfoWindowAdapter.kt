package com.alexanderalzate.udea_restaurant

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.open_info_window.*

class InfoWndowAdapter(context: Context) : InfoWindowAdapter,AppCompatActivity() {

    private val context: Context? = null

     override fun getInfoWindow(marker: Marker):View? {
        return null
    }

    override fun getInfoContents(marker: Marker):View {

        val layoutInflater:LayoutInflater = LayoutInflater.from(applicationContext)

        val view: View = layoutInflater.inflate(
            R.layout.open_info_window, // Custom view/ layout
            rootLayout, // Root layout to attach the view
            false // Attach with root layout or not
        )


        var tvTitle:TextView = view.findViewById(R.id.title)
        var tvSnippet:TextView = view.findViewById(R.id.snippet)
        tvTitle.setText("Gir Forest is located in the Gujarat State of India")
        tvSnippet.setText("Forest is maily known for the Asiatic Lions.")

        return view
    }


}