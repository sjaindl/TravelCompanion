package com.sjaindl.travelcompanion.explore.details

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class ExploreDetailActivity : ComponentActivity() {

    companion object {
        const val PIN_ID = "PIN_ID"
    }

    private val pinId by lazy { intent.extras?.getLong(PIN_ID) ?: throw IllegalStateException("PIN_ID arg not given") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ExploreDetailContainer(pinId = pinId)
        }
    }
}
