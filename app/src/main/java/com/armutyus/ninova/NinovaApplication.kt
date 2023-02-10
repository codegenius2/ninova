package com.armutyus.ninova

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NinovaApplication : Application() {
    companion object {
        lateinit var instance: NinovaApplication private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}