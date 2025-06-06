package com.example.edupresence.utils

import android.content.Context
import kotlin.math.*

class LocationUtils(private val context: Context) {

    fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadius = 6371000.0 // Earth radius in meters

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    fun isLocationWithinRadius(
        currentLat: Double, currentLon: Double,
        targetLat: Double, targetLon: Double,
        radiusInMeters: Double
    ): Boolean {
        val distance = calculateDistance(currentLat, currentLon, targetLat, targetLon)
        return distance <= radiusInMeters
    }
}