package com.example.edupresence.teacher

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.edupresence.R
import com.example.edupresence.model.Class
import com.example.edupresence.model.Schedule
import com.example.edupresence.utils.FirebaseUtils
import kotlinx.android.synthetic.main.activity_schedule_management.*

class ScheduleManagementActivity : AppCompatActivity() {

    private lateinit var firebaseUtils: FirebaseUtils
    private var classId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_management)

        classId = intent.getStringExtra("class_id") ?: ""
        firebaseUtils = FirebaseUtils()

        loadCurrentSchedule()
        setupClickListeners()
    }

    private fun loadCurrentSchedule() {
        firebaseUtils.getClass(classId) { classData ->
            classData?.let {
                val schedule = it.schedule
                spinnerDay.setSelection(schedule.dayOfWeek - 1)
                etStartTime.setText(schedule.startTime)
                etEndTime.setText(schedule.endTime)
                etLocation.setText(schedule.location)
                etLatitude.setText(it.allowedLocation.latitude.toString())
                etLongitude.setText(it.allowedLocation.longitude.toString())
                etRadius.setText(it.allowedLocation.radius.toString())
            }
        }
    }

    private fun setupClickListeners() {
        btnSaveSchedule.setOnClickListener {
            saveSchedule()
        }

        btnGetCurrentLocation.setOnClickListener {
            getCurrentLocation()
        }
    }

    private fun saveSchedule() {
        val dayOfWeek = spinnerDay.selectedItemPosition + 1
        val startTime = etStartTime.text.toString()
        val endTime = etEndTime.text.toString()
        val location = etLocation.text.toString()
        val latitude = etLatitude.text.toString().toDoubleOrNull() ?: 0.0
        val longitude = etLongitude.text.toString().toDoubleOrNull() ?: 0.0
        val radius = etRadius.text.toString().toDoubleOrNull() ?: 100.0

        if (validateScheduleInput(startTime, endTime, location)) {
            val schedule = Schedule(dayOfWeek, startTime, endTime, location)
            val allowedLocation = Class.Location(latitude, longitude, radius)

            firebaseUtils.updateClassSchedule(classId, schedule, allowedLocation) { success ->
                if (success) {
                    Toast.makeText(this, "Jadwal berhasil disimpan", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Gagal menyimpan jadwal", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateScheduleInput(startTime: String, endTime: String, location: String): Boolean {
        if (startTime.isEmpty()) {
            etStartTime.error = "Waktu mulai tidak boleh kosong"
            return false
        }
        if (endTime.isEmpty()) {
            etEndTime.error = "Waktu selesai tidak boleh kosong"
            return false
        }
        if (location.isEmpty()) {
            etLocation.error = "Lokasi tidak boleh kosong"
            return false
        }
        return true
    }

    private fun getCurrentLocation() {
        // Implementation for getting current location
        // This would use LocationUtils to get current GPS coordinates
    }
}