package com.example.edupresence.model

import com.google.firebase.firestore.PropertyName

data class Class(
    val id: Int,
    val name: String,
    val teacherId: String,
    val allowedLocation: Location? = null,
    val schedule: List<String> = emptyList(),
    val students: List<String> = emptyList(),
    val teacherName: String
) {
    data class Location(
        @PropertyName("latitude") val latitude: Double = 0.0,
        @PropertyName("longitude") val longitude: Double = 0.0,
        @PropertyName("radius") val radius: Int = 0
    )
}