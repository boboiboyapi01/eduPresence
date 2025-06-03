package com.example.edupresence.model

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val role: String = "", // "teacher" or "student"
    val faceEmbedding: String = "" // Base64 encoded face data
)