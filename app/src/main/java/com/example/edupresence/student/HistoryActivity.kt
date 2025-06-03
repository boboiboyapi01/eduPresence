package com.example.edupresence.student

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edupresence.R
import com.example.edupresence.model.Attendance
import com.example.edupresence.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var firebaseUtils: FirebaseUtils
    private lateinit var attendanceAdapter: StudentAttendanceAdapter
    private val attendanceList = mutableListOf<Attendance>()
    private var classId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        classId = intent.getStringExtra("class_id") ?: ""
        firebaseUtils = FirebaseUtils()

        setupRecyclerView()
        loadAttendanceHistory()
    }

    private fun setupRecyclerView() {
        attendanceAdapter = StudentAttendanceAdapter(attendanceList)

        rvAttendanceHistory.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = attendanceAdapter
        }
    }

    private fun loadAttendanceHistory() {
        val studentId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firebaseUtils.getStudentAttendanceHistory(studentId, classId) { attendances ->
            attendanceList.clear()
            attendanceList.addAll(attendances.sortedByDescending { it.timestamp })
            attendanceAdapter.notifyDataSetChanged()

            updateStatistics(attendances)
        }
    }

    private fun updateStatistics(attendances: List<Attendance>) {
        val presentCount = attendances.count { it.status == "present" }
        val lateCount = attendances.count { it.status == "late" }
        val totalAttendance = attendances.size

        tvPresentCount.text = "Hadir: $presentCount"
        tvLateCount.text = "Terlambat: $lateCount"
        tvTotalAttendance.text = "Total Kehadiran: $totalAttendance"

        val attendancePercentage = if (totalAttendance > 0) {
            ((presentCount + lateCount).toFloat() / totalAttendance * 100).toInt()
        } else 0

        tvAttendancePercentage.text = "Persentase Kehadiran: $attendancePercentage%"
    }
}