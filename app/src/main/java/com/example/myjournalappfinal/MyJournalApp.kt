package com.example.myjournalappfinal

import android.app.Application
import com.cloudinary.android.MediaManager

class MyJournalApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Cloudinary
        val config = mapOf(
            "cloud_name" to "dwhv4gi2w",
            "api_key" to "592795373161672",
            "api_secret" to "MxXS93RfRi5XHJ8n3eakcR_c6_Q"
        )
        MediaManager.init(this, config)
    }
}