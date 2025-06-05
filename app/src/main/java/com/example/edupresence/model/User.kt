package com.example.edupresence.model

data class User(
    val id: String = "",
    val email: String = "",
    val role: String = "",
    val name: String = "",
    val classId: Int = 0,
    val faceEmbedding: String? = null
)