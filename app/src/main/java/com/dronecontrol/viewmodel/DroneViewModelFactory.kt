package com.dronecontrol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dronecontrol.data.DroneRepository

/**
 * Factory for creating [DroneViewModel] with dependency injection of [DroneRepository].
 */
class DroneViewModelFactory(
    private val repository: DroneRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DroneViewModel::class.java)) {
            return DroneViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
