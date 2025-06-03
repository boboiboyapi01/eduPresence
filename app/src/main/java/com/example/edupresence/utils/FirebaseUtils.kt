package com.example.edupresence.utils

import com.example.edupresence.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FirebaseUtils {

    private val db = FirebaseFirestore.getInstance()

    // User operations
    fun getUser(userId: String, callback: (User?) -> Unit) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)?.copy(id = document.id)
                    callback(user)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    // Class operations
    fun getClass(classId: String, callback: (Class?) -> Unit) {
        db.collection("classes").document(classId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val classData = document.toObject(Class::class.java)?.copy(id = document.id)
                    callback(classData)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun getTeacherClasses(teacherId: String, callback: (List<Class>) -> Unit) {
        db.collection("classes")
            .whereEqualTo("teacherId", teacherId)
            .get()
            .addOnSuccessListener { documents ->
                val classes = documents.map { doc ->
                    doc.toObject(Class::class.java).copy(id = doc.id)
                }
                callback(classes)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun getStudentClasses(studentId: String, callback: (List<Class>) -> Unit) {
        db.collection("classes")
            .whereArrayContains("students", studentId)
            .get()
            .addOnSuccessListener { documents ->
                val classes = documents.map { doc ->
                    doc.toObject(Class::class.java).copy(id = doc.id)
                }
                callback(classes)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun updateClassSchedule(
        classId: String,
        schedule: Schedule,
        allowedLocation: Class.Location,
        callback: (Boolean) -> Unit
    ) {
        val updates = mapOf(
            "schedule" to schedule,
            "allowedLocation" to allowedLocation
        )

        db.collection("classes").document(classId)
            .update(updates)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // Attendance operations
    fun recordAttendance(attendance: Attendance, callback: (Boolean) -> Unit) {
        // Check if attendance already exists for today
        val today = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            .format(java.util.Date())

        db.collection("attendance")
            .whereEqualTo("classId", attendance.classId)
            .whereEqualTo("studentId", attendance.studentId)
            .get()
            .addOnSuccessListener { documents ->
                val todayAttendance = documents.find { doc ->
                    val attendanceData = doc.toObject(Attendance::class.java)
                    val attendanceDate = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                        .format(java.util.Date(attendanceData.timestamp))
                    attendanceDate == today
                }

                if (todayAttendance == null) {
                    // Create new attendance record
                    db.collection("attendance")
                        .add(attendance)
                        .addOnSuccessListener {
                            callback(true)
                        }
                        .addOnFailureListener {
                            callback(false)
                        }
                } else {
                    callback(false) // Already attended today
                }
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun getClassAttendanceData(classId: String, callback: (List<Attendance>) -> Unit) {
        db.collection("attendance")
            .whereEqualTo("classId", classId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val attendances = documents.map { doc ->
                    doc.toObject(Attendance::class.java).copy(id = doc.id)
                }
                callback(attendances)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun getStudentAttendanceHistory(
        studentId: String,
        classId: String,
        callback: (List<Attendance>) -> Unit
    ) {
        db.collection("attendance")
            .whereEqualTo("studentId", studentId)
            .whereEqualTo("classId", classId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val attendances = documents.map { doc ->
                    doc.toObject(Attendance::class.java).copy(id = doc.id)
                }
                callback(attendances)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }
}