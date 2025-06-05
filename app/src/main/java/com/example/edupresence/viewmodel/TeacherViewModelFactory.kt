package com.example.edupresence.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.edupresence.EduPresenceApplication
import com.example.edupresence.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.postgrest.postgrest

class TeacherViewModelFactory(
    private val application: Application,
    private val repository: UserRepository,
    private val teacherId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeacherViewModel::class.java)) {
            val firestore = FirebaseFirestore.getInstance()
            val supabase = (application as EduPresenceApplication).supabase.postgrest
            return TeacherViewModel(repository, firestore, supabase, teacherId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}