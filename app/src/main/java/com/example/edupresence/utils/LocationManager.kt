package com.example.edupresence.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

class LocationManager(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Result<String> {
        return try {
            val location = fusedLocationClient.lastLocation.await()
            if (location != null) {
                val schoolLat = 0.0 // Replace with actual school coordinates
                val schoolLon = 0.0
                val distance = FloatArray(1)
                Location.distanceBetween(schoolLat, schoolLon, location.latitude, location.longitude, distance)

                if (distance[0] < 100) { // 100 meters radius
                    Result.success("${location.latitude},${location.longitude}")
                } else {
                    Result.failure(Exception("Outside school area"))
                }
            } else {
                Result.failure(Exception("Location unavailable"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}