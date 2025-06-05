package com.example.edupresence.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupresence.model.AttendanceRecord
import com.example.edupresence.repository.AttendanceRepository
import kotlinx.coroutines.launch

class AttendanceHistoryViewModel : ViewModel() {
    private val repository = AttendanceRepository()
    private val _attendanceRecords = MutableLiveData<List<AttendanceRecord>>()
    val attendanceRecords: LiveData<List<AttendanceRecord>> = _attendanceRecords

    fun loadAttendanceHistory() {
        viewModelScope.launch {
            val result = repository.getRecordsForStudent("currentStudentId")
            if (result.isSuccess) _attendanceRecords.value = result.getOrNull()
        }
    }
}