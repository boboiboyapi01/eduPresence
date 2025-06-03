package com.example.edupresence.model

data class Class(
    val id: String = "",
    val name: String = "",
    val teacherId: String = "",
    val teacherName: String = "",
    val students: List<String> = listOf(), // Student IDs
    val schedule: Schedule = Schedule(),
    val allowedLocation: Location = Location()
) {
    data class Location(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val radius: Double = 100.0 // meters
    )
}