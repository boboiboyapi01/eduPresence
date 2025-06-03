package com.example.edupresence.model

data class Schedule(
    val dayOfWeek: Int = 1, // 1-7 (Monday-Sunday)
    val startTime: String = "08:00", // HH:mm format
    val endTime: String = "09:30", // HH:mm format
    val location: String = ""
)