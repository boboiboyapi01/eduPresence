package com.example.edupresence.student

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.edupresence.R
import com.example.edupresence.model.Attendance
import com.example.edupresence.model.Class
import com.example.edupresence.utils.FirebaseUtils
import com.example.edupresence.utils.LocationUtils
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_attendance.*
import java.text.SimpleDateFormat
import java.util.*

class AttendanceActivity : AppCompatActivity() {

    private lateinit var firebaseUtils: FirebaseUtils
    private lateinit var locationUtils: LocationUtils
    private var classId: String = ""
    private var currentClass: Class? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        classId = intent.getStringExtra("class_id") ?: ""
        firebaseUtils = FirebaseUtils()
        locationUtils = LocationUtils(this)

        loadClassInfo()
        setupClickListeners()
    }

    private fun loadClassInfo() {
        firebaseUtils.getClass(classId) { classData ->
            currentClass = classData
            classData?.let {
                tvClassName.text = it.name
                tvTeacherName.text = "Pengajar: ${it.teacherName}"

                val schedule = it.schedule
                val dayName = getDayName(schedule.dayOfWeek)
                tvSchedule.text = "$dayName, ${schedule.startTime} - ${schedule.endTime}"
                tvLocation.text = "Lokasi: ${schedule.location}"

                checkAttendanceEligibility(it)
            }
        }
    }

    private fun setupClickListeners() {
        btnAttendNow.setOnClickListener {
            checkLocationAndAttend()
        }

        btnViewHistory.setOnClickListener {
            val intent = android.content.Intent(this, HistoryActivity::class.java)
            intent.putExtra("class_id", classId)
            startActivity(intent)
        }
    }

    private fun checkAttendanceEligibility(classData: Class) {
        val currentTime = Calendar.getInstance()
        val currentDay = currentTime.get(Calendar.DAY_OF_WEEK)
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentTime.get(Calendar.MINUTE)

        val schedule = classData.schedule
        val classDay = if (schedule.dayOfWeek == 7) 1 else schedule.dayOfWeek + 1 // Convert to Calendar format

        val startTimeParts = schedule.startTime.split(":")
        val endTimeParts = schedule.endTime.split(":")
        val startHour = startTimeParts[0].toInt()
        val startMinute = startTimeParts[1].toInt()
        val endHour = endTimeParts[0].toInt()
        val endMinute = endTimeParts[1].toInt()

        val currentTimeInMinutes = currentHour * 60 + currentMinute
        val startTimeInMinutes = startHour * 60 + startMinute
        val endTimeInMinutes = endHour * 60 + endMinute

        if (currentDay == classDay &&
            currentTimeInMinutes >= startTimeInMinutes &&
            currentTimeInMinutes <= endTimeInMinutes) {
            btnAttendNow.isEnabled = true
            tvAttendanceStatus.text = "Anda dapat melakukan absensi sekarang"
        } else {
            btnAttendNow.isEnabled = false
            tvAttendanceStatus.text = "Absensi hanya dapat dilakukan pada jadwal yang telah ditentukan"
        }
    }

    private fun checkLocationAndAttend() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST)
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                checkLocationValidity(it)
            } ?: run {
                Toast.makeText(this, "Tidak dapat mendapatkan lokasi. Coba lagi.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLocationValidity(currentLocation: Location) {
        currentClass?.let { classData ->
            val allowedLocation = classData.allowedLocation
            val distance = locationUtils.calculateDistance(
                currentLocation.latitude, currentLocation.longitude,
                allowedLocation.latitude, allowedLocation.longitude
            )

            if (distance <= allowedLocation.radius) {
                recordAttendance(currentLocation)
            } else {
                Toast.makeText(this,
                    "Anda tidak berada di lokasi sekolah. Jarak: ${distance.toInt()}m. Coba lagi.",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun recordAttendance(location: Location) {
        val studentId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firebaseUtils.getUser(studentId) { user ->
            user?.let {
                val attendance = Attendance(
                    classId = classId,
                    studentId = studentId,
                    studentName = it.name,
                    timestamp = System.currentTimeMillis(),
                    location = Attendance.AttendanceLocation(location.latitude, location.longitude),
                    status = determineAttendanceStatus()
                )

                firebaseUtils.recordAttendance(attendance) { success ->
                    if (success) {
                        Toast.makeText(this, "Absensi berhasil dicatat!", Toast.LENGTH_SHORT).show()
                        btnAttendNow.isEnabled = false
                        tvAttendanceStatus.text = "Anda sudah melakukan absensi hari ini"
                    } else {
                        Toast.makeText(this, "Gagal mencatat absensi. Coba lagi.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun determineAttendanceStatus(): String {
        currentClass?.let { classData ->
            val currentTime = Calendar.getInstance()
            val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
            val currentMinute = currentTime.get(Calendar.MINUTE)
            val currentTimeInMinutes = currentHour * 60 + currentMinute

            val startTimeParts = classData.schedule.startTime.split(":")
            val startTimeInMinutes = startTimeParts[0].toInt() * 60 + startTimeParts[1].toInt()

            // Consider late if more than 15 minutes after start time
            return if (currentTimeInMinutes > startTimeInMinutes + 15) "late" else "present"
        }
        return "present"
    }

    private fun getDayName(dayOfWeek: Int): String {
        return when(dayOfWeek) {
            1 -> "Senin"
            2 -> "Selasa"
            3 -> "Rabu"
            4 -> "Kamis"
            5 -> "Jumat"
            6 -> "Sabtu"
            7 -> "Minggu"
            else -> "Unknown"
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationAndAttend()
                } else {
                    Toast.makeText(this, "Izin lokasi diperlukan untuk absensi",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}