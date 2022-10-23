package com.sjaindl.travelcompanion

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.main_text).text = createApplicationScreenMessage()

        GlobalScope.launch {
            try {
                val code = fetchGeoCode(37.0856432, 25.1488318)
                //val code = fetchGeoCode(0.0, 0.0)
                runOnUiThread {
                    findViewById<TextView>(R.id.main_text).setText(code)
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
