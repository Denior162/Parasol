package com.example.parasol

import android.app.Application
import com.example.parasol.data.AppContainer
import com.example.parasol.data.AppDataContainer

//@HiltAndroidApp
class ParasolApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
