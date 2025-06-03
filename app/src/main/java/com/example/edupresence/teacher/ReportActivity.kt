package com.example.edupresence.teacher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edupresence.R
import com.example.edupresence.model.Attendance
import com.example.edupresence.utils.FirebaseUtils
import kotlinx.android.synthetic.main.activity_report.*

class ReportActivity : AppCompatActivity() {

    private lateinit var firebaseUtils: FirebaseUtils
    private lateinit var attendanceAdapter: AttendanceAdapter
    private val attendanceList = mutableListOf<Attendance>()
    private var classId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        classId = intent.getStringExtra("class_id") ?: ""
        firebaseUtils = FirebaseUtils()

        setupRecyclerView()
        loadAttendanceData()
    }

    private fun setupRecyclerView() {
        attendanceAdapter = AttendanceAdapter(attendanceList)

        rvAttendance.apply {
            layoutManager = LinearLayoutManager(this@ReportActivity)
            adapter = attendanceAdapter
        }
    }

    private fun loadAttendanceData() {
        firebaseUtils.getClassAttendanceData(classId) { attendances ->
            attendanceList.clear()
            attendanceList.addAll(attendances.sortedByDescending { it.timestamp })
            attendanceAdapter.notifyDataSetChanged()

            updateStatistics(attendances)
        }
    }

    private fun updateStatistics(attendances: List<Attendance>) {
        val totalSessions = attendances.groupBy {
            java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                .format(java.util.Date(it.timestamp))
        }.size

        val presentCount = attendances.count { it.status == "present" }
        val lateCount = attendances.count { it.status == "late" }

        tvTotalSessions.text = "Total Pertemuan: $totalSessions"
        tvPresentCount.text = "Hadir: $presentCount"
        tvLateCount.text = "Terlambat: $lateCount"
    }
}