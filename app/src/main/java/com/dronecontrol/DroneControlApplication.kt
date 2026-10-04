package com.dronecontrol

import android.app.Application
import com.dronecontrol.data.DroneRepository

/**
 * Application class providing top-level dependency container for the GCS application.
 */
class DroneControlApplication : Application() {

    lateinit var repository: DroneRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = DroneRepository()
    }
}
