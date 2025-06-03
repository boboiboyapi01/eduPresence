package com.example.edupresence.teacher

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.edupresence.R
import com.example.edupresence.model.Class
import com.example.edupresence.utils.FirebaseUtils
import kotlinx.android.synthetic.main.activity_class_management.*

class ClassManagementActivity : AppCompatActivity() {

    private lateinit var firebaseUtils: FirebaseUtils
    private var classId: String = ""
    private var currentClass: Class? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_management)

        classId = intent.getStringExtra("class_id") ?: ""
        firebaseUtils = FirebaseUtils()

        loadClassData()
        setupClickListeners()
    }

    private fun loadClassData() {
        firebaseUtils.getClass(classId) { classData ->
            currentClass = classData
            classData?.let {
                tvClassName.text = it.name
                tvStudentCount.text = "${it.students.size} siswa"
            }
        }
    }

    private fun setupClickListeners() {
        btnManageStudents.setOnClickListener {
            // Navigate to student management
            val intent = Intent(this, StudentManagementActivity::class.java)
            intent.putExtra("class_id", classId)
            startActivity(intent)
        }

        btnManageSchedule.setOnClickListener {
            val intent = Intent(this, ScheduleManagementActivity::class.java)
            intent.putExtra("class_id", classId)
            startActivity(intent)
        }

        btnViewReports.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            intent.putExtra("class_id", classId)
            startActivity(intent)
        }

        btnExportData.setOnClickListener {
            exportAttendanceData()
        }
    }

    private fun exportAttendanceData() {
        firebaseUtils.getClassAttendanceData(classId) { attendanceList ->
            // Export to CSV or Excel
            val csvData = generateCSV(attendanceList)
            // Save or share the file
            shareCSVFile(csvData)
        }
    }

    private fun generateCSV(attendanceList: List<com.example.edupresence.model.Attendance>): String {
        val header = "Tanggal,Nama Siswa,Status,Waktu\n"
        val rows = attendanceList.joinToString("\n") { attendance ->
            val date = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                .format(java.util.Date(attendance.timestamp))
            val time = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date(attendance.timestamp))
            "$date,${attendance.studentName},${attendance.status},$time"
        }
        return header + rows
    }

    private fun shareCSVFile(csvData: String) {
        // Implementation for sharing CSV file
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, csvData)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Data Absensi ${currentClass?.name}")
        startActivity(Intent.createChooser(intent, "Bagikan Data Absensi"))
    }
}