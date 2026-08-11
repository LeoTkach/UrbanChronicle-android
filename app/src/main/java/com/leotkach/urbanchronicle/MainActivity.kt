package com.leotkach.urbanchronicle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.leotkach.urbanchronicle.ui.UrbanChronicleApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as UrbanChronicleApplication
        setContent {
            UrbanChronicleApp(repository = app.repository)
        }
    }
}
