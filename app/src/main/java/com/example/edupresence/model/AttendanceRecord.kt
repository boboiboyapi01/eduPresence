package com.example.edupresence.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class AttendanceRecord(
    @PropertyName("recordId") val recordId: String = "",
    @PropertyName("sessionId") val sessionId: String = "",
    @PropertyName("studentId") val studentId: String = "",
    @PropertyName("timestamp") val timestamp: Date = Date(),
    @PropertyName("status") val status: String = "",
    @PropertyName("photoUrl") val photoUrl: String = "",
    @PropertyName("location") val location: String = "",
    @PropertyName("createdAt") val createdAt: Date = Date()
)