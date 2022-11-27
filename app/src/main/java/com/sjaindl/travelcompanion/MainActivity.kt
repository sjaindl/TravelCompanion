package com.sjaindl.travelcompanion

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sjaindl.travelcompanion.api.geonames.GeoNamesClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.main_text).text = createApplicationScreenMessage()

        GlobalScope.launch {
            try {
                val countryCode = GeoNamesClient().fetchCountryCode(37.0856432, 35.1488318)
                //val code = fetchGeoCode(0.0, 0.0)
                runOnUiThread {
                    findViewById<TextView>(R.id.main_text).text = countryCode
                }
            } catch (error: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity.applicationContext,
                        error.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }
    }
}
