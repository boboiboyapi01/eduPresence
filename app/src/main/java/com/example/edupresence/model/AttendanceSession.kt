package com.example.edupresence.model

data class AttendanceSession(
    val id: String,
    val sessionId: String,
    val subjectName: String,
    val isActive: Boolean,
    val date: String,
    val teacherId: String,
    val name: String? = null
)
