package com.example.edupresence.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupresence.model.AttendanceSession
import com.example.edupresence.repository.AttendanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudentViewModel(
    private val repository: AttendanceRepository,
    private val teacherId: String // Parameter untuk ID
) : ViewModel() {

    private val _schedule = MutableLiveData<List<AttendanceSession>>()
    val schedule: LiveData<List<AttendanceSession>> = _schedule

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadStudentDashboard() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getSessionsForTeacher(teacherId)
                _schedule.postValue(
                    if (result.isSuccess) {
                        result.getOrNull() ?: emptyList()
                    } else {
                        emptyList()
                    }
                )
                _errorMessage.postValue(result.exceptionOrNull()?.message)
            } catch (e: Exception) {
                _schedule.postValue(emptyList())
                _errorMessage.postValue(e.message ?: "Failed to load sessions")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    // Bersihkan error message
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}