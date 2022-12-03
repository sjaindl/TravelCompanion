package com.sjaindl.travelcompanion

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sjaindl.travelcompanion.api.geonames.GeoNamesClient
import com.sjaindl.travelcompanion.api.google.GoogleClient
import com.sjaindl.travelcompanion.api.google.GooglePlaceType
import com.sjaindl.travelcompanion.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

data class MainMenuItem(val title: String, val subtitle: String, val drawable: Drawable?)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        //findViewById<TextView>(R.id.main_text).text = createApplicationScreenMessage()

        GlobalScope.launch {
            try {
                val countryCode = GeoNamesClient().fetchCountryCode(34.380460, 118.14372588)
                val result = GoogleClient().searchPlaces(
                    "Los F",
                    34.380460,
                    118.14372588,
                    GooglePlaceType.PointOfInterest.key,
                    "500"
                )

                runOnUiThread {

                    /*
                    val tv = findViewById<TextView>(R.id.main_text)
                    tv.text = countryCode
                    result.results.forEach {
                        tv.text = "${tv.text} + ${it.name}"
                    }

                     */
                }
            } catch (error: Exception) {
                /*
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity.applicationContext,
                        error.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }

                 */
            }
        }
    }
}
