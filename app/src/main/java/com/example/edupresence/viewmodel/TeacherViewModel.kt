package com.example.edupresence.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupresence.model.Class
import com.example.edupresence.model.AttendanceSession
import com.example.edupresence.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import io.github.jan.supabase.postgrest.query.eq

class TeacherViewModel(
    private val repository: UserRepository,
    private val firestore: FirebaseFirestore,
    private val supabase: Postgrest,
    private val teacherId: String
) : ViewModel() {

    private val _classes = MutableLiveData<List<Class>>()
    val classes: LiveData<List<Class>> = _classes

    private val _attendanceSessions = MutableLiveData<List<AttendanceSession>>()
    val attendanceSessions: LiveData<List<AttendanceSession>> = _attendanceSessions

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        loadClasses()
    }

    private fun loadClasses() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val supabaseClasses = supabase.from("classes")
                    .select {
                        eq(column = "teacherId", value = teacherId)
                    }
                    .decodeList<Class>()
                _classes.postValue(supabaseClasses)
                _errorMessage.postValue(null)
            } catch (e: Exception) {
                _classes.postValue(emptyList())
                _errorMessage.postValue(e.message ?: "Failed to load classes from Supabase")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun loadTeacherDashboard() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val sessions = firestore.collection("attendance_sessions")
                    .whereEqualTo("teacherId", teacherId)
                    .get()
                    .await()
                    .toObjects(AttendanceSession::class.java)
                _attendanceSessions.postValue(sessions)
                _errorMessage.postValue(null)
            } catch (e: Exception) {
                _attendanceSessions.postValue(emptyList())
                _errorMessage.postValue(e.message ?: "Failed to load sessions")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}