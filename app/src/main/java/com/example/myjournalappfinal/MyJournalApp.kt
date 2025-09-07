package com.example.myjournalappfinal

import android.app.Application
import com.cloudinary.android.MediaManager

class MyJournalApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Cloudinary
        val config = mapOf(
            "cloud_name" to "",
            "api_key" to "",
            "api_secret" to ""
        )
        MediaManager.init(this, config)
    }
}