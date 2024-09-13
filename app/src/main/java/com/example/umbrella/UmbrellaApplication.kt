package com.example.umbrella

import android.app.Application
import com.example.umbrella.data.AppContainer
import com.example.umbrella.data.AppDataContainer

class UmbrellaApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}

//class UmbrellaApplication : Application() {
//    val container: AppContainer by lazy { AppDataContainer(this) }
//}
