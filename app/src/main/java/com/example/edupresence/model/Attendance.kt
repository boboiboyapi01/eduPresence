package com.example.edupresence.model

data class Attendance(
    val id: String = "",
    val classId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val location: AttendanceLocation = AttendanceLocation(),
    val status: String = "present" // present, late, absent
) {
    data class AttendanceLocation(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0
    )
}