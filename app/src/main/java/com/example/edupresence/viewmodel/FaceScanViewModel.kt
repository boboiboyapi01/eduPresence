package com.example.edupresence.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FaceScanViewModel(application: Application) : AndroidViewModel(application) {

    private val _locationVerified = MutableLiveData<Boolean>()
    val locationVerified: LiveData<Boolean> get() = _locationVerified

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private val schoolLocation = Location("school").apply {
        latitude = -6.200000 // Example: Jakarta coordinates
        longitude = 106.816666
    }
    private val maxDistanceMeters = 100f // 100 meters radius

    init {
        verifyLocation()
    }

    @SuppressLint("MissingPermission")
    private fun verifyLocation() {
        viewModelScope.launch {
            try {
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).await()

                if (location != null) {
                    val userLocation = Location("user").apply {
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                    val distance = userLocation.distanceTo(schoolLocation)
                    _locationVerified.postValue(distance <= maxDistanceMeters)
                } else {
                    _locationVerified.postValue(false)
                }
            } catch (e: Exception) {
                _locationVerified.postValue(false)
            }
        }
    }

    fun refreshLocation() {
        verifyLocation()
    }
}